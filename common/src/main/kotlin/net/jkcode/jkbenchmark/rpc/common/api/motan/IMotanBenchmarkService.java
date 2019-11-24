package net.jkcode.jkbenchmark.rpc.common.api.motan;

import com.weibo.api.motan.transport.async.MotanAsync;
import net.jkcode.jkbenchmark.rpc.common.api.MessageEntity;

@MotanAsync
public interface IMotanBenchmarkService {

    /**
     * 啥都不干
     */
    Void doNothing(int id);

    /**
     * 简单输出
     *   测试序列化, 特别是大对象的序列化
     */
    Object echo(Object request);

    /**
     * 从缓存中获得消息
     *    测试读缓存
     *
     * @param id
     * @return
     */
    MessageEntity getMessageFromCache(int id);

    /**
     * 从文件获得消息
     *   测试读文件
     *
     * @param id
     * @return
     */
    MessageEntity getMessageFromFile(int id);

    /**
     * 从db获得消息
     *   测试读db
     *
     * @param id
     * @return
     */
    MessageEntity getMessageFromDb(int id);

}