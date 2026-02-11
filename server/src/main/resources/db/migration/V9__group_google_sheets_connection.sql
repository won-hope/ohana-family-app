CREATE TABLE IF NOT EXISTS group_google_sheets_connection (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL UNIQUE,

  google_sub varchar(64) NOT NULL,
  spreadsheet_id varchar(128) NOT NULL,
  sheet_name varchar(64) NOT NULL DEFAULT 'feeding_logs',

  refresh_token_encrypted text NOT NULL, -- 암호화된 토큰
  scopes text NOT NULL,

  connected_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT fk_google_sheets_group FOREIGN KEY (group_id) REFERENCES app_group(id)
);
