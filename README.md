```sql
create table if not exists devices (
  id uuid primary key default gen_random_uuid(),
  device_id text,
  platform text not null,
  expo_push_token text not null,
  created_at timestamptz not null default now()
);

create unique index if not exists devices_unique_token
on devices (expo_push_token);

```
