package net.jkcode.jksoa.benchmark.rpc

import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.service.IMessageService
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
        val service = Referer.getRefer<IMessageService>()
        // 测试
        test(args){ i ->
            service.getMessageFromDb(i % 10 + 1)
        }
    }

}


