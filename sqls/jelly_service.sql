/*
 Source Host           : 127.0.0.1:3306
 Source Schema         : jelly_service
*/

CREATE TABLE `lock` (
  `Id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `SessionId` varchar(32) DEFAULT NULL COMMENT '会话ID',
  `UpdateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT ' 更新时间',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `u_sessionId` (`SessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `SessionId` varchar(32) NOT NULL DEFAULT '' COMMENT '会话ID',
  `Board` varchar(127) DEFAULT NULL COMMENT '棋盘布局',
  PRIMARY KEY (`SessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='玩家对应的棋盘布局';

-- ----------------------------
-- 通过存储过程, 清理过期的lock
-- ----------------------------
DROP PROCEDURE IF EXISTS `clear_expired_lock`;
delimiter ;;
CREATE PROCEDURE `jelly_service`.`clear_expired_lock`()
begin
	delete from `lock` where UpdateTime < timestampadd(second, -30, current_timestamp());
end
;;
delimiter ;

-- ----------------------------
-- 创建event, 定时调用上述存储过程
-- ----------------------------
DROP EVENT IF EXISTS `e_clear_expired_lock`;
delimiter ;;
CREATE EVENT `jelly_service`.`e_clear_expired_lock`
ON SCHEDULE
EVERY '2' MINUTE STARTS '2019-05-19 10:00:00'
ON COMPLETION PRESERVE
DO BEGIN 
	call clear_expired_lock();
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
