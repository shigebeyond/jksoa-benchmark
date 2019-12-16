package net.jkcode.jkbenchmark.rpc.common.impl.motan

import com.alibaba.fastjson.JSONObject
import net.jkcode.jkbenchmark.rpc.common.api.MessageEntity
import net.jkcode.jkbenchmark.rpc.common.api.motan.IMotanBenchmarkService
import net.jkcode.jkbenchmark.rpc.common.impl.BaseBenchmarkService
import net.jkcode.jkmvc.db.Db

/**
 * 性能测试服务
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-29 8:32 PM
 */
class MotanBenchmarkService: IMotanBenchmarkService, BaseBenchmarkService() {

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
                msg.id = row["id"]!!
                msg.fromUid = row["from_uid"]!!
                msg.toUid = row["to_uid"]!!
                msg.content = row["content"]!!
                msg
            }
        }finally {
            // 关闭db
            Db.instance().closeAndClear()
        }
    }

}

