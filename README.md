# 性能测试

参考motan-benchmark: 1 [说明](https://github.com/weibocom/motan/blob/master/docs/wiki/zh_userguide.md#性能测试) 2
[代码](https://github.com/weibocom/motan/tree/master/motan-benchmark)

我分别针对 jksoa-rpc / dubbo / motan 等3个框架进行性能测试

以下是我们测试的结果：

# 测试环境

## 硬件配置

     Server端：
     CPU：model name:Intel(R) Xeon(R) CPU E5-2620 v2 @ 2.10GHz,cache size: 15360 KB,processor_count : 24
     内存：16G
     网络：千兆网卡
     硬盘：900GB

     Client端：
     CPU：model name:Intel(R) Xeon(R) CPU E5-2620 v2 @ 2.10GHz,cache size: 15360 KB,processor_count : 24
     内存：16G
     网络：千兆网卡
     硬盘：900GB

## 软件配置

     JDK版本：
     java version "1.8.0_172"
     Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
     Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)

     JVM参数：
     java -Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m

# 测试对象

## jksoa
1. 版本: 1.9.0

2. 线程池大小: [20, 800], 队列大小: 100000000

jksoa-benchmark/jkrpc/src/main/resources/common-pool.yaml

```
# 公共线程池的配置
# 初始线程数
corePoolSize: 20
# 最大线程数
maximumPoolSize: 800
# 队列大小
queueSize: 100000000
```

3. client端请求超时: 100秒

jksoa-benchmark/jkrpc/src/main/resources/rpc-client.yaml

```
connectTimeoutMillis: 500 # 连接超时，int类型，单位：毫秒
requestTimeoutMillis: !!java.lang.Long 100000 # 请求超时，Long类型，单位：毫秒
```

## dubbo
1. 版本: 2.7.2

2. 线程池大小: [20, 800], 队列大小: 100000000

jksoa-benchmark/dubbo/src/main/resources/spring/dubbo-provider.xml

```
<dubbo:protocol name="dubbo" dispatcher="all" threadpool="eager" corethreads="20" threads="800" queues="100000000"/>
```

3. client端请求超时: 100秒

jksoa-benchmark/dubbo/src/main/resources/spring/dubbo-consumer.xml

```
<dubbo:reference id="benchmarkService" check="false" interface="net.jkcode.jkbenchmark.rpc.common.api.IBenchmarkService" timeout="100000"/>
```

## motan
1. 版本: 1.1.6

2. 线程池大小: [20, 800], 队列大小: 100000000

jksoa-benchmark/motan/src/main/resources/motan-server.xml

```
<!-- 协议配置。为防止多个业务配置冲突，推荐使用id表示具体协议。-->
<motan:protocol id="benchmarkMotan" default="true" name="motan"
                requestTimeout="20000" maxServerConnection="80000" maxContentLength="1048576"
                maxWorkerThread="800" minWorkerThread="20" workerQueueSize="100000000"/>
```

3. client端请求超时: 100秒

jksoa-benchmark/motan/src/main/resources/motan-client.xml

```
<!-- 具体referer配置。使用方通过beanid使用服务接口类 -->
<motan:referer id="asyncBenchmarkService" directUrl="127.0.0.1:8002"
               interface="net.jkcode.jkbenchmark.rpc.common.api.motan.IMotanBenchmarkServiceAsync"
               connectTimeout="500" requestTimeout="100000" basicReferer="motanClientBasicConfig"
               async="true"/>
```


# 测试脚本

## Server测试场景：

    并发多个Client，连接数50，并发数100，测试Server极限性能

## Client测试场景：

    单客户端，10连接，在并发数分别为1，10，20，50的情况下，分别进行如下场景测试：
    - 传入空包，不做任何处理，原样返回
    - 传入Pojo嵌套对象，不做任何处理，原样返回
    - 传入1kString，不做任何处理，原样返回
    - 传入5kString，不做任何处理，原样返回
    - 传入10kString，不做任何处理，原样返回
    - 传入20kString，不做任何处理，原样返回
    - 传入30kString，不做任何处理，原样返回
    - 传入50kString，不做任何处理，原样返回。

# 测试结果

## Server测试结果：

    请求空包：单Server极限TPS：18W
    请求1KString：单Server极限TPS：8.4W
    请求5KString：单Server极限TPS：2W

## Client测试结果：

对比图：

![](media/14614085719511.jpg)


原始数据：

| 并发数 | 测试场景  | 平均TPS | 平均响应时间(ms) |
|--------|-----------|---------|--------------|
| 1      | Empty     | 5601    | 0.178        |
| 1      | Pojo      | 3556    | 0.281        |
| 1      | 1KString  | 2657    | 0.376        |
| 1      | 5KString  | 1100    | 0.908        |
| 1      | 10KString | 949     | 1.052        |
| 1      | 20KString | 600     | 1.664        |
| 1      | 30KString | 512     | 1.95         |
| 1      | 50KString | 253     | 3.939        |
| 10     | Empty     | 39181   | 0.255        |
| 10     | Pojo      | 27314   | 0.365        |
| 10     | 1KString  | 19968   | 0.5          |
| 10     | 5KString  | 11236   | 0.889        |
| 10     | 10KString | 5875    | 1.701        |
| 10     | 20KString | 4493    | 2.224        |
| 10     | 30KString | 3387    | 2.951        |
| 10     | 50KString | 1499    | 6.668        |
| 20     | Empty     | 69061   | 0.289        |
| 20     | Pojo      | 47226   | 0.423        |
| 20     | 1KString  | 34754   | 0.575        |
| 20     | 5KString  | 18883   | 1.058        |
| 20     | 10KString | 9032    | 2.214        |
| 20     | 20KString | 5471    | 3.654        |
| 20     | 30KString | 3724    | 5.368        |
| 20     | 50KString | 1973    | 10.133       |
| 50     | Empty     | 69474   | 0.719        |
| 50     | Pojo      | 64022   | 0.78         |
| 50     | 1KString  | 58937   | 0.848        |
| 50     | 5KString  | 20703   | 2.414        |
| 50     | 10KString | 10761   | 4.645        |
| 50     | 20KString | 5614    | 8.904        |
| 50     | 30KString | 3782    | 13.214       |
| 50     | 50KString | 2285    | 21.869       |
