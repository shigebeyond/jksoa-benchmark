package net.jkcode.jksoa.benchmark.common

import net.jkcode.jkmvc.common.Config
import net.jkcode.jkmvc.common.currMillis
import net.jkcode.jkmvc.common.currMillisCached
import net.jkcode.jksoa.guard.measure.HashedWheelMeasurer
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

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
     * 日志
     */
    public val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 执行测试
     * @param action 测试的方法调用
     */
    fun test(action: (Int) -> CompletableFuture<*>) {
        currMillisCached = false

        // 热身
        warmup(action)

        // 性能测试
        var concurrents: Int = config["concurrents"]!! // 线程数/并发数
        var requests: Int = config["requests"]!! // 请求数
        logger.info("Test start")
        logger.info("Concurrents: $concurrents")
        logger.info("Requests: $requests")
        val latch = CountDownLatch(requests)
        val pool = Executors.newFixedThreadPool(concurrents)

        val start = currMillis()
        val measurer = HashedWheelMeasurer(60 * 5, 1000, 100)
        val resps = AtomicInteger()
        for (i in 1..requests) {
            pool.submit {
                // 1 添加总计数
                measurer.currentBucket().addTotal()
                val reqStart = currMillis()
                
                // rpc
                val future = action.invoke(i % 10 + 1)
                future.whenComplete { r, e ->
                    //2 添加请求耗时
                    val bucket = measurer.currentBucket()
                    val reqTime = currMillis() - reqStart
                    bucket.addCostTime(reqTime)

                    if (e == null) //3 添加成功计数
                        bucket.addSuccess()
                    else //4 添加异常计数
                        bucket.addException()

                    logger.info("Response " + resps.incrementAndGet() + ": cost $reqTime ms")
                    if(e != null)
                        logger.error("err: " + e.message, e)

                    latch.countDown()
                }

            }
        }

        latch.await()
        val runTime = currMillis() - start
        logger.info("Test end: cost $runTime ms")

        // 打印性能测试结果
        logger.info("----------Benchmark Statistics--------------")
        logger.info("Concurrents: $concurrents")
        logger.info("Runtime: $runTime s")
        logger.info(measurer.bucketCollection().toDesc(runTime))
    }

    /**
     * 热身
     * @param action 测试的方法调用
     */
    protected fun warmup(action: (Int) -> CompletableFuture<*>) {
        val start = currMillis()
        var requests: Int = config["warmupRequests"]!! // 热身请求数
        logger.info("Warmup start")
        logger.info("Requests: $requests")
        val latch = CountDownLatch(requests)
        for(i in 1..requests){
            val future = action.invoke(i % 10 + 1)
            future.whenComplete { r, ex ->
                latch.countDown()
            }
        }
        latch.await()
        val runTime = (currMillis() - start)
        logger.info("Warmup end: cost $runTime ms")
        Thread.sleep(2000)
    }

}