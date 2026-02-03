CREATE TABLE IF NOT EXISTS group_member (
                                            id uuid PRIMARY KEY,
                                            group_id uuid NOT NULL,
                                            user_id uuid NOT NULL,
                                            role varchar(20) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_group_member_group
    FOREIGN KEY (group_id) REFERENCES app_group(id),

    CONSTRAINT fk_group_member_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
    );
