package net.jkcode.jkbenchmark.rpc.motan

import net.jkcode.jkbenchmark.BenchmarkApp
import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkbenchmark.rpc.common.IBenchmarkClient
import net.jkcode.jkbenchmark.rpc.common.api.motan.IMotanBenchmarkServiceAsync
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
class BenchmarkClient: IBenchmarkClient("motan") {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            // 运行app
            BenchmarkApp(BenchmarkClient()).run()
        }
    }

    /**
     * 服务
     */
    override val benchmarkService: Any by lazy{
        val context = ClassPathXmlApplicationContext("motan-client.xml")
        context.start()
        val benchmarkService = context.getBean("asyncBenchmarkService", IMotanBenchmarkServiceAsync::class.java)
        /*val f = benchmarkService.getMessageFromDbAsync(1)
        println(f.value)*/
        benchmarkService
    }

}


