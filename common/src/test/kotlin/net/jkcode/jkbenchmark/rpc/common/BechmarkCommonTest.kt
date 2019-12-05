package net.jkcode.jkbenchmark.rpc.common

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkutil.common.randomInt
import net.jkcode.jkbenchmark.rpc.common.api.MessageEntity
import net.jkcode.jkbenchmark.rpc.common.impl.BenchmarkService
import org.junit.Test
import net.jkcode.jkutil.serialize.ISerializer
import net.jkcode.jkbenchmark.rpc.common.impl.MessageModel
import net.jkcode.jksoa.common.RpcResponse
import net.jkcode.jkutil.common.CommonThreadPool
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import kotlin.collections.ArrayList

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
        val msg = MessageEntity()
        msg.id = 1
        msg.fromUid = 1
        msg.toUid = 1
        msg.content = "hello orm"
        val res = RpcResponse(1, msg)

        val s = ISerializer.instance("fst")
        val bs = s.serialize(res)!!
        File("/home/shi/test/benchmark/original.data").writeBytes(bs)

        val res2 = s.unserialize(bs)
        println(res2)

        val bs3 = File("/home/shi/test/benchmark/original.data").readBytes()
        val res3 = s.unserialize(bs3)
        println(res3)
    }

    @Test
    fun testUnserialize(){
        // 有以下语句则成功, 否则报空指针异常
        //val msg = MessageEntity()

        val bs3 = File("/home/shi/test/benchmark/original.data").readBytes()
        val s = ISerializer.instance("fst")
        val res3 = s.unserialize(bs3)
        println(res3)
    }

    @Test
    fun testFile(){
        val bs = File("/home/shi/test/benchmark/client.data").readBytes()
        val bs2 = File("/home/shi/test/benchmark/server.data").readBytes()
        val bs3 = File("/home/shi/test/benchmark/original.data").readBytes()
        checkEquals(bs, bs2)
        checkEquals(bs2, bs3)

        val s = ISerializer.instance("fst")
        val msg = s.unserialize(bs)
        println(msg)
        val msg2 = s.unserialize(bs2)
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
        //println(bs1 == bs2)
        println(Arrays.equals(bs1, bs2))
        for (i in 0 until bs1.size) {
            val v2 = bs1[i]
            val v3 = bs2[i]
            if (v2 != v3)
                println("第 $i 个字节不等: $v2 != $v3")
        }
    }

    @Test
    fun testThreadPool() {
        val pool = Executors.newFixedThreadPool(2)
//        val pool = net.jkcode.jkutil.common.StandardThreadExecutor(2, 2, Integer.MAX_VALUE)
//        val pool = com.weibo.api.motan.core.StandardThreadExecutor(2, 2, Integer.MAX_VALUE - 100)

        val requests = 500000
        val results = (0..4).map {
            val latch = CountDownLatch(requests)
            val start = System.currentTimeMillis()
            for (i in 0..requests) {
                pool.execute {
                    println(i)
                    latch.countDown()
                }
            }
            latch.await()
            val runtime = System.currentTimeMillis() - start
            println("耗时 $runtime ms")
            runtime
        }

        println("最小耗时 ${results.min()} ms")
    }

    @Test
    fun testThreadPool2() {
        CommonThreadPool.execute {
            println(Thread.currentThread().name)
        }
    }
}