package net.jkcode.jksoa.benchmark.motan

import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 性能测试-server
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 11:00 AM
 */
object BenchmarkServer {

    @JvmStatic
    fun main(args: Array<String>) {
        val applicationContext = ClassPathXmlApplicationContext("classpath*:motan-server.xml")

        println("server running---")
        Thread.sleep(java.lang.Long.MAX_VALUE)
    }

}