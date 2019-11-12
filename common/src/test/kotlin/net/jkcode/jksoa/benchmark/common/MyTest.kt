package net.jkcode.jksoa.benchmark.common

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkmvc.common.randomInt
import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import net.jkcode.jksoa.benchmark.common.impl.BenchmarkService
import org.junit.Test
import net.jkcode.jkmvc.serialize.ISerializer
import net.jkcode.jksoa.benchmark.common.impl.MessageModel
import net.jkcode.jksoa.common.RpcResponse
import java.io.File

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

    @Test
    fun testByte(){
        val s = ISerializer.instance("fst")
        val msg = MessageModel.queryBuilder().where("id", "=", 1).findEntity<MessageModel, MessageEntity>()
        val res = RpcResponse(114137583746809856, msg)
        println(res)
        val bs = s.serialize(res)!!

        val msg2 = s.unserialize(bs)
        println(msg2)


    }

    @Test
    fun testByte2(){
        println("------------- 原始的 -------------")
        val s = ISerializer.instance("fst")
        val msg = MessageModel.queryBuilder().where("id", "=", 1).findEntity<MessageModel, MessageEntity>()
        val res = RpcResponse(114137583746809856, msg)
        println(res)
        val bs = s.serialize(res)!!

        // server发送的
        println("------------- server发送的 -------------")
        val bs2 = File("/home/shi/test/benchmark/server.data").readBytes()
        checkEquals(bs, bs2)
        val res2 = s.unserialize(bs2!!) as RpcResponse
        println(res2)
        println(res.value == res2.value)

        // client收到的
        println("------------- client收到的 -------------")
        val bs3 = File("/home/shi/test/benchmark/client.data").readBytes()
        checkEquals(bs2, bs3)
        val res3 = s.unserialize(bs3!!) as RpcResponse
        println(res3)
        println(res.value == res3.value)
    }

    private fun checkEquals(bs1: ByteArray, bs2: ByteArray) {
        println(bs1 == bs2)
        for (i in 0 until 160) {
            val v2 = bs1[i]
            val v3 = bs2[i]
            if (v2 != v3)
                println("第 $i 个字节不等: $v2 != $v3")
        }
    }
}