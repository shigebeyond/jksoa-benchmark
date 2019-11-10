#!/bin/sh
# gradle build -x test

cd build/libs

export JAVA_OPTS="-XX:MaxPermSize=128m -Xmx512M -Djava.util.concurrent.ForkJoinPool.common.parallelism=8"

SERVER_NAME='net.jkcode.jksoa.benchmark.motan.BenchmarkClient'

java $JAVA_OPTS -cp motan-1.0-SNAPSHOT.jar:libs/* $SERVER_NAME