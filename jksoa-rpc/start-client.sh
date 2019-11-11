#!/bin/sh
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m -Djava.util.concurrent.ForkJoinPool.common.parallelism=8"

SERVER_NAME='net.jkcode.jksoa.benchmark.rpc.BenchmarkClient'

java $JAVA_OPTS -cp jksoa-rpc-1.0-SNAPSHOT.jar:libs/* $SERVER_NAME