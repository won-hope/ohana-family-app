CREATE TABLE IF NOT EXISTS care_event (
                                          id uuid PRIMARY KEY,
                                          group_id uuid NOT NULL,
                                          subject_id uuid NOT NULL,

                                          type varchar(30) NOT NULL,
    occurred_at timestamptz NOT NULL,

    payload jsonb NULL,

    created_by_user_id uuid NOT NULL,
    idempotency_key uuid NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_care_event_group FOREIGN KEY (group_id) REFERENCES app_group(id),
    CONSTRAINT fk_care_event_subject FOREIGN KEY (subject_id) REFERENCES subject(id)
    );

-- 멱등성 보장 (중복 저장 방지)
CREATE UNIQUE INDEX IF NOT EXISTS ux_care_event_subject_idempotency
    ON care_event(subject_id, idempotency_key);

-- 타임라인 조회 속도 최적화
CREATE INDEX IF NOT EXISTS idx_care_event_subject_occurred
    ON care_event(subject_id, occurred_at);

-- "마지막 목욕 언제 했지?" 조회용 인덱스
CREATE INDEX IF NOT EXISTS idx_care_event_subject_type_occurred
    ON care_event(subject_id, type, occurred_at DESC);