-- This script is responsible for creating the database schema

CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    email VARCHAR(256),
    password VARCHAR(256),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "resource_type" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) UNIQUE,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "resource" (
    id SERIAL PRIMARY KEY,
    typeId INT REFERENCES "resource_type" NOT NULL
);

