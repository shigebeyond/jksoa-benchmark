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

import net.jkcode.jkmvc.common.*
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService

import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

abstract class AbstractClientRunnable(
        private val action: ()-> CompletableFuture<*>, // 测试处理
        private val cyclicBarrier: CyclicBarrier, // 栅栏, 用于多测试线程同时开始
        private val countDownLatch: CountDownLatch // 计数器, 用于等待测试线程结束
) {

    var statistics: RunnableStatistics
        internal set
    private val statisticTime: Int

    init {
        statisticTime = ((endTime - startTime) / 1000000).toInt()
        statistics = RunnableStatistics(statisticTime)
    }

    fun run() {
        try {
            cyclicBarrier.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: BrokenBarrierException) {
            e.printStackTrace()
        }

        val future = callAction()
        future.whenComplete { r, ex ->
            countDownLatch.countDown()
        }
    }

    private fun callAction(): CompletableFuture<*> {
        var beginTime = System.nanoTime() / 1000L
        // 1. warm up 热身时间

        while (beginTime <= startTime) {
            beginTime = System.nanoTime() / 1000L
            action.invoke().get()
        }

        // 2. 正式测试时间
        val futures = ArrayList<CompletableFuture<*>>()
        while (beginTime <= endTime) {
            beginTime = System.nanoTime() / 1000L
            // 调用测试处理
            val future = action.invoke()
            future.whenComplete { r, ex ->
                val responseTime = System.nanoTime() / 1000L - beginTime
                collectResponseTimeDistribution(responseTime)
                val currTime = ((beginTime - startTime) / 1000000L).toInt()
                if (currTime >= statisticTime) {
                    continue
                }
                if (result != null) {
                    statistics.TPS[currTime]++
                    statistics.RT[currTime] += responseTime
                } else {
                    statistics.errTPS[currTime]++
                    statistics.errRT[currTime] += responseTime
                }
            }
            futures.add(future)
        }
        return futures.join()
    }

    private fun collectResponseTimeDistribution(time: Long) {
        val responseTime = (time / 1000L).toDouble()
        if (responseTime >= 0 && responseTime <= 1) {
            statistics.above0sum++
        } else if (responseTime > 1 && responseTime <= 5) {
            statistics.above1sum++
        } else if (responseTime > 5 && responseTime <= 10) {
            statistics.above5sum++
        } else if (responseTime > 10 && responseTime <= 50) {
            statistics.above10sum++
        } else if (responseTime > 50 && responseTime <= 100) {
            statistics.above50sum++
        } else if (responseTime > 100 && responseTime <= 500) {
            statistics.above100sum++
        } else if (responseTime > 500 && responseTime <= 1000) {
            statistics.above500sum++
        } else if (responseTime > 1000) {
            statistics.above1000sum++
        }
    }

}
