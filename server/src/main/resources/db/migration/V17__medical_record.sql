CREATE TABLE IF NOT EXISTS medical_record (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,
  subject_id uuid NOT NULL,

  record_type varchar(20) NOT NULL, -- TEMPERATURE(체온), MEDICATION(투약)
  record_time timestamptz NOT NULL, -- 측정/투약 시간

  temperature numeric(3,1) NULL,    -- 체온 (예: 37.5)
  medication_name varchar(50) NULL, -- 약 이름 (예: 챔프 빨강)
  amount_ml numeric(4,1) NULL,      -- 투약량 (예: 2.5ml)
  memo text NULL,

  created_by_user_id uuid NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_medical_group FOREIGN KEY (group_id) REFERENCES app_group(id)
);
