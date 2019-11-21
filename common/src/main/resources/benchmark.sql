-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `from_uid` int(11) unsigned NOT NULL COMMENT '发送人id',
  `to_uid` int(11) unsigned NOT NULL COMMENT '接收人id',
  `content` varchar(50) NOT NULL DEFAULT '' COMMENT '消息内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息';

-- 性能测试结果
CREATE TABLE IF NOT EXISTS `benchmark_result` (
	`id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '结果id',
	`tech` varchar(50) NOT NULL DEFAULT '' COMMENT '技术: jksoa/dubbo/motan',
	`action` varchar(50) NOT NULL DEFAULT '' COMMENT '动作: nth/cache/file/db',
	`concurrents` int(11) unsigned NOT NULL COMMENT '并发数',
	`requests` int(11) unsigned NOT NULL COMMENT '请求数',
	`async` tinyint(3) unsigned NOT NULL COMMENT '是否异步',
	`run_time` double(64,2) unsigned NOT NULL COMMENT '运行时间',
	`tps` double(64,2) unsigned NOT NULL COMMENT '吞吐量',
	`rt` double(64,2) unsigned NOT NULL COMMENT '响应时间',
	`err_pct` int(11) unsigned NOT NULL COMMENT '错误百分比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='性能测试结果';