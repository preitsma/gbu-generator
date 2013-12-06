-- Script temporarily disables actual DDL execution.
-- 
-- Run before the Designer Batch DDL generation so that DDL scripts
-- are only generated and not executed.
-- 
-- Run allow-ddl.sql afterwards
-- 
-- 
SET ECHO ON

create or replace trigger no_ddl
   before ddl on schema
begin
   raise_application_error( -20001, 'DDL execution temporarily not allowed, run ALTER TRIGGER no_ddl DISABLE;');
end no_ddl;
/