package net.jkcode.jksoa.benchmark.dubbo

import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient : IBenchmarkClient {

    @JvmStatic
    fun main(args: Array<String>) {
        val context = ClassPathXmlApplicationContext("motan-client.xml")
        context.start()
        val benchmarkService = context.getBean("benchmarkService", IBenchmarkService::class.java)
        // 测试
        test(args){ i ->
            benchmarkService.getMessageFromDb(i % 10 + 1)
        }
    }

}


