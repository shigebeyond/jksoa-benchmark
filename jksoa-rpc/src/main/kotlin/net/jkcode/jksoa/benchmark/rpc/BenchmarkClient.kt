package net.jkcode.jksoa.benchmark.rpc

import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.rpc.client.referer.Referer

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient : IBenchmarkClient {

    @JvmStatic
    fun main(args: Array<String>) {
        val benchmarkService = Referer.getRefer<IBenchmarkService>()
        // 测试
        test(args){ i ->
            benchmarkService.getMessageFromDb(i % 10 + 1)
        }
    }

}


