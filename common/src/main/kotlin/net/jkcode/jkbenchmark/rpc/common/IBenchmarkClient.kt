package net.jkcode.jkbenchmark.rpc.common

import com.weibo.api.motan.rpc.ResponseFuture
import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkbenchmark.rpc.common.api.IBenchmarkService
import net.jkcode.jkbenchmark.rpc.common.api.motan.IMotanBenchmarkServiceAsync
import java.util.concurrent.CompletableFuture

/**
 * 性能测试玩家/即rpc client
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
abstract class IBenchmarkClient(public override val name: String): IBenchmarkPlayer {

    /**
     * 服务
     */
    public abstract val benchmarkService: Any

    /**
     * 获得异步动作
     * @param 动作名
     * @return 对应的方法调用
     */
    override fun getAsyncAction(action: String): (Int)->CompletableFuture<*> {
        return when(benchmarkService){
                    is IBenchmarkService -> getNormalAction(benchmarkService as IBenchmarkService, action)
                    is IMotanBenchmarkServiceAsync -> getMotanAction(benchmarkService as IMotanBenchmarkServiceAsync, action)
                    else -> throw IllegalArgumentException("不能识别 benchmarkService 类型: " + benchmarkService.javaClass)
                }
    }

    /**
     * 获得正常的动作(测试调用的方法)
     *   方法返回类型就是 CompletableFuture
     *
     * @param benchmarkService
     * @return
     */
    protected fun getNormalAction(benchmarkService: IBenchmarkService, action: String): (Int) -> CompletableFuture<*> {
        return when (action) {
                    "nth" -> benchmarkService::doNothing
                    "cache" -> benchmarkService::getMessageFromCache
                    "file" -> benchmarkService::getMessageFromFile
                    "db" -> benchmarkService::getMessageFromDb
                    else -> throw Exception("不能识别action配置: " + action)
                }
    }

    /**
     * 获得motan的动作(测试调用的方法)
     *    方法返回类型就是 DefaultResponseFuture
     *
     * @param benchmarkService
     * @return
     */
    protected fun getMotanAction(benchmarkService: IMotanBenchmarkServiceAsync, action: String): (Int) -> CompletableFuture<*> {
        val action: (Int) -> ResponseFuture =
                when (action) {
                    "nth" -> benchmarkService::doNothingAsync
                    "cache" -> benchmarkService::getMessageFromCacheAsync
                    "file" -> benchmarkService::getMessageFromFileAsync
                    "db" -> benchmarkService::getMessageFromDbAsync
                    else -> throw Exception("不能识别action配置: " + action)
                }
        val action2: (Int) -> CompletableFuture<*> = { id:Int ->
            val f = action.invoke(id)
            toCompletableFuture(f)
        }

        return action2
    }

    /**
     * motan的 ResponseFuture 转 CompletableFuture
     */
    protected fun toCompletableFuture(src: ResponseFuture): CompletableFuture<Any?> {
        val target = CompletableFuture<Any?>()
        src.addListener { f ->
            try{
                target.complete(f.value)
            }catch (e: Exception){
                target.completeExceptionally(e)
            }
        }
        return target
    }
}