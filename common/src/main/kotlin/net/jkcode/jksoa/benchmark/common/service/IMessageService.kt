package net.jkcode.jksoa.benchmark.common.service

import com.alibaba.fastjson.JSONObject
import net.jkcode.jksoa.benchmark.common.entity.MessageEntity
import net.jkcode.jksoa.common.annotation.RemoteService
import java.util.concurrent.CompletableFuture

/**
 * 消息服务
 * @author shijianhang<772910474@qq.com>
 * @date 2019-09-11 2:26 PM
 */
@RemoteService(version = 1)
interface IMessageService {

    /**
     * 从缓存中获得消息
     * @param id
     * @return
     */
    fun getMessageFromCache(id: Int): CompletableFuture<MessageEntity>

    /**
     * 从db获得消息
     * @param id
     * @return
     */
    fun getMessageFromFile(id: Int): CompletableFuture<MessageEntity>

    /**
     * 从db获得消息
     * @param id
     * @return
     */
    fun getMessageFromDb(Id: Int): CompletableFuture<MessageEntity>

}