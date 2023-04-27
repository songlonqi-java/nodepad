# dk-trace-ut
基于 java 的jar包，制作镜像发送链路数据到DK。


## java 代码逻辑
这是一个web项目，发送不同的请求会得到不同的 span 状态。

1. host:port/resource: 如：http://10.200.14.226:8080/resource
2. host:port/restError 如 http://10.200.14.226:8080/testError

链路图
![image](https://df-storage-dev.oss-cn-hangzhou.aliyuncs.com/songlongqi/trace/otel.png)

详情图
![image](https://df-storage-dev.oss-cn-hangzhou.aliyuncs.com/songlongqi/trace/span.png)

## jar 包位置
具体看  dockerfile 和 startup.sh


## build
```shell
docker build -t dk-trace-ut:v0.0.1 .
```

## 启动命令
docker run -d -p 8080:8080 -e AGENT="ddtrace or other" -e AGENTARGS="agent args" -e PARAMS="--server.port=8080"

比如 

ddtrace:
```shell
docker run -d --name=app --network=host -p 8080:8080 -e AGENT="ddtrace" -e AGENTARGS="-Ddd.agent.host=10.200.14.226" -e PARAMS="--server.port=8080"  dk-trace-ut:v0.0.1
```

otel:
```shell
docker run -d --name app --network host -p 8080:8080 -e AGENT="otel" -e AGENTARGS="-Dotel.traces.exporter=otlp -Dotel.exporter.otlp.endpoint=http://localhost:4317" -e PARAMS="--server.port=8080"  dk-trace-ut:v0.0.1

# otel 除了可以发送otlp到dk，还可以发送 jaeger、zipkin 以及 prom 数据到dk。

# jaeger
# otel.traces.exporter=jaeger otel.exporter.jaeger.endpoint
# 环境变量方式：OTEL_TRACES_EXPORTER=jaeger OTEL_EXPORTER_JAEGER_ENDPOINT
docker run -d --name app --network host -p 8080:8080 \
 -e AGENT="otel" \
 -e AGENTARGS="-Dotel.traces.exporter=jaeger -Dotel.exporter.jaeger.endpoint=http://localhost:14250" \
 -e PARAMS="--server.port=8080" \
 dk-trace-ut:v0.0.1

```

skywalking:
```shell
# SW_AGENT_NAME
# SW_AGENT_COLLECTOR_BACKEND_SERVICES
# 更多参数查看 skywalking-agnet/config/agent.config
docker run -d --name app --network host -p 8080:8080 -e AGENT="skywalking" -e SW_AGENT_COLLECTOR_BACKEND_SERVICES="10.200.14.226:11800" -e PARAMS="--server.port=8080"  dk-trace-ut:v0.0.1
```
