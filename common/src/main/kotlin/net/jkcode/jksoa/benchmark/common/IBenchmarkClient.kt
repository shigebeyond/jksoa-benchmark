package net.jkcode.jksoa.benchmark.common

import net.jkcode.jkutil.common.Config
import org.slf4j.LoggerFactory

/**
 * 性能测试 -- client
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
abstract class IBenchmarkClient(public val tech: String /* 技术: jksoa/dubbo/motan */) {

    /**
     * 调试的配置
     */
    public val debugConfig: Config = Config.instance("debug", "yaml")

    /**
     * 所有场景测试的过程日志
     */
    public val roundLogger = LoggerFactory.getLogger("net.jkcode.jksoa.benchmark.round")

    /**
     * 所有场景测试的结果日志
     * 格式: 2019-11-20 17:00:28 [INFO] file-c10-n40000-syn | Runtime: 1431.09 ms, Avg TPS: 27950.64, Avg RT: 0.36ms
     */
    public val resultLogger = LoggerFactory.getLogger("net.jkcode.jksoa.benchmark.result")

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

        for (action in actions)
            for (concurrents in concurrentses)
                for (requests in requestses)
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
        roundLogger.info("----------$tech Run1Test\n")
        val test = BenchmarkTest(tech, Config.instance("benchmark", "yaml"))
        val result = test.run(benchmarkService)
        roundLogger.info("result: \n$result\n")
    }

    /**
     * 运行 benchmarks.yaml 所有场景的性能测试
     *    全自动化测试, 多测几遍, 取最优
     *
     * @param benchmarkService
     */
    protected fun runAllTest(benchmarkService: Any){
        roundLogger.info("----------$tech RunAllTest\n")
        for(config in listAllConfigs()) {
            roundLogger.info("----------$tech Benchmark Statistics--------------\n${config.props}\n")
            // 尝试多遍
            val results = ArrayList<BenchmarkResult>()
            val roundCount: Int = debugConfig["roundCount"]!!
            if(roundCount < 1)
                throw Exception("配置项[roundCount]必须为正整数")
            for(i in 0 until roundCount) {
                // 测试
                val test = BenchmarkTest(tech, config)
                val result = test.run(benchmarkService)
                results.add(result)
                // 直接打印
                roundLogger.info("+++ Round ${i + 1} result: \n$result\n")
                Thread.sleep(3000)
            }
            // 取最优结果: 耗时最短
            val bestResult = results.min()!!
            roundLogger.info(">>> Best result: \n$bestResult\n")
            resultLogger.info(toSummary(config) + " | " + bestResult.toSummary())
        }
    }

    /**
     * 简写配置名
     * @param config
     * @return
     */
    protected fun toSummary(config: Config): String {
        val action: String = config["action"]!! // 动作
        val concurrents: Int = config["concurrents"]!! // 线程数/并发数
        val requests: Int = config["requests"]!! // 请求数
        val async: Boolean = config["async"]!! // 是否异步
        return "$action-c$concurrents-n$requests-" + if(async) "asyn" else "syn"
    }

}