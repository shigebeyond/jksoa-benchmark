package net.jkcode.jksoa.benchmark.jkrpc

import net.jkcode.jksoa.benchmark.common.IBenchmarkApp
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.rpc.client.referer.Referer

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient: IBenchmarkApp("jksoa")  {

    @JvmStatic
    fun main(args: Array<String>) {
        val benchmarkService = Referer.getRefer<IBenchmarkService>()
        /*val r = benchmarkService.getMessageFromDb( 1)
        println(r.get())*/

        // 测试
        runTest(benchmarkService)
    }

}


