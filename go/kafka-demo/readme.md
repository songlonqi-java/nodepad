## kafka 测试
从kafka中订阅消息主题，输出到当前文件 topic.msg

### 启动
启动 zookeeper
```shell
docker run -d --name zookeeper -p 2181:2181 -v  /etc/kafka/secrets/zk_server_jaas.conf:/etc/kafka/secrets/zk_server_jaas.conf -e ZOOKEEPER_TICK_TIME: 2000 -e ZOOKEEPER_MAXCLIENTCNXNS: 0 -e ZOOKEEPER_AUTHPROVIDER.1: org.apache.zookeeper.server.auth.SASLAuthenticationProvider -e ZOOKEEPER_REQUIRECLIENTAUTHSCHEME: sasl -e ZOOKEEPER_JAASLOGINRENEW: 3600000 -e KAFKA_OPTS: -Djava.security.auth.login.config=/etc/kafka/secrets/zk_server_jaas.conf confluentinc/cp-zookeeper:5.5.2

docker run -d --name zookeeper -p 2181:2181 -v  /etc/kafka/secrets/zk_server_jaas.conf:/etc/kafka/secrets/zk_server_jaas.conf -e ZOOKEEPER_TICK_TIME=2000 -e ZOOKEEPER_MAXCLIENTCNXNS=0 -e ZOOKEEPER_AUTHPROVIDER.1=org.apache.zookeeper.server.auth.SASLAuthenticationProvider -e ZOOKEEPER_REQUIRECLIENTAUTHSCHEME=sasl -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_JAASLOGINRENEW=3600000 -e ZOO_REQUIRE_CLIENT_AUTH="true" -e ZOO_ALLOW_ANONYMOUS_LOGIN="true" -e ZOO_SERVER_USERS='myuser=34819d7beeabb9260a5c854bc85b3e44' -e KAFKA_OPTS=-Djava.security.auth.login.config=/etc/kafka/secrets/zk_server_jaas.conf confluentinc/cp-zookeeper:5.5.2
```

启动kafka
```shell
docker run -d --name kafka --publish 9092:9092 --link zookeeper:zookeeper -e KAFKA_BROKER_ID=1  -e HOST_IP=10.200.14.226  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://10.200.14.226:9092  -e KAFKA_ADVERTISED_HOST_NAME=10.200.14.226 -e KAFKA_ADVERTISED_PORT=9082  --restart=always  -t  wurstmeister/kafka:2.12-2.5.1

docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper \
-v /etc/kafka/secrets/kafka_server_jaas.conf:/etc/kafka/secrets/kafka_server_jaas.conf \
-e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT='zookeeper:2181' \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0 \
-e KAFKA_LISTENERS='SASL_PLAINTEXT://0.0.0.0:9092' \
-e KAFKA_ADVERTISED_LISTENERS='SASL_PLAINTEXT://10.200.14.226:9092' \
-e KAFKA_SECURITY_INTER_BROKER_PROTOCOL=SASL_PLAINTEXT \
-e KAFKA_SASL_MECHANISM_INTER_BROKER_PROTOCOL=PLAIN \
-e KAFKA_SASL_ENABLED_MECHANISMS=PLAIN \
-e KAFKA_AUTHORIZER_CLASS_NAME=kafka.security.auth.SimpleAclAuthorizer \
-e KAFKA_OPTS='-Djava.security.auth.login.config=/etc/kafka/secrets/kafka_server_jaas.conf' \
confluentinc/cp-kafka:5.5.2

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
