package net.jkcode.jkbenchmark.rpc.common.impl.motan

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkutil.cache.ICache
import net.jkcode.jkutil.common.Config
import net.jkcode.jkutil.common.randomInt
import net.jkcode.jkmvc.db.Db
import net.jkcode.jkbenchmark.rpc.common.api.MessageEntity
import net.jkcode.jkbenchmark.rpc.common.api.motan.IMotanBenchmarkService
import net.jkcode.jkbenchmark.rpc.common.impl.MessageModel
import java.io.File
import java.io.InputStreamReader

/**
 * 性能测试服务
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-29 8:32 PM
 */
class MotanBenchmarkService: IMotanBenchmarkService {

    /**
     * 应用配置
     */
    public val appConfig: Config = Config.instance("app", "yaml")

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
        // 建表
        createTable()

        // 初始化数据
        initData()
    }

    /**
     * 建表: message
     */
    private fun createTable() {
        val `is` = Thread.currentThread().contextClassLoader.getResourceAsStream("benchmark.sql")
        val sql = InputStreamReader(`is`).readText()
        Db.instance().execute(sql)
    }

    /**
     * 初始化数据
     */
    private fun initData() {
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
    public override fun doNothing(i: Int): Void?{
        return null
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
     * @param i
     * @return
     */
    public override fun getMessageFromCache(i: Int): MessageEntity? {
        val id = i  % 10 + 1
        return cache.get(id) as MessageEntity
    }

    /**
     * 从文件获得消息
     *    测试读文件
     *
     * @param i
     * @return
     */
    public override fun getMessageFromFile(i: Int): MessageEntity? {
        val id = i  % 10 + 1
        val json = file.readText()
        val msgs = JSONObject.parseArray(json, MessageEntity::class.java)
        val msg = msgs.first{
            it.id == id
        }
        return msg
    }

    /**
     * 从db获得消息
     *   测试读db
     *
     * @param i
     * @return
     */
    public override fun getMessageFromDb(i: Int): MessageEntity? {
        try {
            val id = i % 10 + 1
            return Db.instance().queryRow("select * from message where id = $id", emptyList()) { row ->
                val msg = MessageEntity()
                msg.fromRow(row, true)
                msg
            }
        }finally {
            // 关闭db
            Db.instance().closeAndClear()
        }
    }

}

