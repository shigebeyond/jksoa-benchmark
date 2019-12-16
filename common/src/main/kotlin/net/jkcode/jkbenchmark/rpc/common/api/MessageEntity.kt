package net.jkcode.jkbenchmark.rpc.common.api

import net.jkcode.jkmvc.orm.OrmEntity

/**
 * 消息实体
 * @author shijianhang<772910474@qq.com>
 * @date 2019-06-27 2:53 PM
 */
class MessageEntity {

    // 代理属性读写
    public var id:Int = 0 // 消息id

    public var fromUid:Int = 0 // 发送人id

    public var toUid:Int = 0 // 接收人id

    public var content:String = "" // 消息内容

    override fun toString(): String {
        return "MessageEntity{id=$id, from_uid=$fromUid, to_uid=$toUid, content='$content'}"
    }

}