package main

import (
	"bufio"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"os"
	"sync"
	"time"

	"github.com/GuanceCloud/cliutils/logger"
	"github.com/Shopify/sarama"
)

var (
	log        = logger.DefaultSLogger("kafkamq_custom")
	jsonConfig = "./config.json"
)

type SaslConf struct {
	Enable        bool   `json:"enable"`
	SaslMechanism string `json:"sasl_mechanism"` // "PLAIN"
	Username      string `json:"username"`       // "user"
	Password      string `json:"password"`       // pw
}

type kafkaConfig struct {
	Addrs   []string  `json:"addrs"`
	Topics  []string  `json:"topics"`
	Version string    `json:"version"`
	SASL    *SaslConf `json:"sasl"`
}

func writeToFile() {
	//创建一个新文件，写入内容 5 句 “http://c.biancheng.net/golang/”
	file, err := os.OpenFile(jsonConfig, os.O_WRONLY|os.O_CREATE, 0666)
	if err != nil {
		fmt.Println("文件打开失败", err)
	}
	//及时关闭file句柄
	defer file.Close()
	//写入文件时，使用带缓存的 *Writer
	write := bufio.NewWriter(file)

	write.WriteString(`
{
	"addrs": ["10.200.14.226:9092"],
	"topics": ["apm", "apm01"],
	"version": "2.5.1",
	"sasl": {
		"enable": true,
		"sasl_mechanism": "PLAIN",
		"username": "username",
		"password": "pw"
	}
}`)

	//Flush将缓存的文件真正写入到文件中
	write.Flush()
}

func main() {
	// 先判断这个文件是否存在，不存在创建后直接退出
	_, err := os.Stat("./config.json")
	if err != nil {
		writeToFile()
		fmt.Println("create ./config.json file,exit")
		return
	}
	bts, err := os.ReadFile(jsonConfig)
	if err != nil {
		fmt.Println(err)
		return
	}
	kafkaConf := &kafkaConfig{}
	err = json.Unmarshal(bts, kafkaConf)
	if err != nil {
		fmt.Println(err)
		return
	}

	logger.InitRoot(&logger.Option{Path: "./log", Level: "debug", Flags: 0})

	config := sarama.NewConfig()
	config.Consumer.Return.Errors = true
	version, err := sarama.ParseKafkaVersion(kafkaConf.Version)
	if err != nil {
		fmt.Println(err)
		return
	}
	config.Version = version                              // specify appropriate version
	config.Consumer.Offsets.Initial = sarama.OffsetNewest // 未找到组消费位移的时候从哪边开始消费

	//config.Consumer.Group.Rebalance.GroupStrategies = []sarama.BalanceStrategy{sarama.BalanceStrategyRoundRobin, sarama.BalanceStrategyRange}
	config.Consumer.Offsets.Retry.Max = 10

	name, _ := os.Hostname()
	config.ClientID = name

	if kafkaConf.SASL.Enable {
		config.Net.SASL.Enable = true
		config.Net.SASL.Password = kafkaConf.SASL.Username
		config.Net.SASL.User = kafkaConf.SASL.Password
		config.Net.SASL.Mechanism = "PLAIN"
		config.Net.SASL.Version = sarama.SASLHandshakeV1
	}
	file, err := os.OpenFile("./topic.msg", os.O_CREATE|os.O_RDWR|os.O_APPEND, 0755)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer file.Close()
	custom := &Custom{
		GroupID: "datakit",
		topics:  kafkaConf.Topics,
		stop:    make(chan struct{}, 1),
		output:  file,
	}

	custom.SaramaConsumerGroup(kafkaConf.Addrs, config)
	sarama.Logger = custom
}

type Custom struct {
	GroupID string `toml:"group_id"`
	stop    chan struct{}
	topics  []string
	output  io.WriteCloser
}

func (c *Custom) Print(v ...interface{}) {
	log.Debug(v)
}

func (c *Custom) Printf(format string, v ...interface{}) {
	log.Debugf(format, v)
}

func (c *Custom) Println(v ...interface{}) {
	log.Debug(v)
}

func (c *Custom) SaramaConsumerGroup(addrs []string, config *sarama.Config) {
	log = logger.SLogger("kafkamq_custom")
	sarama.Logger = c
	c.stop = make(chan struct{}, 1)
	var group sarama.ConsumerGroup
	var err error
	var count int
	for {
		if count == 10 {
			log.Errorf("can not connect to kafka, consrmer exit")
			return
		}

		group, err = sarama.NewConsumerGroup(addrs, c.GroupID, config)
		if errors.Is(err, sarama.ErrOutOfBrokers) {
			group, err = UseSupportedVersions(addrs, c.GroupID, config)
			if group != nil {
				break
			}
		}
		if err != nil {
			log.Errorf("new group is err,restart count=%d ,addrs=[%v] err=%v", count, addrs, err)
			time.Sleep(time.Second * 5)
			count++
			continue
		}
		break
	}

	// Iterate over consumer sessions.
	ctx, cancel := context.WithCancel(context.Background())

	handler := &consumerGroupHandler{ready: make(chan bool), output: c.output}
	wg := &sync.WaitGroup{}
	wg.Add(1)

	log.Infof("custom is run with topics =[%+v]", c.topics)
	go func() {
		defer wg.Done()
		for {
			// `Consume` should be called inside an infinite loop, when a
			// server-side rebalance happens, the consumer session will need to be
			// recreated to get the new claims.
			if err := group.Consume(ctx, c.topics, handler); err != nil {
				log.Errorf("Error from consumer: %v", err)
			}
			// check if context was canceled, signaling that the consumer should stop.
			if ctx.Err() != nil {
				return
			}
			time.Sleep(time.Second) // 防止频率太快 造成的日志太大.
			handler.ready = make(chan bool)
		}
	}()

	<-handler.ready // wait till the consumer has been set up
	log.Infof("Sarama consumer up and running!...")

	select {
	case <-ctx.Done():
		log.Infof("terminating: context canceled")
	case <-c.stop:
		log.Infof("consumer stop")
	}

	cancel()
	wg.Wait()
	if err = group.Close(); err != nil {
		log.Errorf("Error closing client: %v", err)
	}
}

func (c *Custom) Stop() {
	if c.stop != nil {
		c.stop <- struct{}{}
	}
}

// UseSupportedVersions :用户不提供版本信息，暴力破解版本.
func UseSupportedVersions(addrs []string, groupID string, config *sarama.Config) (sarama.ConsumerGroup, error) {
	var err error
	var group sarama.ConsumerGroup
	for i := len(sarama.SupportedVersions) - 1; i >= 0; i-- {
		config.Version = sarama.SupportedVersions[i]
		group, err = sarama.NewConsumerGroup(addrs, groupID, config)
		if err != nil {
			log.Errorf("new group is err,restart count=%d ,addrs=[%v] err=%v", i, addrs, err)
			time.Sleep(time.Second * 10)
		} else {
			break
		}
	}
	return group, err
}

type consumerGroupHandler struct {
	// 暂时先支持 log 和 metric 两种数据.
	ready  chan bool
	output io.WriteCloser
}

func (c *consumerGroupHandler) Setup(session sarama.ConsumerGroupSession) error {
	// Mark the consumer as ready
	close(c.ready)
	return nil
}

func (c *consumerGroupHandler) Cleanup(sarama.ConsumerGroupSession) error { return nil }

// ConsumeClaim must start a consumer loop of ConsumerGroupClaim's Messages().
func (c *consumerGroupHandler) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for {
		select {
		case msg := <-claim.Messages():
			log.Debugf("partition=%d", claim.Partition())
			if msg == nil {
				return nil
			}
			log.Debugf("message topic =%s", msg.Topic)
			session.MarkMessage(msg, "")

			c.feedMsg(msg)
		case <-session.Context().Done():
			log.Infof("session context is close,err=%v", session.Context().Err())
			return nil
		}
	}
}

func (c *consumerGroupHandler) feedMsg(msg *sarama.ConsumerMessage) {
	c.output.Write([]byte(fmt.Sprintf("topic=%s |", msg.Topic)))
	c.output.Write(msg.Value)
	c.output.Write([]byte("\n"))
}
