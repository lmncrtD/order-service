--liquibase formatted sql

--changeset Lmncrt:001-create-shedlock-table
create TABLE public.shedlock
(
    name VARCHAR(64) PRIMARY KEY NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL
);