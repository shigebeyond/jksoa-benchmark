package net.jkcode.jksoa.benchmark.common.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkmvc.cache.ICache
import net.jkcode.jkmvc.common.randomInt
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * 消息服务
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-29 8:32 PM
 */
class BenchmarkService: IBenchmarkService {

    /**
     * 基于内存的缓存
     */
    private val cache = ICache.instance("lru")

    /**
     * json文件
     */
    private val file = File("messages.json")

    // 至于db的配置见 MessageModel

    init{
        // 初始化测试数据
        initData()
    }

    /**
     * 初始化测试数据
     */
    private fun initData() {
        // 有数据跳过
        if (MessageModel.queryBuilder().count() > 0)
            return

        // 初始化数据
        val msgs = ArrayList<MessageEntity>()
        for (i in 1..10) {
            val msg = MessageEntity()
            msg.id = i
            msg.fromUid = randomInt(10)
            msg.toUid = randomInt(10)
            msg.content = "benchmark message $i"
            msgs.add(msg)
        }

        // 1 写缓存
        for(msg in msgs)
            cache.put(msg.id, msg, 100000000)

        // 2 写文件
        var json = JSON.toJSONString(msgs)
        file.writeText(json)

        // 3 写db
        MessageModel.batchInsert(msgs);
    }

    /**
     * 简单输出
     *   测试序列化, 特别是大对象的序列化
     */
    public override fun echo(request: Any): Any {
        return request
    }

    /**
     * 从缓存中获得消息
     *    测试读缓存
     *
     * @param id
     * @return
     */
    public override fun getMessageFromCache(id: Int): CompletableFuture<MessageEntity> {
        val msg = cache.get(id) as MessageEntity
        return CompletableFuture.completedFuture(msg)
    }

    /**
     * 从文件获得消息
     *    测试读文件
     *
     * @param id
     * @return
     */
    public override fun getMessageFromFile(id: Int): CompletableFuture<MessageEntity> {
        val json = file.readText()
        val msgs = JSONObject.parseArray(json, MessageEntity::class.java)
        val msg = msgs.first{
            it.id == id
        }
        return CompletableFuture.completedFuture(msg)
    }

    /**
     * 从db获得消息
     *   测试读db
     *
     * @param id
     * @return
     */
    public override fun getMessageFromDb(id: Int): CompletableFuture<MessageEntity> {
        val msg = MessageModel.queryBuilder().where("id", "=", id).findEntity<MessageModel, MessageEntity>()
        return CompletableFuture.completedFuture(msg)
    }

}

