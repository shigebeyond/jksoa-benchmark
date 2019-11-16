package net.jkcode.jksoa.benchmark.dubbo

import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.rpc.client.referer.Referer
import org.springframework.context.support.ClassPathXmlApplicationContext
import java.util.concurrent.CompletableFuture

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient : IBenchmarkClient() {

    @JvmStatic
    fun main(args: Array<String>) {
        val context = ClassPathXmlApplicationContext("spring/dubbo-consumer.xml")
        context.start()
        val benchmarkService = context.getBean("benchmarkService", IBenchmarkService::class.java)
        val action: (Int) -> CompletableFuture<*> =
                when(config.getString("action")!!){
                    "cache" -> benchmarkService::getMessageFromCache
                    "file" -> benchmarkService::getMessageFromFile
                    "db" -> benchmarkService::getMessageFromDb
                    else -> throw Exception("不能识别action配置: " + config.getString("action"))
                }
        // 测试
        test(action)
    }

}


