CREATE TABLE IF NOT EXISTS export_job (
                                          id uuid PRIMARY KEY,
                                          group_id uuid NOT NULL,

                                          job_type varchar(30) NOT NULL,         -- FEEDING_SHEETS
    job_date date NOT NULL,                -- 대상 날짜 (ex: 어제)
    status varchar(20) NOT NULL,           -- PENDING/RUNNING/SUCCESS/FAILED
    try_count int NOT NULL DEFAULT 0,
    last_error text NULL,

    started_at timestamptz NULL,
    finished_at timestamptz NULL,

    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_export_job_group
    FOREIGN KEY (group_id) REFERENCES app_group(id)
    );

-- 중복 실행 방지 (한 그룹, 같은 날짜, 같은 타입은 딱 1개만!)
CREATE UNIQUE INDEX IF NOT EXISTS ux_export_job_group_type_date
    ON export_job(group_id, job_type, job_date);

CREATE INDEX IF NOT EXISTS idx_export_job_status
    ON export_job(status);