#!/bin/bash

echo 'agent=' ${AGENT} # 打印
echo 'args=' ${AGENTARGS}

## -javaagent:

case $AGENT in
    "ddtrace")
        # 默认端口9529
        JAVAAGENT="-javaagent:/usr/local/ddtrace/dd-java-agent.jar"
        ;;
    "otel")
        JAVAAGENT="-javaagent:/usr/local/otel/opentelemetry-javaagent.jar"
        ;;
    "skywalking")
        JAVAAGENT="-javaagent:/usr/local/skywalking/skywalking-agent.jar"
        ;;
    *)
    printf "no agent, exit"
    exit      
esac



# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi


exec $JAVA $JAVAAGENT $AGENTARGS -jar /usr/local/app.jar $PARAMS