package net.jkcode.jksoa.benchmark.common.api

import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import net.jkcode.jksoa.common.annotation.RemoteService
import java.util.concurrent.CompletableFuture

/**
 * 消息服务
 * @author shijianhang<772910474@qq.com>
 * @date 2019-09-11 2:26 PM
 */
@RemoteService(version = 1)
interface IBenchmarkService {

    /**
     * 简单输出
     *   测试序列化, 特别是大对象的序列化
     */
    fun echo(request: Any): Any

    /**
     * 从缓存中获得消息
     *    测试读缓存
     *
     * @param id
     * @return
     */
    fun getMessageFromCache(id: Int): CompletableFuture<MessageEntity>

    /**
     * 从文件获得消息
     *   测试读文件
     *
     * @param id
     * @return
     */
    fun getMessageFromFile(id: Int): CompletableFuture<MessageEntity>

    /**
     * 从db获得消息
     *   测试读db
     *
     * @param id
     * @return
     */
    fun getMessageFromDb(Id: Int): CompletableFuture<MessageEntity>

    /**
     * 从db获得消息
     *   测试读db
     *
     * @param id
     * @return
     */
    fun getMessageFromDb2(id: Int): MessageEntity

}