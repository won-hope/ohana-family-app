CREATE TABLE IF NOT EXISTS subject (
                                       id uuid PRIMARY KEY,
                                       group_id uuid NOT NULL,
                                       type varchar(20) NOT NULL,
    name varchar(50) NOT NULL,
    birth_date date NULL,
    notes text NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_subject_group
    FOREIGN KEY (group_id) REFERENCES app_group(id)
    );

CREATE INDEX IF NOT EXISTS idx_subject_group_id ON subject(group_id);