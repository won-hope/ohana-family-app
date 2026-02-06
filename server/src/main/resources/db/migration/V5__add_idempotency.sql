-- 1) 멱등성 키 컬럼 추가 (기존 데이터 고려해서 처음엔 NULL 허용, 나중에 NOT NULL 추천)
ALTER TABLE feeding_log
    ADD COLUMN IF NOT EXISTS idempotency_key uuid;

-- 2) 중복 방지 유니크 인덱스 (핵심)
-- 한 아이(subject)에게 같은 요청키(idempotency)가 두 번 들어올 수 없음
CREATE UNIQUE INDEX IF NOT EXISTS ux_feeding_subject_idempotency
    ON feeding_log(subject_id, idempotency_key);

-- (참고: created_by는 V4에서 이미 만들었으므로 생략!)
