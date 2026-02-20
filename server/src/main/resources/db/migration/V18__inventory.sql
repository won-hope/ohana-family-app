CREATE TABLE IF NOT EXISTS inventory_item (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,

  item_type varchar(20) NOT NULL,  -- DIAPER(기저귀), FORMULA(분유), WIPES(물티슈)
  name varchar(100) NOT NULL,      -- 품명 (예: "하기스 네이처메이드 3단계")

  remaining_count int NOT NULL DEFAULT 0,  -- 남은 수량
  alert_threshold int NOT NULL DEFAULT 20, -- 이 수량 이하가 되면 "부족" 알림

  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_inventory_group FOREIGN KEY (group_id) REFERENCES app_group(id),
  CONSTRAINT ux_inventory_group_type UNIQUE (group_id, item_type) -- 타입별 하나씩 관리
);
