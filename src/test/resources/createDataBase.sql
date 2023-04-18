--SET AUTOCOMMIT = ON;
--DROP DATABASE IF EXISTS mydb;
--CREATE DATABASE mydb;

printf '\set AUTOCOMMIT on drop database mydb; create database mydb; ' |  psql postgres
