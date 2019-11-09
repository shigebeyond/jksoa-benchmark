#!/bin/sh
# gradle build -x test

cd build/libs

export JAVA_OPTS="-XX:MaxPermSize=128m -Xmx512M -Djava.util.concurrent.ForkJoinPool.common.parallelism=8"

java $JAVA_OPTS -cp jksoa-rpc-1.0-SNAPSHOT.jar:libs/* net.jkcode.jksoa.rpc.server.RpcServerLauncher