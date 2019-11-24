package net.jkcode.jkbenchmark

import net.jkcode.jkbenchmark.analyze.BenchmarkResultAnalyzer
import net.jkcode.jkutil.common.Config
import org.slf4j.LoggerFactory

/**
 * 性能测试应用
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
class BenchmarkApp(public val player: IBenchmarkPlayer) {

    companion object{

        /**
         * 所有场景测试的过程日志
         */
        public val roundLogger = LoggerFactory.getLogger("net.jkcode.jkbenchmark.round")

        /**
         * 所有场景测试的结果日志
         * 格式: 2019-11-20 17:00:28 [INFO] file-c10-n40000-syn | Runtime: 1431.09 ms, Avg TPS: 27950.64, Avg RT: 0.36ms
         */
        public val resultLogger = LoggerFactory.getLogger("net.jkcode.jkbenchmark.result")

        /**
         * 调试的配置
         */
        public val appConfig: Config = Config.instance("app", "yaml")

        @JvmStatic
        fun main(args: Array<String>) {
            if(args.isEmpty())
                throw IllegalArgumentException("未指定玩家类型")

            // 第一个参数为玩家类
            val playerClass = args[0]
            // 实例化玩家
            val player = Class.forName(playerClass).newInstance() as IBenchmarkPlayer
            // 运行app
            BenchmarkApp(player).run()
            // 解析result.log
            BenchmarkResultAnalyzer.parseResultLog("logs/")
        }

    }


    /**
     * 列出所有场景的配置
     * @return
     */
    public fun listAllSceneConfigs(): List<Config> {
        val allConfig = Config.instance("scenes", "yaml")
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
     * 测试指定场景, 全自动化测试, 多测几遍, 取最优
     *    如果 app.yaml 中 all = true, 则测试 scenes.yaml 所有场景
     *    否则, 测试 scene.yaml 单一场景
     */
    public fun run(){
        // 获得场景配置
        val sceneConfigs: List<Config> =
                if(appConfig["all"]!!) { // 测试 scenes.yaml 所有场景
                    roundLogger.info("Player [${player.name}] Run all scenes in scenes.yaml\n")
                    listAllSceneConfigs()
                }else{ // 测试 scene.yaml 单一场景
                    roundLogger.info("Player [${player.name}] Run 1 scene1 in scene.yaml\n")
                    listOf(Config.instance("scene", "yaml"))
                }

        // 遍历每个场景来测试
        for(sceneConfig in sceneConfigs) {
            // 尝试多遍
            val results = ArrayList<BenchmarkResult>()
            val roundCount: Int = appConfig["roundCount"]!!
            if(roundCount < 1)
                throw Exception("配置项[roundCount]必须为正整数")
            val scene = BenchmarkScene(player, sceneConfig)
            roundLogger.info("---------- Run scene [$scene] $roundCount rounds ----------")

            for(i in 0 until roundCount) {
                // 测试
                val result = scene.run()
                results.add(result)
                // 直接打印
                roundLogger.info("+++ Round ${i + 1} result: \n$result\n")
                Thread.sleep(3000)
            }
            // 取最优结果: 耗时最短
            val bestResult = results.min()!!
            roundLogger.info(">>> Best result: \n$bestResult\n")
            resultLogger.info(scene.name + " | " + bestResult.toSummary())
        }
    }

}