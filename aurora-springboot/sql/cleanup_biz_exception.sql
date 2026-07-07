-- Aurora Part 2.1 清理脚本
-- 用途：清理 t_resource 表中已废弃的 /bizException 资源记录
-- 背景：BizExceptionController（反常设计 + 死代码）已在 Part 2.1 删除，
--       同步清理数据库中对应资源记录，避免动态权限管理出现死规则。
-- 执行方式：docker exec -i aurora-mysql mysql -uaurora -paurora123 -D aurora < sql/cleanup_biz_exception.sql
-- 幂等性：可重复执行，无匹配行时 DELETE 不报错。

DELETE FROM `t_resource` WHERE `url` = '/bizException';
