package main

import (
	"bufio"
	"fmt"
	"io"
	"os"

	"github.com/Shopify/sarama"
)

func main() {

	/*	msgstr := `[{
			"message": "2023-03-27 01:17:08,969 DEBUG auth start, request: GET /nacos/v1/ns/instance/list\n",
			"docker_container": "k8s_k8snacos_nacos-0_nacos_b8d95fe8-9788-4834-9fc6-2225f522acf7_0",
			"topic": "logstash-test",
			"k8s_container_name": "k8snacos"
		},
		{
			"message": "2023-03-27 01:17:08,969 DEBUG auth start, request: GET /nacos/v1/ns/instance/list\n",
			"docker_container": "k8s_k8snacos_nacos-0_nacos_b8d95fe8-9788-4834-9fc6-2225f522acf7_0",
			"topic": "logstash-test",
			"k8s_container_name": "k8snacos"
		}]`*/

	/*	is := make([]interface{}, 0)
		err := json.Unmarshal([]byte(msgstr), &is)
		if err != nil {
			fmt.Printf("err=%v\n", err)
			return
		}
		for _, i := range is {
			bts, err := json.Marshal(i)
			if err != nil {
				fmt.Printf("marshal err=%v\n", err)
				return
			}
			fmt.Println(string(bts))
		}*/
	// Set up Kafka connection.
	topic := "apm"
	// topic := "skywalking-meters" // skywalking-metrics skywalking-segments skywalking-profilings skywalking-managements skywalking-logging
	//topic := "skywalking-logging" //
	brokerAddr := []string{"49.232.153.84:9092"}

	config := sarama.NewConfig()
	config.Producer.RequiredAcks = sarama.WaitForAll
	config.Producer.Retry.Max = 5
	config.Producer.Return.Successes = true

	producer, err := sarama.NewSyncProducer(brokerAddr, config)
	if err != nil {
		fmt.Printf("Failed to create Kafka producer: %v \n", err)
		return
	}
	defer producer.Close()

	file, err := os.Open("./topic.msg")
	if err != nil {
		fmt.Println(err)
		return
	}
	rd := bufio.NewReader(file)

	for {
		line, err := rd.ReadString('\n')
		if err != nil || io.EOF == err {
			break
		} else {
			// Send message to Kafka.
			msg := &sarama.ProducerMessage{
				Topic: topic,
				Value: sarama.StringEncoder(line),
			}

			partition, offset, err := producer.SendMessage(msg)
			if err != nil {
				fmt.Printf("Failed to send message to Kafka: %v\n", err)
				return
			}

			fmt.Printf("Message sent successfully. Partition: %d, Offset: %d\n", partition, offset)
		}
	}
}
