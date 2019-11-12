package net.jkcode.jksoa.benchmark.rpc

import net.jkcode.jkmvc.common.randomInt
import net.jkcode.jksoa.benchmark.common.IBenchmarkClient
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import net.jkcode.jksoa.benchmark.common.impl.MessageModel
import net.jkcode.jksoa.rpc.client.referer.Referer

/**
 * 性能测试
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
object BenchmarkClient : IBenchmarkClient {

    @JvmStatic
    fun main(args: Array<String>) {
        val benchmarkService = Referer.getRefer<IBenchmarkService>()

        /*val msg = MessageEntity()
        msg.id = 1
        msg.fromUid = randomInt(10)
        msg.toUid = randomInt(10)
        msg.content = "hello orm"*/
        val msg = MessageModel.queryBuilder().where("id", "=", 1).findEntity<MessageModel, MessageEntity>()!!
        val msg2 = benchmarkService.echo(msg)
        println(msg2)

//        val msg = benchmarkService.getMessageFromDb2(1)
//        println(msg)

//        val f = benchmarkService.getMessageFromDb(2)
//        println(f.get())
        // 测试
        /*test(args){ i ->
            benchmarkService.getMessageFromDb(i % 10 + 1)
        }*/
    }

}


