package main

import (
	"flag"
	"fmt"
	"strings"

	"github.com/GuanceCloud/cliutils/logger"

	// "github.com/confluentinc/confluent-kafka-go/kafka"
	"os"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

var (
	log       = logger.DefaultSLogger("kafkamq_custom")
	kafkaAddr string
	topics    string
	user      string
	pw        string
	stop      = make(chan os.Signal, 1)
)

func main() {
	logger.InitRoot(&logger.Option{Path: "./log", Level: "debug", Flags: 0})

	flag.StringVar(&kafkaAddr, "kafkaAddrs", "", "kafka addrs 10.300.14.1:9092,10.200.14.2:9092")
	flag.StringVar(&topics, "topics", "", "topic,topic2,topic3")
	flag.StringVar(&user, "user", "", "username")
	flag.StringVar(&pw, "pw", "", "pw")
	flag.Parse()

	file, err := os.OpenFile("./topic.msg", os.O_CREATE|os.O_RDWR|os.O_APPEND, 0755)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer func() { file.Close() }()

	kafkaTopics := strings.Split(topics, ",")
	notnilTopic := make([]string, 0)
	for _, topic := range kafkaTopics {
		if topic != "" {
			notnilTopic = append(notnilTopic, topic)
		}
	}
	config := &kafka.ConfigMap{
		"bootstrap.servers":  kafkaAddr,
		"sasl.username":      user,
		"sasl.password":      pw,
		"security.protocol":  "SASL_PLAINTEXT",
		"sasl.mechanism":     "PLAIN",
		"group.id":           "my-group1",
		"auto.offset.reset":  "earliest",
		"enable.auto.commit": false,
	}

	//adminClient, err := kafka.NewAdminClient(config)
	//if err != nil {
	//	log.Errorf("NewAdminClient err=%v", err)
	//	return
	//}
	//defer adminClient.Close()
	//topics, err := adminClient.GetMetadata(nil, true, 5000)
	//if err != nil {
	//	log.Errorf("GetMetadata err=%v", err)
	//	return
	//}
	//
	//subTopics := make([]string, 0)
	//fmt.Println("Available topics:")
	//for _, topic := range topics.Topics {
	//	fmt.Println(topic.Topic)
	//	subTopics = append(subTopics, topic.Topic)
	//}

	consumer, err := kafka.NewConsumer(config)
	if err != nil {
		log.Errorf("NewConsumer err=%v", err)
		return
	}
	defer consumer.Close()

	err = consumer.SubscribeTopics(notnilTopic, nil)
	if err != nil {
		log.Errorf("SubscribeTopics err=%v", err)
		return
	}

	for {
		message, err := consumer.ReadMessage(-1)
		if err == nil {
			file.WriteString(fmt.Sprintf("Received topic:%s message: %s\n", *message.TopicPartition.Topic, string(message.Value)))
		} else {
			fmt.Printf("Error: %v (%v)\n", err, message)
		}
	}
}

/*
docker run -d \
    --name kafka \
    -p 9093:9093 \
    -e KAFKA_LISTENERS=SASL_PLAINTEXT://0.0.0.0:9093 \
    -e KAFKA_ADVERTISED_LISTENERS=SASL_PLAINTEXT://localhost:9093 \
    -e KAFKA_AUTHORIZER_CLASS_NAME=kafka.security.auth.SimpleAclAuthorizer \
    -e KAFKA_SUPER_USERS=User:admin \
    -e KAFKA_ALLOW_EVERYONE_IF_NO_ACL_FOUND=false \
    -e KAFKA_SASL_MECHANISM_INTER_BROKER_PROTOCOL=PLAIN \
    -e KAFKA_SASL_ENABLED_MECHANISMS=PLAIN \
    -e KAFKA_SECURITY_INTER_BROKER_PROTOCOL=SASL_PLAINTEXT \
    -e KAFKA_OPTS='' \
    -e KAFKA_SASL_JAAS_CONFIG='org.apache.kafka.common.security.plain.PlainLoginModule required username="user" password="password";' \
    -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
    confluentinc/cp-kafka:5.5.2

cd ~/opt/kafka
./bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
  496  ps -ef|grep zoo
  497  ./bin/kafka-server-start.sh -daemon config/server.properties
  498  ps -ef|grep kafka

*/
