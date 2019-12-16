package net.jkcode.jkbenchmark.rpc.common.impl

import com.alibaba.fastjson.JSON
import net.jkcode.jkbenchmark.rpc.common.api.MessageEntity
import net.jkcode.jkmvc.db.Db
import net.jkcode.jkutil.cache.ICache
import net.jkcode.jkutil.common.Config
import net.jkcode.jkutil.common.randomInt
import java.io.File
import java.io.InputStreamReader

/**
 * 性能测试服务基类
 * @author shijianhang<772910474@qq.com>
 * @date 2019-12-16 4:52 PM
 */
open class BaseBenchmarkService {

    /**
     * 应用配置
     */
    public val appConfig: Config = Config.instance("bmapp", "yaml")

    /**
     * 基于内存的缓存
     */
    protected val cache = ICache.instance("lru")

    /**
     * json文件
     */
    protected val file = File("messages.json")

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
        val `is` = Thread.currentThread().contextClassLoader.getResourceAsStream("jksoa-benchmark.sql")
        val txt = InputStreamReader(`is`).readText()
        val sqls = txt.split(";\\s+".toRegex())
        for(sql in sqls)
            if(sql.isNotBlank())
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
        val count = Db.instance().queryValue<Int>("select count(1) from message")!!
        if(count > 0)
            return
        for(msg in msgs)
            Db.instance().execute(
                    "insert into message(id, from_uid, to_uid, content) values(?, ?)"
                    , listOf(msg.id, msg.fromUid, msg.toUid, msg.content))
    }

}