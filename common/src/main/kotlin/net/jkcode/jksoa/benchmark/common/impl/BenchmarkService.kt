package net.jkcode.jksoa.benchmark.common.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkmvc.cache.ICache
import net.jkcode.jkmvc.common.Config
import net.jkcode.jkmvc.common.getInt
import net.jkcode.jkmvc.common.getString
import net.jkcode.jkmvc.common.randomInt
import net.jkcode.jkmvc.db.Db
import net.jkcode.jksoa.benchmark.common.api.IBenchmarkService
import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

/**
 * 性能测试服务
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-29 8:32 PM
 */
class BenchmarkService: IBenchmarkService {

    /**
     * 调试配置
     */
    public val debugConfig: Config = Config.instance("debug", "yaml")

    /**
     * 基于内存的缓存
     */
    protected val cache = ICache.instance("lru")

    /**
     * json文件
     */
    protected val file = File("messages.json")

    // 至于db的配置见 MessageModel

    init{
        // 建表
        createTable()

        // 初始化数据
        initData()
    }

    /**
     * 建表: message
     */
    protected fun createTable() {
        val `is` = Thread.currentThread().contextClassLoader.getResourceAsStream("message.mysql.sql")
        val sql = InputStreamReader(`is`).readText()
        Db.instance().execute(sql)
    }

    /**
     * 初始化数据
     */
    protected fun initData() {
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
        if(MessageModel.queryBuilder().count() == 0)
            MessageModel.batchInsert(msgs);
    }

    /**
     * 啥都不干
     */
    public override fun doNothing(id: Int): CompletableFuture<Void>{
        return CompletableFuture.completedFuture(null)
    }

    /**
     * 简单输出
     *   测试序列化, 特别是大对象的序列化
     */
    public override fun echo(request: Any): CompletableFuture<Any> {
        return CompletableFuture.completedFuture(request)
    }

    /**
     * 从缓存中获得消息
     *    测试读缓存
     *
     * @param i
     * @return
     */
    public override fun getMessageFromCache(i: Int): CompletableFuture<MessageEntity> {
        val id = i  % 10 + 1
        val msg = cache.get(id  % 10 + 1) as MessageEntity
        return CompletableFuture.completedFuture(msg)
    }

    /**
     * 从文件获得消息
     *    测试读文件
     *
     * @param i
     * @return
     */
    public override fun getMessageFromFile(i: Int): CompletableFuture<MessageEntity> {
        val id = i  % 10 + 1
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
     * @param i
     * @return
     */
    public override fun getMessageFromDb(i: Int): CompletableFuture<MessageEntity> {
        try {
            val id = i % 10 + 1
            val msg = Db.instance().queryRow("select * from message where id = $id") { row ->
                        val msg = MessageEntity()
                        msg.fromMap(row)
                        msg
                    }
            return CompletableFuture.completedFuture(msg)
        }finally {
            // 关闭db
            Db.instance().closeAndClear()
        }
    }

}

