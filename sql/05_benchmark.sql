
-- ============================================================
-- PassJava Task 20
-- Step 7：独立性能测试数据库初始化
--
-- MySQL 8.0
--
-- 功能：
-- 1. 创建 passjava_benchmark 数据库
-- 2. 复制三张业务表的结构和索引
-- 3. 检查索引
-- 4. 生成 100000 条 Outbox 模拟数据
-- 5. 验证数据分布
-- 6. 更新 MySQL 统计信息
--
-- 注意：
-- 只删除 passjava_benchmark 中的测试表
-- 不修改 passjava_study 中的任何业务数据
-- ============================================================


-- ============================================================
-- Step 1：创建独立测试数据库
-- ============================================================

CREATE DATABASE IF NOT EXISTS passjava_benchmark
    DEFAULT CHARACTER SET utf8mb4;


-- ============================================================
-- Step 2：删除旧测试表
-- 如果重新执行脚本，会重新初始化测试环境
-- ============================================================

DROP TABLE IF EXISTS passjava_benchmark.sms_event_outbox;
DROP TABLE IF EXISTS passjava_benchmark.sms_view_log;
DROP TABLE IF EXISTS passjava_benchmark.sms_daily_stat;


-- ============================================================
-- Step 3：复制原业务表结构和索引
-- CREATE TABLE LIKE 不复制业务数据
-- ============================================================

CREATE TABLE passjava_benchmark.sms_event_outbox
    LIKE passjava_study.sms_event_outbox;

CREATE TABLE passjava_benchmark.sms_view_log
    LIKE passjava_study.sms_view_log;

CREATE TABLE passjava_benchmark.sms_daily_stat
    LIKE passjava_study.sms_daily_stat;


-- ============================================================
-- Step 4：切换测试数据库
-- ============================================================

USE passjava_benchmark;


-- ============================================================
-- Step 5：检查表结构
-- ============================================================

SHOW TABLES;


-- ============================================================
-- Step 6：检查原始索引
-- 后续需要根据这些索引进行性能优化
-- ============================================================

SHOW INDEX FROM sms_event_outbox;

SHOW INDEX FROM sms_view_log;

SHOW INDEX FROM sms_daily_stat;


-- ============================================================
-- Step 7：生成 100000 条 Outbox 测试数据
--
-- 使用五个独立的数字派生表：
-- a：个位
-- b：十位
-- c：百位
-- d：千位
-- e：万位
--
-- 通过 CROSS JOIN 生成 0 ~ 99999
--
-- 不使用 TEMPORARY TABLE
-- 避免 MySQL 1137 Can't reopen table 错误
-- ============================================================

INSERT INTO sms_event_outbox (
    event_id,
    user_id,
    question_id,
    occurred_at,
    status,
    retry_count,
    next_retry_at,
    create_time,
    update_time
)

SELECT
    -- 唯一事件 ID
    CONCAT('bench-', LPAD(n, 10, '0')),

    -- 1000 个用户
    10000 + MOD(n, 1000),

    -- 3000 道题目
    1 + MOD(n, 3000),

    -- 最近 30 天内的事件时间
    DATE_SUB(
            NOW(),
            INTERVAL MOD(n, 30) DAY
    ),

    -- 10% 为待发布状态
    -- 90% 为其他状态
    CASE
        WHEN MOD(n, 10) = 0 THEN 0
        ELSE 2
        END,

    -- 重试次数 0 ~ 2
    MOD(n, 3),

    -- 部分事件已经到期
    -- 部分事件尚未到期
    CASE
        WHEN MOD(n, 4) = 0 THEN
            DATE_SUB(
                    NOW(),
                    INTERVAL MOD(n, 24) HOUR
            )
        ELSE
            DATE_ADD(
                    NOW(),
                    INTERVAL MOD(n, 24) HOUR
            )
        END,

    -- 最近 10000 秒内的创建时间
    DATE_SUB(
            NOW(),
            INTERVAL MOD(n, 10000) SECOND
    ),

    NOW()

FROM (

         -- ========================================================
         -- 生成 0 ~ 99999
         -- 五组独立数字表，每组 0 ~ 9
         -- ========================================================

         SELECT
             a.d
                 + b.d * 10
                 + c.d * 100
                 + d.d * 1000
                 + e.d * 10000 AS n

         FROM (

                  SELECT 0 AS d UNION ALL
                  SELECT 1 UNION ALL
                  SELECT 2 UNION ALL
                  SELECT 3 UNION ALL
                  SELECT 4 UNION ALL
                  SELECT 5 UNION ALL
                  SELECT 6 UNION ALL
                  SELECT 7 UNION ALL
                  SELECT 8 UNION ALL
                  SELECT 9

              ) AS a

                  CROSS JOIN (

             SELECT 0 AS d UNION ALL
             SELECT 1 UNION ALL
             SELECT 2 UNION ALL
             SELECT 3 UNION ALL
             SELECT 4 UNION ALL
             SELECT 5 UNION ALL
             SELECT 6 UNION ALL
             SELECT 7 UNION ALL
             SELECT 8 UNION ALL
             SELECT 9

         ) AS b

                  CROSS JOIN (

             SELECT 0 AS d UNION ALL
             SELECT 1 UNION ALL
             SELECT 2 UNION ALL
             SELECT 3 UNION ALL
             SELECT 4 UNION ALL
             SELECT 5 UNION ALL
             SELECT 6 UNION ALL
             SELECT 7 UNION ALL
             SELECT 8 UNION ALL
             SELECT 9

         ) AS c

                  CROSS JOIN (

             SELECT 0 AS d UNION ALL
             SELECT 1 UNION ALL
             SELECT 2 UNION ALL
             SELECT 3 UNION ALL
             SELECT 4 UNION ALL
             SELECT 5 UNION ALL
             SELECT 6 UNION ALL
             SELECT 7 UNION ALL
             SELECT 8 UNION ALL
             SELECT 9

         ) AS d

                  CROSS JOIN (

             SELECT 0 AS d UNION ALL
             SELECT 1 UNION ALL
             SELECT 2 UNION ALL
             SELECT 3 UNION ALL
             SELECT 4 UNION ALL
             SELECT 5 UNION ALL
             SELECT 6 UNION ALL
             SELECT 7 UNION ALL
             SELECT 8 UNION ALL
             SELECT 9

         ) AS e

     ) AS numbers;


-- ============================================================
-- Step 8：验证 Outbox 总数据量
-- 预期：100000
-- ============================================================

SELECT
    COUNT(*) AS total_rows
FROM sms_event_outbox;


-- ============================================================
-- Step 9：验证状态分布
-- 预期：
-- status = 0：10000
-- status = 2：90000
-- ============================================================

SELECT
    status,
    COUNT(*) AS row_count
FROM sms_event_outbox
GROUP BY status
ORDER BY status;


-- ============================================================
-- Step 10：统计已经到期的待发布事件
-- 预期：5000
-- ============================================================

SELECT
    COUNT(*) AS ready_count
FROM sms_event_outbox
WHERE status = 0
  AND next_retry_at <= NOW();


-- ============================================================
-- Step 11：验证事件 ID 是否重复
-- 预期：0
-- ============================================================

SELECT
    COUNT(*) - COUNT(DISTINCT event_id) AS duplicate_count
FROM sms_event_outbox;


-- ============================================================
-- Step 12：检查模拟数据时间范围
-- ============================================================

SELECT
    MIN(occurred_at) AS earliest_event,
    MAX(occurred_at) AS latest_event,
    MIN(create_time) AS earliest_create,
    MAX(create_time) AS latest_create
FROM sms_event_outbox;


-- ============================================================
-- Step 13：更新 MySQL 统计信息
-- ============================================================

ANALYZE TABLE sms_event_outbox;

ANALYZE TABLE sms_view_log;

ANALYZE TABLE sms_daily_stat;


-- ============================================================
-- Step 14：查看测试数据库表空间占用
-- ============================================================

SELECT
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'passjava_benchmark';


-- ============================================================
-- Step 15：最终检查索引
-- ============================================================

SHOW INDEX FROM sms_event_outbox;

SHOW INDEX FROM sms_view_log;

SHOW INDEX FROM sms_daily_stat;


-- ============================================================
-- Step 7 初始化完成
-- ============================================================
