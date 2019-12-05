package net.jkcode.jkbenchmark.rpc.dubbo

import net.jkcode.jkbenchmark.rpc.common.IBenchmarkClient
import net.jkcode.jkbenchmark.rpc.common.api.IBenchmarkService
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
class BenchmarkClient: IBenchmarkClient("dubbo") {

    /**
     * 服务
     */
    override val benchmarkService: Any by lazy{
        val context = ClassPathXmlApplicationContext("spring/dubbo-consumer.xml")
        context.start()
        val benchmarkService = context.getBean("benchmarkService", IBenchmarkService::class.java)
        /*val r = benchmarkService.getMessageFromDb( 1)
        println(r.get())*/
        benchmarkService
    }


}


