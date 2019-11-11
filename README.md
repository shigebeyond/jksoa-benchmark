# 性能测试

参考motan-benchmark: 1 [说明](https://github.com/weibocom/motan/blob/master/docs/wiki/zh_userguide.md#性能测试) 2
[代码](https://github.com/weibocom/motan/tree/master/motan-benchmark)

我分别针对 jksoa-rpc / dubbo / motan 等3个框架进行性能测试

以下是我们测试的结果：

### 测试环境

#### 硬件配置

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

#### 软件配置

     JDK版本：
     java version "1.8.0_172"
     Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
     Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)

     JVM参数：
     java -Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m

### 测试脚本

#### Server测试场景：

    并发多个Client，连接数50，并发数100，测试Server极限性能

#### Client测试场景：

    单客户端，10连接，在并发数分别为1，10，20，50的情况下，分别进行如下场景测试：
    - 传入空包，不做任何处理，原样返回
    - 传入Pojo嵌套对象，不做任何处理，原样返回
    - 传入1kString，不做任何处理，原样返回
    - 传入5kString，不做任何处理，原样返回
    - 传入10kString，不做任何处理，原样返回
    - 传入20kString，不做任何处理，原样返回
    - 传入30kString，不做任何处理，原样返回
    - 传入50kString，不做任何处理，原样返回。

### 测试结果

#### Server测试结果：

    请求空包：单Server极限TPS：18W
    请求1KString：单Server极限TPS：8.4W
    请求5KString：单Server极限TPS：2W

#### Client测试结果：

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
