CREATE TABLE IF NOT EXISTS ledger_transaction (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,

  transaction_type varchar(20) NOT NULL, -- INCOME(수입), EXPENSE(지출), TRANSFER(이체)
  amount bigint NOT NULL,                -- 금액 (원)
  transaction_date date NOT NULL,        -- 거래 날짜

  category varchar(50) NOT NULL,         -- 카테고리 (예: 식비, 주거비, 육아용품, 월급)
  payment_method varchar(50) NULL,       -- 결제 수단 (예: 신용카드, 현금, 토스)
  memo text NULL,                        -- 상세 내역 (예: 이마트 기저귀 구매)

  created_by_user_id uuid NOT NULL,      -- 누가 썼는지 (형 or 와이프)
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT fk_ledger_group FOREIGN KEY (group_id) REFERENCES app_group(id)
);

-- 월별 통계 뽑을 때 엄청 빨라지도록 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_ledger_group_date ON ledger_transaction(group_id, transaction_date DESC);
