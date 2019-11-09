package net.jkcode.jksoa.benchmark.dubbo

import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 性能测试-server
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 11:00 AM
 */
object BenchmarkServer {

    @JvmStatic
    fun main(args: Array<String>) {
        val context = ClassPathXmlApplicationContext("spring/dubbo-provider.xml")
        context.start()
        System.`in`.read()
    }

}