CREATE TABLE IF NOT EXISTS vaccination (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,
  subject_id uuid NOT NULL,

  vaccine_type varchar(20) NOT NULL,    -- 백신 코드 (ENUM)
  dose_number int NOT NULL DEFAULT 1,   -- 1차, 2차, 3차...

  scheduled_date date NOT NULL,         -- 접종 예정일 (생일 기준 자동 계산)
  inoculated_date date NULL,            -- 실제 접종일 (이게 채워지면 '완료')

  hospital_name varchar(50) NULL,
  memo text NULL,

  google_event_id varchar(255) NULL,    -- 구글 캘린더 이벤트 ID

  created_by_user_id uuid NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT fk_vaccination_group FOREIGN KEY (group_id) REFERENCES app_group(id),
  CONSTRAINT fk_vaccination_subject FOREIGN KEY (subject_id) REFERENCES subject(id),

  -- 중복 방지 (같은 아이에게 같은 주사 N차는 1번만)
  CONSTRAINT ux_vaccination_unique UNIQUE (subject_id, vaccine_type, dose_number)
);

-- "이번 달 맞을 주사" 빨리 찾기용 인덱스
CREATE INDEX IF NOT EXISTS idx_vaccination_schedule ON vaccination(subject_id, scheduled_date);
