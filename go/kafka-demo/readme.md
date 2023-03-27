## kafka 测试
从kafka中订阅消息主题，输出到当前文件 topic.msg

### 启动
启动 zookeeper
```shell
docker run -d --name zookeeper -p 2181:2181 wurstmeister/zookeeper
```

启动kafka
```shell
docker run -d --name kafka --publish 9092:9092 --link zookeeper:zookeeper -e KAFKA_BROKER_ID=1  -e HOST_IP=10.200.14.226  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://10.200.14.226:9092  -e KAFKA_ADVERTISED_HOST_NAME=10.200.14.226 -e KAFKA_ADVERTISED_PORT=9082  --restart=always  -t  wurstmeister/kafka:2.12-2.5.1
```

启动kafka-demo
```shell
./kafka-demo -kafkaAddrs 10.200.14.226:9092 -topics test_topic
# 如果是多个kafka 或者 多个topic
./kafka-demo -kafkaAddrs 10.200.14.226:9092,10.200.14.227:9092 -topics test_topic,test_topic2
```

单元测试 发送数据
```go
go test -v .
```
