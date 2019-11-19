package net.jkcode.jksoa.benchmark.rpc

import net.jkcode.jkmvc.common.Config
import net.jkcode.jksoa.benchmark.common.BenchmarkTest
import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.rpc.client.referer.Referer
import java.util.concurrent.CompletableFuture

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient: IBenchmarkClient("jksoa")  {

    @JvmStatic
    fun main(args: Array<String>) {
        val benchmarkService = Referer.getRefer<IBenchmarkService>()
        /*val r = benchmarkService.getMessageFromDb( 1)
        println(r.get())*/

        // 测试
        //run1Test(benchmarkService)
        runAllTest(benchmarkService)
    }

}


