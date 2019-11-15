/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.jkcode.jksoa.benchmark.client

import net.jkcode.jkmvc.common.Config
import net.jkcode.jkmvc.common.mapToArray
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors

abstract class IBenchmarkClient {


    // 配置
    private val config = Config.instance("benchmark", "properties")

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    // 并发数据
    private var concurrents: Int = 0

    // 测试运行时间
    private var runTime: Int = 0

    private var statistics: ClientStatistics? = null


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

    /**
     *
     * @param concurrents 并发线程数
     * @param runtime benchmark实际运行时间
     * @param classname 测试的类名
     * @param params 测试String时，指String的size，单位为k
     */
    fun start(concurrents: Int , runtime: Int, classname: String, params: String) {
        this.concurrents = config["concurrents"]!!
        this.runTime = config["runtime"]!!

        printStartInfo()

        // prepare runnables
        val currentTime = System.nanoTime() / 1000L
        val startTime = currentTime + WARMUPTIME.toLong() * 1000 * 1000L
        val endTime = currentTime + runTime.toLong() * 1000 * 1000L

        val runnables = ArrayList<ClientRunnable>()
        val cyclicBarrier = CyclicBarrier(this.concurrents)
        val countDownLatch = CountDownLatch(this.concurrents)
        for (i in 0 until this.concurrents) {
            val runnable = getClientRunnable(classname, params, cyclicBarrier, countDownLatch, startTime, endTime)
            runnables.add(runnable)
            val thread = Thread(runnable, "benchmarkclient-$i")
            thread.start()
        }

        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val runnableStatisticses = ArrayList<RunnableStatistics>()
        for (runnable in runnables) {
            runnableStatisticses.add(runnable.getStatistics())
        }
        statistics = ClientStatistics(runnableStatisticses)
        statistics!!.collectStatistics()

        printStatistics()

        System.exit(0)
    }

    private fun printStartInfo() {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.SECOND, runTime)
        val finishDate = calendar.time

        val startInfo = StringBuilder(dateFormat.format(currentDate))
        startInfo.append(" ready to start client benchmark")
        startInfo.append(", concurrent num is ").append(concurrents)
        startInfo.append(", the benchmark will end at ").append(dateFormat.format(finishDate))

        println(startInfo.toString())
    }

    private fun printStatistics() {
        println("----------Benchmark Statistics--------------")
        println("Concurrents: $concurrents")
        println("Runtime: $runTime seconds")
        statistics!!.printStatistics()
    }

    companion object {

        private val WARMUPTIME = 30
    }
}
