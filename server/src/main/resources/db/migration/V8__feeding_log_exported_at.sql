ALTER TABLE feeding_log
  ADD COLUMN IF NOT EXISTS exported_at timestamptz NULL;

CREATE INDEX IF NOT EXISTS idx_feeding_exported_at
  ON feeding_log(exported_at);
