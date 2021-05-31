CREATE TABLE "users"
(
    "id"                  SERIAL PRIMARY KEY,
    "created_at"          TIMESTAMP NOT NULL,
    "name"                VARCHAR   NOT NULL,
    "email"               VARCHAR   NOT NULL,
    "password"            VARCHAR   NOT NULL,
    "phone"               VARCHAR   NULL
);