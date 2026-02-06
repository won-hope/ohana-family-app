CREATE TABLE IF NOT EXISTS feeding_log (
                                           id uuid PRIMARY KEY,
                                           group_id uuid NOT NULL,
                                           subject_id uuid NOT NULL,
                                           fed_at timestamptz NOT NULL,
                                           amount_ml int NULL,
                                           method varchar(20) NOT NULL,
    note text NULL,
    created_by uuid NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_feeding_group FOREIGN KEY (group_id) REFERENCES app_group(id),
    CONSTRAINT fk_feeding_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_feeding_created_by FOREIGN KEY (created_by) REFERENCES app_user(id)
    );

CREATE INDEX IF NOT EXISTS idx_feeding_group_fedat ON feeding_log(group_id, fed_at DESC);
CREATE INDEX IF NOT EXISTS idx_feeding_subject_fedat ON feeding_log(subject_id, fed_at DESC);