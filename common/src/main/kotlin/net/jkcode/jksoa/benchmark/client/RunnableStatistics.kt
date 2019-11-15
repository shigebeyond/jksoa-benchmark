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

/**
 * 单个任务运行统计
 */
class RunnableStatistics(var statisticTime: Int) {
    // Transaction per second
    var TPS: LongArray
    // response times per second
    var RT: LongArray
    // error Transaction per second
    var errTPS: LongArray
    // error response times per second
    var errRT: LongArray

    var above0sum: Long = 0      // [0,1]
    var above1sum: Long = 0      // (1,5]
    var above5sum: Long = 0      // (5,10]
    var above10sum: Long = 0     // (10,50]
    var above50sum: Long = 0     // (50,100]
    var above100sum: Long = 0    // (100,500]
    var above500sum: Long = 0    // (500,1000]
    var above1000sum: Long = 0   // > 1000

    init {
        TPS = LongArray(statisticTime)
        RT = LongArray(statisticTime)
        errTPS = LongArray(statisticTime)
        errRT = LongArray(statisticTime)
    }
}
