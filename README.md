# 🏠 Project OHANA — Private Family ERP

> **Expo · Supabase · Google OAuth · Push Notification 기반**  
> 상용 육아 앱의 데이터 락인을 피하기 위해 직접 설계한  
> **2026년형 프라이빗 가족 ERP**

---

## ✨ Overview

Project OHANA는 가족의 일상 데이터와 태스크(심부름, 기록)를  
**외부 플랫폼에 종속되지 않고** 직접 소유하기 위한 모바일 앱 프로젝트다.

### 핵심 철학
- 📊 **Data Ownership**: 최종 데이터는 Google Sheets (내 계정)
- 🔔 **Instant Action**: Push Notification 기반 태스크 전달
- ⚙️ **Zero Server Ops**: Supabase + Edge Function (Serverless)
- 📱 **One-Hand UX**: 한 손, 한 번의 터치

---

## 🧱 Tech Stack

### App
- **Expo SDK 54**
- **React Native (New Architecture)**
- **Expo Router**

### Backend / Infra
- **Supabase**
  - Auth (Google OAuth)
  - Realtime Sync
  - Edge Functions
- **Expo Push Notification**
- **Google OAuth**
- **(Next) Google Sheets API**

---

## 📂 Project Structure




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


