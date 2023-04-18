----BEGIN TRANSACTION;
--drop database eventholderdb;
--create database eventholderdb;
----COMMIT;

--DO
--$do$
--DECLARE
--BEGIN
--   IF EXISTS (SELECT FROM pg_database WHERE datname = 'eventholderdb') THEN
--      RAISE NOTICE 'Database already exists';  -- optional
--   ELSE
--      PERFORM dblink_exec('dbname=' || current_database()  -- current db
--                        , 'CREATE DATABASE eventholderdb');
--   END IF;
--
--END
--$do$;

DO $$
BEGIN
PERFORM dblink_exec('', 'CREATE DATABASE eventholderdb');
EXCEPTION WHEN duplicate_database THEN RAISE NOTICE '%, skipping', SQLERRM USING ERRCODE = SQLSTATE;
END
$$;