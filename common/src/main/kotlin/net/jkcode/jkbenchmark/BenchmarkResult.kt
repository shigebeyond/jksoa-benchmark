package net.jkcode.jkbenchmark

import net.jkcode.jksoa.guard.measure.IMetricBucket

/**
 * 性能测试结果
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 10:33 AM
 */
class BenchmarkResult(
        public val bucket: IMetricBucket, // 统计数据
        public val runTime: Long // 运行时间
): Comparable<BenchmarkResult>{

    override fun toString(): String {
        return bucket.toDesc(runTime)
    }

    public fun toSummary(): String {
        return bucket.toSummary(runTime)
    }

    override fun compareTo(other: BenchmarkResult): Int {
        return (this.runTime - other.runTime).toInt()
    }
}

