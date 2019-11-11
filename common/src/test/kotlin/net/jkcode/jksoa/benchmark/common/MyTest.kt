package net.jkcode.jksoa.benchmark.common

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkmvc.common.randomInt
import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import net.jkcode.jksoa.benchmark.common.impl.BenchmarkService
import org.junit.Test
import net.jkcode.jkmvc.serialize.ISerializer

/**
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 3:03 PM
 */
class MyTest {

    @Test
    fun testJson(){
        val msgs = ArrayList<MessageEntity>()
        for (i in 1..10) {
            val msg = MessageEntity()
            msg.id = i
            msg.fromUid = randomInt(10)
            msg.toUid = randomInt(10)
            msg.content = "hello orm"
            msgs.add(msg)
        }
        // list转json
        var json = JSON.toJSONString(msgs)
        println(json)
        println("-----------------")

        // json转array
        val msgs2 = JSONObject.parseArray(json, MessageEntity::class.java)
        println(msgs2)
    }

    @Test
    fun testService(){
        val service = BenchmarkService()
        val msgFuture = service.getMessageFromDb(1)
        println(msgFuture.get())
    }

    @Test
    fun testSerialize(){
        val i = 1
        val msg = MessageEntity()
        msg.id = i
        msg.fromUid = randomInt(10)
        msg.toUid = randomInt(10)
        msg.content = "benchmark message $i"

        val s = ISerializer.instance("fst")
        val bs = s.serialize(msg)

        val msg2 = s.unserialize(bs!!) as MessageEntity
        println(msg2)
    }
}