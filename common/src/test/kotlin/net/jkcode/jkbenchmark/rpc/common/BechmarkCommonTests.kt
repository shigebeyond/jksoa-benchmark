package net.jkcode.jkbenchmark.rpc.common

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkbenchmark.rpc.common.api.MessageEntity
import net.jkcode.jkbenchmark.rpc.common.impl.BenchmarkService
import net.jkcode.jkbenchmark.rpc.common.impl.motan.MotanBenchmarkService
import net.jkcode.jksoa.common.RpcResponse
import net.jkcode.jkutil.common.CommonThreadPool
import net.jkcode.jkutil.common.randomInt
import net.jkcode.jkutil.serialize.ISerializer
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 3:03 PM
 */
class BechmarkCommonTests {

    @Test
    fun testService(){
        val service = BenchmarkService()
        val msgFuture = service.getMessageFromDb(1)
        println(msgFuture.get())
    }

    @Test
    fun testMotanService(){
        val service = MotanBenchmarkService()
        val msg = service.getMessageFromDb(1)
        println(msg)
    }

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