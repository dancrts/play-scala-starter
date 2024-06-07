\c chaapy;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--CREATE SCHEMA IF NOT EXISTS chaapy;

CREATE TABLE IF NOT EXISTS accounts (
    account_key uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(100),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS messages
(
    message_key uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    content     TEXT,
    room_key    uuid NOT NULL REFERENCES rooms (room_key) ON DELETE CASCADE ON UPDATE CASCADE,
    user_key    uuid NOT NULL REFERENCES accounts (account_key) ON DELETE CASCADE ON UPDATE CASCADE,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    inserted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS workspaces (
    workspace_key uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(50),
    description TEXT,
    image_key uuid,
    color VARCHAR(50),
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    inserted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


--CREATE INDEX rooms_workspace_key_idx ON rooms (workspace_key);
--CREATE INDEX room_users_room_key_idx ON room_users (room_key);
--CREATE INDEX room_users_user_key_idx ON room_users (user_key);
--CREATE INDEX messages_room_key_idx ON messages (room_key);
--CREATE INDEX messages_message_key_idx ON messages (message_key);
--CREATE INDEX messages_room_user_key_idx ON messages (user_key);
--CREATE INDEX messages_room_key_inserted_at_idx ON messages (room_key, inserted_at);