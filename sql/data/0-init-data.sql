-- This script is responsible for initializes the tables of the database.
-- Should be run after schema.sql

INSERT INTO "user" ("email", "password") VALUES ('root', 'root');

INSERT INTO "resource_type" ("name") VALUES ('texture'), ('music'), ('sound-effect'), ('model');

