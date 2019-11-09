package net.jkcode.jksoa.benchmark.dubbo

import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.service.IMessageService
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
        val context = ClassPathXmlApplicationContext("spring/dubbo-consumer.xml")
        context.start()
        val messageService = context.getBean("messageService", IMessageService::class.java)
        // 测试
        test(args){ i ->
            messageService.getMessageFromDb(i % 10 + 1)
        }
    }

}


