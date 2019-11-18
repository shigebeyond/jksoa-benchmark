package net.jkcode.jksoa.benchmark.motan

import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.benchmark.common.api.motan.IMotanBenchmarkServiceAsync
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
        val context = ClassPathXmlApplicationContext("motan-client.xml")
        context.start()
        val benchmarkService = context.getBean("asyncBenchmarkService", IMotanBenchmarkServiceAsync::class.java)
        /*val f = benchmarkService.getMessageFromDbAsync(1)
        println(f.value)*/
        val action: (Int) -> CompletableFuture<*> = getMotanAction(benchmarkService)
        // 测试
        test(action)
    }

}


