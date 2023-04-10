package main

import (
	"encoding/json"
	"fmt"
	"testing"

	"github.com/Shopify/sarama"
	"github.com/confluentinc/confluent-kafka-go/kafka"
)

func TestCustom_SaramaConsumerGroup(t *testing.T) {
	// Set up Kafka connection.
	topic := "test_topic"
	brokerAddr := []string{"10.200.6.16:9092"}

	config := sarama.NewConfig()
	config.Producer.RequiredAcks = sarama.WaitForAll
	config.Producer.Retry.Max = 5
	config.Producer.Return.Successes = true

	config.Net.SASL.Enable = true
	config.Net.SASL.Version = sarama.SASLHandshakeV1
	config.Net.SASL.Password = "producerpwd"
	config.Net.SASL.User = "producer"
	config.Net.SASL.Mechanism = sarama.SASLTypePlaintext

	producer, err := sarama.NewSyncProducer(brokerAddr, config)
	if err != nil {
		t.Fatalf("Failed to create Kafka producer: %v", err)
	}
	defer producer.Close()

	// Send message to Kafka.
	msg := &sarama.ProducerMessage{
		Topic: topic,
		Value: sarama.StringEncoder("hello world"),
	}

	partition, offset, err := producer.SendMessage(msg)
	if err != nil {
		t.Fatalf("Failed to send message to Kafka: %v", err)
	}

	fmt.Printf("Message sent successfully. Partition: %d, Offset: %d\n", partition, offset)
}

func TestCustom_NoSASL(t *testing.T) {

	msgstr := `[{
	"@timestamp": "2023-03-26T17:17:09.416Z",
	"@metadata": {
		"beat": "filebeat",
		"type": "doc",
		"version": "6.1.1",
		"topic": "logstash-test"
	},
	"fields": {
		"module": "hw-nacos",
		"productline": "nacos",
		"env": "uat"
	},
	"k8s_pod_namespace": "nacos",
	"index": "logstash-test",
	"source": "/host/home/logs/core-auth.log",
	"offset": 54135810,
	"prospector": {
		"type": "log"
	},
	"k8s_pod": "nacos-0",
	"k8s_node_name": "10.136.130.104",
	"beat": {
		"hostname": "log-pilot-tprrp",
		"version": "6.1.1",
		"name": "log-pilot-tprrp"
	},
	"message": "2023-03-27 01:17:08,969 DEBUG auth start, request: GET /nacos/v1/ns/instance/list\n",
	"docker_container": "k8s_k8snacos_nacos-0_nacos_b8d95fe8-9788-4834-9fc6-2225f522acf7_0",
	"topic": "logstash-test",
	"k8s_container_name": "k8snacos"
},
{
	"@timestamp": "2023-03-26T17:17:09.416Z",
	"@metadata": {
		"beat": "filebeat",
		"type": "doc",
		"version": "6.1.1",
		"topic": "logstash-test"
	},
	"fields": {
		"module": "hw-nacos",
		"productline": "nacos",
		"env": "uat"
	},
	"k8s_pod_namespace": "nacos",
	"index": "logstash-test",
	"source": "/host/home/logs/core-auth.log",
	"offset": 54135810,
	"prospector": {
		"type": "log"
	},
	"k8s_pod": "nacos-0",
	"k8s_node_name": "10.136.130.104",
	"beat": {
		"hostname": "log-pilot-tprrp",
		"version": "6.1.1",
		"name": "log-pilot-tprrp"
	},
	"message": "2023-03-27 01:17:08,969 DEBUG auth start, request: GET /nacos/v1/ns/instance/list\n",
	"docker_container": "k8s_k8snacos_nacos-0_nacos_b8d95fe8-9788-4834-9fc6-2225f522acf7_0",
	"topic": "logstash-test",
	"k8s_container_name": "k8snacos"
}]`

	is := make([]interface{}, 0)
	err := json.Unmarshal([]byte(msgstr), &is)
	if err != nil {
		t.Errorf("err=%v", err)
		return
	}
	for _, i := range is {
		bts, err := json.Marshal(i)
		if err != nil {
			log.Warnf("marshal err=%v", err)
			return
		}
		t.Log(string(bts))
	}
	// Set up Kafka connection.
	topic := "apm-02"
	brokerAddr := []string{"10.200.14.226:9092"}

	config := sarama.NewConfig()
	config.Producer.RequiredAcks = sarama.WaitForAll
	config.Producer.Retry.Max = 5
	config.Producer.Return.Successes = true

	producer, err := sarama.NewSyncProducer(brokerAddr, config)
	if err != nil {
		t.Fatalf("Failed to create Kafka producer: %v", err)
	}
	defer producer.Close()

	// Send message to Kafka.
	msg := &sarama.ProducerMessage{
		Topic: topic,
		Value: sarama.StringEncoder(msgstr),
	}

	partition, offset, err := producer.SendMessage(msg)
	if err != nil {
		t.Fatalf("Failed to send message to Kafka: %v", err)
	}

	fmt.Printf("Message sent successfully. Partition: %d, Offset: %d\n", partition, offset)
}

func TestSassss(t *testing.T) {
	config := sarama.NewConfig()
	config.Net.SASL.Enable = true
	config.Version = sarama.V2_1_1_0
	config.Net.SASL.User = "producer"
	config.Consumer.Offsets.Initial = sarama.OffsetOldest
	config.Net.SASL.Password = "producerpwd"
	config.Net.SASL.Mechanism = sarama.SASLTypePlaintext
	config.Consumer.Offsets.Retry.Max = 10

	brokers := []string{"10.200.6.16:9092"}

	client, err := sarama.NewClient(brokers, config)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer client.Close()

	topics, err := client.Topics()
	if err != nil {
		panic(err)
	}

	fmt.Println("Available topics:")
	for _, topic := range topics {
		fmt.Println(topic)
	}
}

func TestCustom_Print(t *testing.T) {
	config := &kafka.ConfigMap{
		"bootstrap.servers":  "10.200.6.16:9092",
		"sasl.username":      "producer",
		"sasl.password":      "producerpwd",
		"security.protocol":  "SASL_PLAINTEXT",
		"sasl.mechanism":     "PLAIN",
		"group.id":           "my-group",
		"auto.offset.reset":  "earliest",
		"enable.auto.commit": false,
	}
	/*
		adminClient, err := kafka.NewAdminClient(config)
		if err != nil {
			panic(err)
		}
		defer adminClient.Close()
		topics, err := adminClient.GetMetadata(nil, true, 5000)
		if err != nil {
			panic(err)
		}

		fmt.Println("Available topics:")
		for _, topic := range topics.Topics {
			fmt.Println(topic.Topic)
		}*/
	consumer, err := kafka.NewConsumer(config)
	if err != nil {
		panic(err)
	}
	defer consumer.Close()

	topic := "test_topic"

	err = consumer.SubscribeTopics([]string{topic}, nil)
	if err != nil {
		panic(err)
	}

	fmt.Printf("Subscribed to topic %s\n", topic)

	for {
		message, err := consumer.ReadMessage(-1)
		if err == nil {
			fmt.Printf("Received message: %s\n", string(message.Value))
		} else {
			fmt.Printf("Error: %v (%v)\n", err, message)
		}
	}
}
