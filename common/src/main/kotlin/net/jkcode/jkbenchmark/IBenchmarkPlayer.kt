package net.jkcode.jkbenchmark

import java.util.concurrent.CompletableFuture

/**
 * 性能测试的玩家
 *    1 如果玩家类有异步动作, 则同步动作的默认实现就是调用异步动作的 get() => 你可以不显示同步动作
 *    2 如果玩家类只有同步动作, 你可以不实现异步动作, 但是在场景配置中只能指定 async = false
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
interface IBenchmarkPlayer {

    /**
     * 玩家名, 一般是要做性能测试的技术名
     */
    val name: String;

    /**
     * 获得异步动作
     * @param 动作名
     * @return 对应的方法调用
     */
    fun getAsyncAction(action: String): (Int)->CompletableFuture<*> {
        throw UnsupportedOperationException()
    }

    /**
     * 获得同步动作
     * @param 动作名
     * @return 对应的方法调用
     */
    fun getSyncAction(action: String): (Int)->Any? {
        return { i ->
            getAsyncAction(action).invoke(i).get()
        }
    }

}