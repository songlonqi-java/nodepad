package main

import (
	"context"
	"errors"
	"flag"
	"fmt"
	"github.com/GuanceCloud/cliutils/logger"
	"github.com/Shopify/sarama"
	"io"
	"math/rand"
	"os"
	"strings"
	"sync"
	"time"
)

var (
	log       = logger.DefaultSLogger("kafkamq_custom")
	kafkaAddr string
	topics    string
	stop      = make(chan os.Signal, 1)
)

func main() {
	logger.InitRoot(&logger.Option{Path: "./log", Level: "debug", Flags: 0})

	flag.StringVar(&kafkaAddr, "kafkaAddrs", "", "kafka addrs 10.300.14.1:9092,10.200.14.2:9092")
	flag.StringVar(&topics, "topics", "", "topic,topic2,topic3")
	flag.Parse()
	if kafkaAddr == "" || topics == "" {
		fmt.Println("kafkaAddrs is nil  or  topics is nil")
		return
	}
	file, err := os.OpenFile("./topic.msg", os.O_CREATE|os.O_RDWR|os.O_APPEND, 0755)
	if err != nil {
		fmt.Println(err)
		return
	}

	custom := &Custom{
		GroupID: "datakit",
		stop:    make(chan struct{}, 1),
		output:  file,
	}
	addrs := strings.Split(kafkaAddr, ",")
	custom.SaramaConsumerGroup(addrs)
	file.Close()
}

type Custom struct {
	GroupID string `toml:"group_id"`
	stop    chan struct{}
	output  io.WriteCloser
}

func (c *Custom) SaramaConsumerGroup(addrs []string) {
	config := sarama.NewConfig()
	config.Consumer.Return.Errors = false
	config.Version = sarama.V2_1_1_0                      // specify appropriate version
	config.Consumer.Offsets.Initial = sarama.OffsetOldest // 未找到组消费位移的时候从哪边开始消费

	config.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategyRoundRobin
	config.Consumer.Offsets.Retry.Max = 10
	name, _ := os.Hostname()
	config.ClientID = name

	log = logger.SLogger("kafkamq_custom")

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
			time.Sleep(time.Second * 10)
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
	kafkaTopics := strings.Split(topics, ",")
	notnilTopic := make([]string, 0)
	for _, topic := range kafkaTopics {
		if topic != "" {
			notnilTopic = append(notnilTopic, topic)
		}
	}
	log.Infof("custom is run with topics =[%+v]", topics)
	go func() {
		defer wg.Done()
		for {
			// `Consume` should be called inside an infinite loop, when a
			// server-side rebalance happens, the consumer session will need to be
			// recreated to get the new claims.
			if err := group.Consume(ctx, notnilTopic, handler); err != nil {
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
	case <-stop:

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

type sampler struct {
	rate float64
}

func (s *sampler) sample() bool {
	num := rand.Intn(10) //nolint
	return num < int(s.rate*10)
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
	c.output.Write([]byte(fmt.Sprintf("topic=%s", msg.Topic)))
	c.output.Write(msg.Value)
	c.output.Write([]byte("\n"))
}
