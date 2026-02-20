ALTER TABLE inventory_item ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now();
