package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"io"
	"os"
	"testing"
	"time"

	"github.com/Shopify/sarama"
	"github.com/confluentinc/confluent-kafka-go/kafka"
)

func TestCustom_SaramaConsumerGroup(t *testing.T) {
	// Set up Kafka connection.
	topic := "test_topic"
	brokerAddr := []string{"tingyun-01:9092"}

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
	"@timestamp": "2023-05-30T13:48:09.416Z",
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
	"@timestamp": "2023-05-30T13:48:09.416Z",
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
	topic := "log_topic"
	// topic := "skywalking-meters" // skywalking-metrics skywalking-segments skywalking-profilings skywalking-managements skywalking-logging
	//topic := "skywalking-logging" //
	brokerAddr := []string{"10.200.6.16:9092"}

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
	config.Consumer.Offsets.Initial = sarama.OffsetOldest
	config.Version = sarama.V2_0_0_0

	config.Net.SASL.Enable = true
	config.Net.SASL.User = "producer"
	config.Net.SASL.Version = sarama.SASLHandshakeV1
	config.Net.SASL.Password = "producerpwd"
	config.Net.SASL.Mechanism = sarama.SASLTypePlaintext

	config.Consumer.Offsets.Retry.Max = 10

	brokers := []string{"tingyun-01:9092"}

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
		"bootstrap.servers":  "tingyun-01:9092",
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

func TestCustom_ty(t *testing.T) {

	//	msgstr := `{"common":{"app_id":"7024","app_key":"83adbec7d44a431e86767506860ce31c","app_name":"LF19ITS-智学苑-iOS","app_type":"app","app_version_name":"3.3.1(2.17.1)","carrier_name":"","channel_name":"AppStore","city_name":"","client_ip":"29.132.15.39","connect_type_name":"WIFI","country_name":"U.S.A","device_id":"19091","latitude":"0.0","longitude":"0.0","manufacturer_model_name":"iPhone 6","manufacturer_name":"Apple","os_name":"iOS","os_version_name":"12.5.7","region_name":"","session_id":"526a3bf7-b636-4e03-869c-774b7d00f17d","user_id":""},"ux_view":{"additional_info":"","timestamp":1686623744,"type":"ux-view","uuid":"1686537789239347747","view_appear_time":146,"view_composite_name":"登录","view_interactive_time":146,"view_name":""}}`
	// msgstr := `{"common":{"app_id":"7023","app_key":"c745489b5e484a998af079bd8546295e","app_name":"LF19ITS-智学苑-Android","app_type":"app","app_version_name":"2.7.5(2.17.1)","carrier_name":"","channel_name":"","city_name":"","client_ip":"29.132.15.38","connect_type_name":"WIFI","country_name":"U.S.A","device_id":"19099","latitude":"0.0","longitude":"0.0","manufacturer_model_name":"Pixel","manufacturer_name":"Google","os_name":"Android","os_version_name":"10","region_name":"","session_id":"c95a66f2-42af-66e1-6b32-b28d730e8b6a","user_id":"haoyadong"},"ux_view":{"additional_info":"","timestamp":1686535776,"type":"ux-view","uuid":"1686536098615952201","view_appear_time":79,"view_composite_name":"com.wisdom.lms.ui.activity.LoginVoiceAccountActivity No_Catched_Action","view_interactive_time":79,"view_name":"com.wisdom.lms.ui.activity.LoginVoiceAccountActivity"}}`

	// Set up Kafka connection.
	topic := "tyrum"

	brokerAddr := []string{"10.200.6.16:9092"}

	config := sarama.NewConfig()
	config.Producer.RequiredAcks = sarama.WaitForAll
	config.Producer.Retry.Max = 5
	config.Producer.Return.Successes = true

	producer, err := sarama.NewSyncProducer(brokerAddr, config)
	if err != nil {
		t.Fatalf("Failed to create Kafka producer: %v", err)
	}
	defer producer.Close()

	lines, err := ReadLinesV3("/data/home/songlq/data/sxtbdemo.msg.md")
	if err != nil {
		t.Fatalf("read lines err=%v", err)
		return
	}
	for _, msgstr := range lines {
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
		time.Sleep(time.Second / 10)
	}
}

func ReadLinesV3(path string) ([]string, error) {
	f, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer f.Close()

	var lines []string
	r := bufio.NewReader(f)
	for {
		// ReadLine is a low-level line-reading primitive.
		// Most callers should use ReadBytes('\n') or ReadString('\n') instead or use a Scanner.
		bytes, _, err := r.ReadLine()
		if err == io.EOF {
			break
		}
		if err != nil {
			return lines, err
		}
		lines = append(lines, string(bytes))
	}
	return lines, nil
}

// {"common":{"app_id":"7012","app_key":"6e644d0a66b341c0b20b4021e4423737","app_name":"LF12YiDYY-神行太保-Android","app_type":"app","app_version_name":"12.57(2.17.0.11)","carrier_name":"","channel_name":"","city_name":"","client_ip":"29.132.15.13","connect_type_name":"","country_name":"U.S.A","device_id":"18051","latitude":"0.0","longitude":"0.0","manufacturer_model_name":"SM-A9200","manufacturer_name":"Samsung","os_name":"Android","os_version_name":"8.0.0","region_name":"","session_id":"5e4806f5-8368-48a1-44b1-71592165f425","user_id":"CHENGANG-069"},"ux_view":{"additional_info":"","timestamp":1686111477,"type":"ux-view","uuid":"1686111769131604378","view_appear_time":15,"view_composite_name":"com.apperian.ease.appcatalog.ui.Login ApplicationInForeground","view_interactive_time":15,"view_name":"com.apperian.ease.appcatalog.ui.Login"}}
// {"common":{"app_id":"7012","app_key":"6e644d0a66b341c0b20b4021e4423737","app_name":"LF12YiDYY-神行太保-Android","app_type":"app","app_version_name":"12.57(2.17.0.11)","carrier_name":"","channel_name":"","city_name":"","client_ip":"29.132.15.18","connect_type_name":"","country_name":"U.S.A","device_id":"18051","latitude":"0.0","longitude":"0.0","manufacturer_model_name":"SM-A9200","manufacturer_name":"Samsung","os_name":"Android","os_version_name":"8.0.0","region_name":"","session_id":"5e4806f5-8368-48a1-44b1-71592165f425","user_id":"CHENGANG-069"},"ux_view":{"additional_info":"","timestamp":1686111477,"type":"ux-view","uuid":"1686111769131604378","view_appear_time":15,"view_composite_name":"com.apperian.ease.appcatalog.ui.Login ApplicationInForeground","view_interactive_time":15,"view_name":"com.apperian.ease.appcatalog.ui.Login"}}
