package net.jkcode.jksoa.benchmark.common

import com.weibo.api.motan.rpc.DefaultResponseFuture
import com.weibo.api.motan.rpc.ResponseFuture
import net.jkcode.jkmvc.common.Config
import net.jkcode.jkmvc.common.currMillis
import net.jkcode.jkmvc.common.currMillisCached
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.benchmark.common.api.motan.IMotanBenchmarkService
import net.jkcode.jksoa.benchmark.common.api.motan.IMotanBenchmarkServiceAsync
import net.jkcode.jksoa.guard.measure.HashedWheelMeasurer
import net.jkcode.jksoa.guard.measure.IMetricBucket
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.text.MessageFormat
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 性能测试结果
 */
class BenchmarkResult(
        public val bucket: IMetricBucket, // 统计数据
        public val runTime: Long // 运行时间
): Comparable<BenchmarkResult>{

    override fun toString(): String {
        return bucket.toDesc(runTime)
    }

    override fun compareTo(other: BenchmarkResult): Int {
        return (this.runTime - other.runTime).toInt()
    }
}

/**
 * 性能测试
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
class BenchmarkTest(
        public val name: String, // 测试名
        public val config: Config // 性能测试配置
) {

    /**
     * 调试配置
     */
    public val debugConfig: Config = Config.instance("debug", "yaml")

    /**
     * 日志
     */
    public val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 执行测试
     * @param service 服务代理
     */
    public fun run(benchmarkService: Any): BenchmarkResult {
        // 测试调用的方法
        val action: (Int) -> CompletableFuture<*> =
                when(benchmarkService){
                    is IBenchmarkService -> getNormalAction(benchmarkService)
                    is IMotanBenchmarkServiceAsync -> getMotanAction(benchmarkService)
                    else -> throw IllegalArgumentException("不能识别 benchmarkService 类型")
                }


        // 性能测试
        return test(action)
    }

    /**
     * 性能测试
     * @param action 测试的方法调用
     */
    protected fun test(action: (Int) -> CompletableFuture<*>): BenchmarkResult {
        currMillisCached = false

        // 热身
        warmup(action)

        // 性能测试
        var concurrents: Int = config["concurrents"]!! // 线程数/并发数
        var requests: Int = config["requests"]!! // 请求数
        logger.info("Test start")
        logger.info(config.props.toString())
        val latch = CountDownLatch(requests)
        val pool = Executors.newFixedThreadPool(concurrents)

        val rtMsFraction = 1000 // 千分之一毫秒
        val rtNsMultiple = 1000000L / rtMsFraction
        val measurer = HashedWheelMeasurer(60 * 5, 1000, 100, rtMsFraction)
        val start = System.nanoTime() / rtNsMultiple
        val resps = AtomicInteger()
        for (i in 1..requests) {
            pool.submit {
                // 1 添加总计数
                measurer.currentBucket().addTotal()
                val reqStart = System.nanoTime() / rtNsMultiple

                // rpc
                val future = action.invoke(i)
                future.whenComplete { r, e ->
                    //2 添加请求耗时
                    val bucket = measurer.currentBucket()
                    val reqTime = System.nanoTime() / rtNsMultiple - reqStart
                    bucket.addRt(reqTime) // 千分之一毫秒

                    if (e == null) //3 添加成功计数
                        bucket.addSuccess()
                    else //4 添加异常计数
                        bucket.addException()

                    if (debugConfig["logEveryRequest"]!!) {
                        logger.info(MessageFormat.format("Response {0}: cost {1,number,#.##} ms", resps.incrementAndGet(), reqTime.toDouble() / rtMsFraction))
                        if (e != null)
                            logger.error("err: " + e.message, e)
                    }

                    latch.countDown()
                }

            }
        }

        var runTime = System.nanoTime() / rtNsMultiple - start
        logger.info(MessageFormat.format("Test request end: cost {0,number,#.##} ms", runTime.toDouble() / rtMsFraction))

        latch.await()
        runTime = System.nanoTime() / rtNsMultiple - start
        logger.info(MessageFormat.format("Test response end: cost {0,number,#.##} ms", runTime.toDouble() / rtMsFraction))

        // 打印性能测试结果
        val result = BenchmarkResult(measurer.bucketCollection(), runTime)
        logger.info("----------$name Benchmark Statistics--------------\n${config.props}\n$result")

        pool.shutdownNow()

        return result
    }


    /**
     * 热身
     * @param action 测试的方法调用
     */
    protected fun warmup(action: (Int) -> CompletableFuture<*>) {
        val start = currMillis()
        var requests: Int = 1000 //config["warmupRequests"]!! // 热身请求数
        logger.info("Warmup start")
        logger.info("Requests: $requests")
        val latch = CountDownLatch(requests)
        for(i in 1..requests){
            val future = action.invoke(i)
            future.whenComplete { r, ex ->
                latch.countDown()
            }
        }
        latch.await()
        val runTime = (currMillis() - start)
        logger.info("Warmup end: cost $runTime ms")
        Thread.sleep(2000)
    }

    /**
     * 获得正常的动作(测试调用的方法)
     *   方法返回类型就是 CompletableFuture
     *
     * @param benchmarkService
     * @return
     */
    protected fun getNormalAction(benchmarkService: IBenchmarkService): (Int) -> CompletableFuture<*> {
        val action: (Int) -> CompletableFuture<*> =
                when (config.getString("action")!!) {
                    "nth" -> benchmarkService::doNothing
                    "cache" -> benchmarkService::getMessageFromCache
                    "file" -> benchmarkService::getMessageFromFile
                    "db" -> benchmarkService::getMessageFromDb
                    else -> throw Exception("不能识别action配置: " + config.getString("action"))
                }
        return tryAsyncToSync(action)
    }

    /**
     * 异步转同步
     */
    protected fun tryAsyncToSync(action: (Int) -> CompletableFuture<*>): (Int) -> CompletableFuture<*>{
        // 异步
        if(config["async"]!!)
            return action

        // 同步
        return {id ->
            val f = action.invoke(id)
            f.get()
            f
        }
    }

    /**
     * 获得motan的动作(测试调用的方法)
     *    方法返回类型就是 DefaultResponseFuture
     *
     * @param benchmarkService
     * @return
     */
    protected fun getMotanAction(benchmarkService: IMotanBenchmarkServiceAsync): (Int) -> CompletableFuture<*> {
        val action: (Int) -> ResponseFuture =
                when (config.getString("action")!!) {
                    "nth" -> benchmarkService::doNothingAsync
                    "cache" -> benchmarkService::getMessageFromCacheAsync
                    "file" -> benchmarkService::getMessageFromFileAsync
                    "db" -> benchmarkService::getMessageFromDbAsync
                    else -> throw Exception("不能识别action配置: " + config.getString("action"))
                }
        val action2: (Int) -> CompletableFuture<*> = { id:Int ->
            val f = action.invoke(id)
            toCompletableFuture(f)
        }

        return tryAsyncToSync(action2)
    }

    /**
     * motan的 ResponseFuture 转 CompletableFuture
     */
    protected fun toCompletableFuture(src: ResponseFuture): CompletableFuture<Any?> {
        val target = CompletableFuture<Any?>()
        src.addListener { f ->
            try{
                target.complete(f.value)
            }catch (e: Exception){
                target.completeExceptionally(e)
            }
        }
        return target
    }

}