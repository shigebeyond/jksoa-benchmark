#!/bin/sh
gradle build -x test -Pall

deploy()
{
	echo "打包 $1"
	cd $1/build
	rm *.zip
	zip -r $1.zip $1/
	echo "上传 $1"
	scp *.zip root@$test:/root/java/benchmark
}

deploy jkrpc
deploy motan
deploy dubbo
