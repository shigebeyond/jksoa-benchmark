package net.jkcode.jksoa.benchmark.common

import net.jkcode.jkmvc.common.Config
import net.jkcode.jkmvc.common.currMillis
import net.jkcode.jkmvc.common.currMillisCached
import net.jkcode.jkmvc.common.mapToArray
import net.jkcode.jksoa.guard.measure.HashedWheelMeasurer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 * 性能测试-client
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
abstract class IBenchmarkClient {

    /**
     * 配置
     */
    public val config: Config = Config.instance("benchmark", "yaml")

    /**
     * 执行测试
     *
     * @param action 测试的方法调用
     */
    fun test(action: (Int) -> CompletableFuture<*>) {
        // 热身
        val futures = (1..1000).mapToArray { i ->
            action.invoke(i)
        }
        CompletableFuture.allOf(*futures).get()

        // 性能测试
        var concurrents: Int = config["concurrents"]!! // 线程数/并发数
        var requests: Int = config["requests"]!! // 请求数
        val latch = CountDownLatch(requests)
        val pool = Executors.newFixedThreadPool(concurrents)

        currMillisCached = false
        val start = currMillis()
        val measurer = HashedWheelMeasurer(5, 1000, 100)
        for (i in 1..requests) {
            pool.submit {
                // 1 添加总计数
                measurer.currentBucket().addTotal()
                val reqStart = currMillis()
                
                // rpc
                val future = action.invoke(i)
                future.whenComplete { r, e ->
                    //2 添加请求耗时
                    val bucket = measurer.currentBucket()
                    bucket.addCostTime(currMillis() - reqStart)

                    if (e == null) //3 添加成功计数
                        bucket.addSuccess()
                    else //4 添加异常计数
                        bucket.addException()

                    latch.countDown()
                }

            }
        }

        latch.await()

        // 打印性能测试结果
        val runTime = (currMillis() - start) / 1000
        println("----------Benchmark Statistics--------------")
        println("Concurrents: $concurrents")
        println("Runtime: $runTime seconds")
        measurer.bucketCollection().printStatistics(runTime)
    }

}