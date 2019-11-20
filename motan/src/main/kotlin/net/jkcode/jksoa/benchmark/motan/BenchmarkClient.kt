package net.jkcode.jksoa.benchmark.motan

import net.jkcode.jkmvc.common.Config
import net.jkcode.jksoa.benchmark.common.BenchmarkTest
import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.motan.IMotanBenchmarkServiceAsync
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient: IBenchmarkClient("motan") {

    @JvmStatic
    fun main(args: Array<String>) {
        val context = ClassPathXmlApplicationContext("motan-client.xml")
        context.start()
        val benchmarkService = context.getBean("asyncBenchmarkService", IMotanBenchmarkServiceAsync::class.java)
        /*val f = benchmarkService.getMessageFromDbAsync(1)
        println(f.value)*/

        // 测试
        runTest(benchmarkService)
    }

}


