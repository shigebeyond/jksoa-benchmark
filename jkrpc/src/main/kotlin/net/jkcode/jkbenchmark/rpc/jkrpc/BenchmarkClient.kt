package net.jkcode.jkbenchmark.rpc.jkrpc

import net.jkcode.jkbenchmark.rpc.common.IBenchmarkClient
import net.jkcode.jkbenchmark.rpc.common.api.IBenchmarkService
import net.jkcode.jksoa.rpc.client.referer.Referer

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient: IBenchmarkClient("jksoa")  {

    /**
     * 服务
     */
    override val benchmarkService: Any by lazy {
        val benchmarkService = Referer.getRefer<IBenchmarkService>()
        /*val r = benchmarkService.getMessageFromDb( 1)
        println(r.get())*/
    }

}


