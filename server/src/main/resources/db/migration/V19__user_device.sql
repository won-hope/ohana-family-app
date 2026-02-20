CREATE TABLE IF NOT EXISTS user_device (
  user_id uuid PRIMARY KEY,
  fcm_token varchar(255) NOT NULL, -- 기기별 푸시 토큰
  updated_at timestamptz NOT NULL DEFAULT now()
);
