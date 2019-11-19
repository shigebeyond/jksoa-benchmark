package net.jkcode.jksoa.benchmark.common

import net.jkcode.jkmvc.common.Config

/**
 * 性能测试 -- client
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
abstract class IBenchmarkClient(public val name: String /* 测试名 */) {

    /**
     * 列出所有场景的配置
     * @return
     */
    fun listAllConfigs(): ArrayList<Config> {
        val configs = ArrayList<Config>()

        // 构建每个场景的配置
        val actions = arrayOf("nth", "cache", "file", "db")
        val concurrentses = arrayOf(1, 10, 20, 50)
        val requestses = arrayOf(10000, 20000, 30000, 40000, 50000)
        val asyncs = arrayOf(false, true)

        for (concurrents in concurrentses)
            for (requests in requestses)
                for (action in actions)
                    for (async in asyncs) {
                        val map = mapOf<String, Any>(
                                "action" to action, // 动作
                                "concurrents" to concurrents, // 并发数
                                "requests" to requests, // 请求数
                                "warmupRequests" to 1000, // 热身请求数
                                "async" to async // 是否异步
                        )
                        configs.add(Config(map))
                    }
        return configs
    }

    /**
     * 运行 benchmark.yaml 指定场景的性能测试
     * @param benchmarkService
     */
    fun run1Test(benchmarkService: Any){
        val test = BenchmarkTest(name, Config.instance("benchmark", "yaml"))
        test.run(benchmarkService)
    }

    /**
     * 运行所有场景的性能测试
     *    全自动化测试, 多测几遍, 取最优
     *
     * @param benchmarkService
     */
    fun runAllTest(benchmarkService: Any){
        for(config in listAllConfigs()) {
            // 尝试5遍
            val results = (0..5).map {
                val test = BenchmarkTest(name, config)
                test.run(benchmarkService)
            }

            // 取最优: 耗时最短
            val result = results.min()

        }
    }

}