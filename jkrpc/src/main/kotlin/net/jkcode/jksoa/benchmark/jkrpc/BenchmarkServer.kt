package net.jkcode.jksoa.benchmark.jkrpc

import net.jkcode.jksoa.rpc.server.RpcServerLauncher

/**
 * 性能测试-server
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 11:00 AM
 */
object BenchmarkServer {

    @JvmStatic
    fun main(args: Array<String>) {
        RpcServerLauncher.main(args)
    }

}