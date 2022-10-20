CREATE TABLE IF NOT EXISTS statistic
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app  VARCHAR                     NOT NULL,
    uri  VARCHAR                     NOT NULL,
    ip   VARCHAR                     NOT NULL,
    time TIMESTAMP WITHOUT TIME ZONE NOT NULL
);