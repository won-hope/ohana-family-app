CREATE TABLE IF NOT EXISTS app_user (
                                        id uuid PRIMARY KEY,
                                        google_sub varchar(64) NOT NULL UNIQUE,
    email varchar(255) NOT NULL,
    name varchar(100),
    picture_url text,
    created_at timestamptz NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS app_group (
                                         id uuid PRIMARY KEY,
                                         owner_user_id uuid NOT NULL REFERENCES app_user(id),
    name varchar(100) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
    );
