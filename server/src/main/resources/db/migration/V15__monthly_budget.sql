CREATE TABLE IF NOT EXISTS monthly_budget (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,

  year_month varchar(7) NOT NULL, -- 예: '2026-02'
  target_amount bigint NOT NULL,  -- 한 달 목표 생활비 (예: 1,500,000)

  created_by_user_id uuid NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT fk_budget_group FOREIGN KEY (group_id) REFERENCES app_group(id),
  CONSTRAINT ux_budget_group_month UNIQUE (group_id, year_month) -- 한 달에 예산은 1개만
);
