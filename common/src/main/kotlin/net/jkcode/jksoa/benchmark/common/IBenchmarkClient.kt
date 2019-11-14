package net.jkcode.jksoa.benchmark.common

import net.jkcode.jkmvc.common.mapToArray
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 性能测试-client
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
interface IBenchmarkClient {

    /**
     * 执行测试
     *
     * @param args main()参数
     * @param action 测试的方法调用
     */
    fun test(args: Array<String>, action: (Int)-> CompletableFuture<*>) {
        // 线程数/并发数
        var nThread = 1
        if (args.size > 0)
            nThread = Integer.parseInt(args[0])

        // 请求数
        var nReq = 100
        if (args.size > 1)
            nReq = Integer.parseInt(args[1])

        test(nThread, nReq, action)
    }

    /**
     * 执行测试
     *
     * @param nThread 线程数/并发数
     * @param nReq 请求数
     * @param action 测试的方法调用
     */
    fun test(nThread: Int, nReq: Int, action: (Int) -> CompletableFuture<*>) {
        val latch = CountDownLatch(nReq)
        val pool = Executors.newFixedThreadPool(nThread)

        // 热身
        val futures = (1..10).mapToArray { i ->
            action.invoke(i)
        }
        CompletableFuture.allOf(*futures).get()

        // 性能测试
        val stats = SynchronizedDescriptiveStatistics()
        var start = System.currentTimeMillis()
        val total = AtomicInteger(0) // 请求总数
        val success = AtomicInteger(0) // 请求成功数
        for (i in 1..nReq) {
            pool.submit {
                val start = System.currentTimeMillis()
                // rpc
                val future = action.invoke(i)
                future.whenComplete { r, ex ->
                    // 添加耗时
                    val costTime = System.currentTimeMillis() - start
                    stats.addValue(costTime.toDouble())

                    // 请求总数+1
                    total.incrementAndGet()

                    // 请求成功数+1
                    if (ex != null)
                        success.incrementAndGet()

                    latch.countDown()
                }

            }
        }

        latch.await()

        // 打印性能测试结果
        val costTime = System.currentTimeMillis() - start
        System.out.printf("sent     request    : %d\n", nReq)
        System.out.printf("received response    : %d\n", total.get())
        System.out.printf("received response success : %d\n", success.get())
        System.out.printf("throughput  (TPS)    : %d\n", nReq * 1000 / costTime)

        System.out.printf("mean: %f\n", stats.getMean())
        System.out.printf("median: %f\n", stats.getPercentile(50.0))
        System.out.printf("max: %f\n", stats.getMax())
        System.out.printf("min: %f\n", stats.getMin())
        System.out.printf("99P: %f\n", stats.getPercentile(90.0))
    }


}


