package main

import (
	"context"
	"fmt"
	"time"

	"github.com/confluentinc/confluent-kafka-go/kafka"
	"github.com/uber/jaeger-client-go/thrift"
	"github.com/uber/jaeger-client-go/thrift-gen/jaeger"
)

func main() {
	// 创建 Kafka 生产者对象。
	producer, err := kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers": "10.200.14.226:9092",
	})
	if err != nil {
		fmt.Println(err)
		return
	}
	defer producer.Close()

	/*	// 组装span pb数据
		// 组装一个 span。
		span := &model.Span{
			TraceID:       model.TraceID{High: 0x1234567890abcdef, Low: 0xfedcba0987654321},
			SpanID:        model.SpanID(0xdeadbeef),
			OperationName: "my-operation",
			StartTime:     time.Now(),
			Duration:      1000000000,
			Tags: []model.KeyValue{
				model.String("my-tag", "my-value"),
			},
		}

		// 将 span 转换为 Jaeger proto 对象。
		//	jaegerSpan := modelToJaegerProto(span)

		// 创建 Jaeger 批处理请求。
		request := &api_v2.PostSpansRequest{
			Batch: model.Batch{
				Spans: []*model.Span{
					span,
				},
			},
		}

		payload, err := proto.Marshal(request)*/
	//span := JaegerSpan{
	//	TraceID:       "1234567890abcdef",
	//	SpanID:        "fedcba0987654321",
	//	OperationName: "my-operation",
	//	Flags:         0,
	//	StartTime:     1617064279000000000,
	//	Duration:      1000000000,
	//	Tags: map[string]string{
	//		"my-tag": "my-value",
	//	},
	//}

	val := "sadfas"
	batch := &jaeger.Batch{Process: &jaeger.Process{ServiceName: "service"}, Spans: []*jaeger.Span{{
		TraceIdLow:    0xfedcba09,
		TraceIdHigh:   0x12345678,
		SpanId:        0xdeadbeef,
		ParentSpanId:  0,
		OperationName: "test_op",
		References:    nil,
		Flags:         0,
		StartTime:     time.Now().UnixMicro(),
		Duration:      1000000000,
		Tags: []*jaeger.Tag{{
			Key:   "new_key",
			VType: 0,
			VStr:  &val,
		}},
	}}}

	// 将 span 数据序列化为 Thrift 格式。
	transport := thrift.NewTMemoryBuffer()
	protocol := thrift.NewTBinaryProtocolConf(transport, &thrift.TConfiguration{})
	err = batch.Write(context.Background(), protocol)
	if err != nil {
		fmt.Println(err)
		return
	}
	payload := transport.Bytes()

	// 发送消息到 Kafka。
	topic := "jaeger-spans"
	message := &kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
		Value:          payload,
	}
	deliveryChan := make(chan kafka.Event)
	err = producer.Produce(message, deliveryChan)
	if err != nil {
		fmt.Println(err)
		return
	}

	// 等待消息发送完成。
	event := <-deliveryChan
	switch e := event.(type) {
	case *kafka.Message:
		if e.TopicPartition.Error != nil {
			fmt.Printf("Delivery failed: %v\n", e.TopicPartition.Error)
		} else {
			fmt.Printf("Delivered message to %v [%v]\n", *e.TopicPartition.Topic, e.TopicPartition.Partition)
		}
	default:
		fmt.Printf("Unexpected event type: %T\n", e)
	}
}
