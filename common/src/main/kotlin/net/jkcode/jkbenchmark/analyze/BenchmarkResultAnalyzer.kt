package net.jkcode.jkbenchmark.analyze

import net.jkcode.jkutil.common.travel
import java.io.File
import java.lang.IllegalArgumentException

/**
 * 测试结果分析
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-20 21:20:33
 */
object BenchmarkResultAnalyzer {

    /**
     * 解析测试结果日志
     * 格式: 2019-11-20 17:00:28 [INFO] motan-file-c10-n40000-syn | Runtime: 1431.09 ms, Avg TPS: 27950.64, Avg RT: 0.36ms
     * @param log
     * @return
     */
    public fun parseResultLog(logDir: String): List<BenchmarkResultModel> {
        val dir = File(logDir)
        if(!dir.exists())
            throw IllegalArgumentException("目录不存在: logDir")

        val results = ArrayList<BenchmarkResultModel>()
        dir.travel {f ->
            if(f.isFile && f.name.startsWith("result.log")){
                println("解析测试结果日志: $f")
                val reg = "[^\\[]+\\[(TRACE|DEBUG|INFO|WARN|ERROR)\\] (\\w+)-(\\w+)-c(\\d+)-n(\\d+)-(a?)syn \\| Runtime: ([\\d\\.]+) ms, Avg TPS: ([\\d\\.]+), Avg RT: ([\\d\\.]+) ms, Error: (\\d+)%".toRegex()
                f.forEachLine { line ->
                    val m = reg.find(line)!!
                    val result = BenchmarkResultModel()
                    result.player = m.groups[2]!!.value
                    result.action = m.groups[3]!!.value
                    result.concurrents = m.groups[4]!!.value.toInt()
                    result.requests = m.groups[5]!!.value.toInt()
                    result.async = if("a" == m.groups[6]!!.value) 1 else 0
                    result.runTime = m.groups[7]!!.value.toDouble()
                    result.tps = m.groups[8]!!.value.toDouble()
                    result.rt = m.groups[9]!!.value.toDouble()
                    result.errPct = m.groups[10]!!.value.toInt()
                    result.create()
                    results.add(result)
                }
            }
        }

        return results
    }

}