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

import java.text.MessageFormat

/**
 * client运行统计
 */
class ClientStatistics(var statistics: List<RunnableStatistics>) {
    var statisticTime: Int = 0
    var above0sum: Long = 0      // [0,1]
    var above1sum: Long = 0      // (1,5]
    var above5sum: Long = 0      // (5,10]
    var above10sum: Long = 0     // (10,50]
    var above50sum: Long = 0     // (50,100]
    var above100sum: Long = 0    // (100,500]
    var above500sum: Long = 0    // (500,1000]
    var above1000sum: Long = 0   // > 1000

    var maxTPS: Long = 0
    var minTPS: Long = 0
    var succTPS: Long = 0
    var succRT: Long = 0
    var errTPS: Long = 0
    var errRT: Long = 0
    var allTPS: Long = 0
    var allRT: Long = 0

    init {
        statisticTime = statistics[0].statisticTime
    }

    fun collectStatistics() {
        for (statistic in statistics) {
            above0sum += statistic.above0sum
            above1sum += statistic.above1sum
            above5sum += statistic.above5sum
            above10sum += statistic.above10sum
            above50sum += statistic.above50sum
            above100sum += statistic.above100sum
            above500sum += statistic.above500sum
            above1000sum += statistic.above1000sum
        }
        for (i in 0 until statistics[0].statisticTime) {
            var runnableTPS: Long = 0
            for (statistic in statistics) {
                runnableTPS += statistic.TPS[i] + statistic.errTPS[i]
                succTPS += statistic.TPS[i]
                succRT += statistic.RT[i]
                errTPS += statistic.errTPS[i]
                errRT += statistic.errRT[i]
            }
            if (runnableTPS > maxTPS) {
                maxTPS = runnableTPS
            }
            if (runnableTPS < minTPS || minTPS == 0L) {
                minTPS = runnableTPS
            }
        }
        allTPS = succTPS + errTPS
        allRT = succRT + errRT
    }

    fun printStatistics() {
        println("Benchmark Run Time: $statisticTime")
        println(MessageFormat.format("Requests: {0}, Success: {1}%({2}), Error: {3}%({4})", allTPS, succTPS * 100 / allTPS, succTPS, errTPS * 100 / allTPS, errTPS))
        println(MessageFormat.format("Avg TPS: {0}, Max TPS: {1}, Min TPS: {2}", allTPS / statisticTime, maxTPS, minTPS))
        println(MessageFormat.format("Avg ResponseTime: {0}ms", allRT.toFloat() / allTPS.toFloat() / 1000f))

        println(MessageFormat.format("RT [0,1]: {0}% {1}/{2}", above0sum * 100 / allTPS, above0sum, allTPS))
        println(MessageFormat.format("RT (1,5]: {0}% {1}/{2}", above1sum * 100 / allTPS, above1sum, allTPS))
        println(MessageFormat.format("RT (5,10]: {0}% {1}/{2}", above5sum * 100 / allTPS, above5sum, allTPS))
        println(MessageFormat.format("RT (10,50]: {0}% {1}/{2}", above10sum * 100 / allTPS, above10sum, allTPS))
        println(MessageFormat.format("RT (50,100]: {0}% {1}/{2}", above50sum * 100 / allTPS, above50sum, allTPS))
        println(MessageFormat.format("RT (100,500]: {0}% {1}/{2}", above100sum * 100 / allTPS, above100sum, allTPS))
        println(MessageFormat.format("RT (500,1000]: {0}% {1}/{2}", above500sum * 100 / allTPS, above500sum, allTPS))
        println(MessageFormat.format("RT >1000: {0}% {1}/{2}", above1000sum * 100 / allTPS, above1000sum, allTPS))
    }
}
