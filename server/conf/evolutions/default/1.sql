CREATE TABLE "users"
(
    "id"         SERIAL PRIMARY KEY,
    "created_at" TIMESTAMP NOT NULL,
    "name"       VARCHAR   NOT NULL,
    "email"      VARCHAR   NOT NULL,
    "password"   VARCHAR   NOT NULL,
    "phone"      VARCHAR NULL
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