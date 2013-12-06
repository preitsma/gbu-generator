-- Script temporarily disables actual DDL execution.
-- 
-- Run before the Designer Batch DDL generation so that DDL scripts
-- are only generated and not executed.
-- 
-- Run allow-ddl.sql afterwards
-- 
-- 
SET ECHO ON

drop trigger no_ddl;