CREATE TABLE IF NOT EXISTS growth_record (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,
  subject_id uuid NOT NULL,

  measured_date date NOT NULL,          -- 측정 날짜

  height numeric(5,1) NULL,             -- 키 (cm)
  weight numeric(5,2) NULL,             -- 몸무게 (kg)
  head_circumference numeric(5,1) NULL, -- 머리둘레 (cm)

  summary_desc varchar(255) NULL,       -- "상위 15% (우량)" 같은 분석 문구 저장

  created_by_user_id uuid NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT fk_growth_group FOREIGN KEY (group_id) REFERENCES app_group(id),
  CONSTRAINT fk_growth_subject FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE INDEX IF NOT EXISTS idx_growth_subject_date ON growth_record(subject_id, measured_date DESC);
