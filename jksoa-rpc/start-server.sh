#!/bin/sh
JAVA_OPTS="-Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m -Djava.util.concurrent.ForkJoinPool.common.parallelism=16"

JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

SERVER_NAME='net.jkcode.jksoa.benchmark.rpc.BenchmarkServer'

java $JAVA_OPTS $JAVA_DEBUG_OPTS -cp conf:libs/* $SERVER_NAME