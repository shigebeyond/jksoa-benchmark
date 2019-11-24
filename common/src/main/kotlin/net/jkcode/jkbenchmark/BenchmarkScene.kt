package net.jkcode.jkbenchmark

import com.weibo.api.motan.rpc.ResponseFuture
import net.jkcode.jkbenchmark.rpc.common.api.IBenchmarkService
import net.jkcode.jkbenchmark.rpc.common.api.motan.IMotanBenchmarkServiceAsync
import net.jkcode.jksoa.guard.measure.HashedWheelMeasurer
import net.jkcode.jkutil.common.Config
import net.jkcode.jkutil.common.currMillis
import net.jkcode.jkutil.common.currMillisCached
import org.slf4j.LoggerFactory
import java.text.MessageFormat
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 性能测试场景
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
class BenchmarkScene(
        public val player: IBenchmarkPlayer, // 玩家
        public val sceneConfig: Config // 场景配置
) {
    
    companion object{
        /**
         * 日志
         */
        public val logger = LoggerFactory.getLogger(this.javaClass)

        /**
         * 应用配置
         */
        public val appConfig: Config = Config.instance("app", "yaml")
    }

    /**
     * 是否异步
     */
    public val async: Boolean = sceneConfig["async"]!!

    /**
     * 动作
     */
    public val action: (Int)->Any? by lazy {
        if(async) // 异步
            player.getAsyncAction(sceneConfig["action"]!!)
        else // 同步
            player.getSyncAction(sceneConfig["action"]!!)
    }

    /**
     * 场景名
     */
    public val name: String by lazy{
        val action: String = sceneConfig["action"]!! // 动作
        val concurrents: Int = sceneConfig["concurrents"]!! // 线程数/并发数
        val requests: Int = sceneConfig["requests"]!! // 请求数
        val async: Boolean = sceneConfig["async"]!! // 是否异步
        "${player.name}-$action-c$concurrents-n$requests-" + if(async) "asyn" else "syn"
    }

    override fun toString(): String {
        return name
    }

    init {
        currMillisCached = false
    }

    /**
     * 热身
     */
    protected fun warmup() {
        val start = currMillis()
        var requests: Int = 2000 //appConfig["warmupRequests"]!! // 热身请求数
        logger.info("Warmup start: n$requests")
        val latch = CountDownLatch(requests)
        for(i in 1..requests){
            callAction(i) { r, ex ->
                // 热身阶段的异常直接抛
                if(ex != null)
                    throw  ex
                
                latch.countDown()
            }
        }
        latch.await()
        val runTime = (currMillis() - start)
        logger.info("Warmup end: cost $runTime ms")
        Thread.sleep(3000)
    }

    /**
     * 性能测试
     */
    public fun run(): BenchmarkResult {
        logger.info("---------- Run scene: $name ----------")
        // 热身
        warmup()
        
        // 性能测试
        val concurrents: Int = sceneConfig["concurrents"]!! // 线程数/并发数
        val requests: Int = sceneConfig["requests"]!! // 请求数
        val logEveryRequest: Boolean = appConfig["logEveryRequest"]!!
        logger.info("Test start: $name")
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
                callAction(i) { r, e ->
                    //2 添加请求耗时
                    val bucket = measurer.currentBucket()
                    val reqTime = System.nanoTime() / rtNsMultiple - reqStart
                    bucket.addRt(reqTime) // 千分之一毫秒

                    if (e == null) //3 添加成功计数
                        bucket.addSuccess()
                    else //4 添加异常计数
                        bucket.addException()

                    if (logEveryRequest) {
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
        logger.info(">>> Test Result: $name \n$result")

        pool.shutdownNow()

        return result
    }

    /**
     * 调用动作
     * @param param 动作参数
     * @param callback 回调
     * 
     */
    protected fun callAction(param: Int, callback: (Any?, Throwable?)->Unit){
        // 异步
        if(async){
            val future = action.invoke(param) as CompletableFuture<*>
            future.whenComplete(callback)
            return
        }

        // 同步
        try {
            val r = action.invoke(param)
            callback.invoke(r, null)
        }catch (t: Throwable){
            callback.invoke(null, t)
        }
    }


}