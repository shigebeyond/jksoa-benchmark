package net.jkcode.jksoa.benchmark.common

import net.jkcode.jkmvc.common.Config
import org.slf4j.LoggerFactory

/**
 * 性能测试 -- client
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
abstract class IBenchmarkClient(public val name: String /* 测试名 */) {

    /**
     * 调试的配置
     */
    public val debugConfig: Config = Config.instance("debug", "yaml")

    /**
     * 日志
     */
    public val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 列出所有场景的配置
     * @return
     */
    public fun listAllConfigs(): ArrayList<Config> {
        val allConfig = Config.instance("benchmarks", "yaml")
        val configs = ArrayList<Config>()

        // 构建每个场景的配置
        val actions:List<String> = allConfig["action"]!!
        val concurrentses: List<Int> = allConfig["concurrents"]!!
        val requestses:List<Int> = allConfig["requests"]!!
        val asyncs: List<Boolean> = allConfig["async"]!!

        for (concurrents in concurrentses)
            for (requests in requestses)
                for (action in actions)
                    for (async in asyncs) {
                        val map = mapOf<String, Any>(
                                "action" to action, // 动作
                                "concurrents" to concurrents, // 并发数
                                "requests" to requests, // 请求数
                                "async" to async // 是否异步
                        )
                        configs.add(Config(map))
                    }
        return configs
    }

    /**
     * 运行性能测试
     * @param benchmarkService
     */
    public fun runTest(benchmarkService: Any){
        if(debugConfig["all"]!!)
            runAllTest(benchmarkService)
        else
            run1Test(benchmarkService)
    }

    /**
     * 运行 benchmark.yaml 单一场景的性能测试
     * @param benchmarkService
     */
    protected fun run1Test(benchmarkService: Any){
        val test = BenchmarkTest(name, Config.instance("benchmark", "yaml"))
        test.run(benchmarkService)
    }

    /**
     * 运行 benchmarks.yaml 所有场景的性能测试
     *    全自动化测试, 多测几遍, 取最优
     *
     * @param benchmarkService
     */
    protected fun runAllTest(benchmarkService: Any){
        for(config in listAllConfigs()) {
            // 尝试5遍
            // 取最优: 耗时最短
            // 直接打印吧
            for(i in 0..5) {
                val test = BenchmarkTest(name, config)
                val result = test.run(benchmarkService)
                logger.info("----------$name Benchmark Statistics--------------\n${config.props}\n$result")
            }
        }
    }

}