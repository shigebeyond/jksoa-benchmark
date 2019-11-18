#!/bin/sh
gradle build -x test -Pall

echo "打包 jksoa-rpc"
cd jksoa-rpc/build
rm *.zip
zip -r jksoa-rpc.zip jksoa-rpc/
scp *.zip root@$test:/root/java/benchmark

echo "打包 dubbo"
cd -
cd dubbo/build
rm *.zip
zip -r dubbo.zip dubbo/
scp *.zip root@$test:/root/java/benchmark

echo "打包 motan"
cd -
cd motan/build
rm *.zip
zip -r motan.zip motan/
scp *.zip root@$test:/root/java/benchmark