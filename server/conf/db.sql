/*
sudo su postgres
psql
*/
CREATE ROLE e_user WITH CREATEDB CREATEROLE LOGIN ENCRYPTED PASSWORD '123';
CREATE DATABASE education WITH OWNER e_user;
/*
exit
exit

edit conf file
sudo gedit /etc/postgresql/12/main/pg_hba.conf
restart postgresql
sudo systemctl restart postgresql

sudo su postgres
psql -U e_user -d education

*/
CREATE TABLE "users"
(
    "id"                  SERIAL PRIMARY KEY,
    "created_at"          TIMESTAMP NOT NULL,
    "name"                VARCHAR   NOT NULL,
    "email"               VARCHAR   NOT NULL,
    "password"            VARCHAR   NOT NULL,
    "phone"               VARCHAR   NULL
);
CREATE TABLE "courses"
(
    "id"          SERIAL PRIMARY KEY,
    "created_at"  TIMESTAMP NOT NULL,
    "title"       VARCHAR   NOT NULL,
    "category"    VARCHAR   NOT NULL,
    "video"       VARCHAR   NOT NULL,
    "description" VARCHAR   NOT NULL
);