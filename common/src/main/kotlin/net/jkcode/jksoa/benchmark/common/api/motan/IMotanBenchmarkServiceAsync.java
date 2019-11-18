package net.jkcode.jksoa.benchmark.common.api.motan;

import com.weibo.api.motan.rpc.ResponseFuture;
import java.lang.Object;

/**
 * motan专用接口
 */
public interface IMotanBenchmarkServiceAsync extends IMotanBenchmarkService {

  ResponseFuture doNothingAsync(int id);

  ResponseFuture echoAsync(Object request);

  ResponseFuture getMessageFromCacheAsync(int id);

  ResponseFuture getMessageFromFileAsync(int id);

  ResponseFuture getMessageFromDbAsync(int id);
}
