# 🏠 Project OHANA — Private Family ERP

> **Expo · Supabase · Google OAuth · Push Notification 기반**  
> 상용 육아 앱의 데이터 락인을 피하기 위해 직접 설계한  
> **2026년형 프라이빗 가족 ERP**

---

## ✨ Overview

Project OHANA는 가족의 일상 데이터와 태스크(심부름, 기록)를  
**외부 플랫폼에 종속되지 않고 직접 소유**하기 위한 모바일 앱 프로젝트다.

기존 육아 앱과 가계부 앱은 편리하지만 데이터는 결국 서비스 사업자의 서버에 갇힌다.  
이 프로젝트는 그 구조 자체를 거부한다.

### Core Principles

- 📊 **Data Ownership**: 최종 데이터는 **내 계정의 Google Sheets**에 저장
- 🔔 **Instant Action**: 심부름/요청은 즉시 **Push Notification**으로 전달
- ⚙️ **Zero Server Ops**: Supabase + Edge Functions 기반 서버리스
- 📱 **One-Hand UX**: 육아 상황을 고려한 한 손·원탭 UX

---

## 🧱 Tech Stack

### Mobile App
- Expo SDK 54
- React Native (New Architecture)
- Expo Router
- Expo Dev Client (EAS Development Build)

### Backend / Infra
- Supabase
  - Authentication (Google OAuth)
  - Realtime Sync
  - Edge Functions
- Expo Push Notification
- Google OAuth
- Google Sheets API (Planned)

---

## 📂 Project Structure

```
app/                    # Screens & Routing (Expo Router)
 ├─ (auth)/             # Authentication screens
 │   └─ sign-in.tsx
 ├─ (tabs)/             # Main app after login
 │   └─ index.tsx
 ├─ index.tsx           # Auth Gate (login routing)
 └─ _layout.tsx

src/
 └─ lib/                # External services / infra
    ├─ supabase.ts
    ├─ auth.ts
    ├─ notifications.ts
    └─ device.ts

assets/                 # icons, splash, images
components/
hooks/
```

**Rules**
- UI & Routing → `app/`
- External services & infra → `src/lib`
- 화면과 인프라 로직을 분리

---

## 🔐 Environment & Security Policy

### Allowed in App (.env)
```
EXPO_PUBLIC_SUPABASE_URL=
EXPO_PUBLIC_SUPABASE_ANON_KEY=
```

### NEVER Stored in App / Git
- Supabase service_role key
- Google OAuth Client Secret
- Google Service Account JSON
- Firebase / Native credential files
- `.env` 실파일

> 모든 Secret은 Supabase Dashboard 또는 Edge Function 환경변수로만 관리

---

## 🧪 Local Development Setup

### 1) Clone
```
git clone <repository-url>
cd ohana-family-app
```

### 2) Environment
```
copy .env.example .env   # Windows
```

### 3) Install
```
npm install
```

### 4) Run (Development Build)
```
npx expo start --dev-client
```

> ⚠️ Expo SDK 53+부터 Android Push Notification은 Expo Go에서 지원되지 않음  
> 반드시 **EAS Development Build** 사용

---

## 📦 EAS (Expo Application Services)

### Install & Login
```
npm install -g eas-cli
eas login
```

### Initialize
```
eas init
```

`app.json`에 자동 추가:
```
{
  "extra": {
    "eas": {
      "projectId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxx"
    }
  }
}
```

---

## 🔔 Push Notification

- Android Development Build 기준 Push Token 발급 완료
```
ExponentPushToken[xxxxxxxxxxxxxxxx]
```
- Expo Go ❌ / Dev Build ⭕

---

## 🔐 Authentication — Google OAuth

### Flow
```
App → Supabase → Google OAuth → Supabase → App
```

### Google Cloud Console
- OAuth Client Type: Web Application
- Redirect URI:
```
https://<SUPABASE_PROJECT_ID>.supabase.co/auth/v1/callback
```

### Supabase Settings
Authentication → Providers → Google  
(Client ID / Client Secret 등록)

### Supabase Redirect URLs
```
ohanafamilyapp://
ohanafamilyapp://--/
```

---

## 🔑 Auth Implementation

```
await supabase.auth.signInWithOAuth({
  provider: "google",
  options: { redirectTo: Linking.createURL("/") }
});
```

Auth Gate:
- Session 있음 → `(tabs)`
- Session 없음 → `(auth)/sign-in`

---

## 🧹 Git Hygiene

- `.env` 및 모든 secret 파일 gitignore
- Native key / keystore ignore
- EAS / build artifacts ignore

`.env.example`만 커밋:
```
EXPO_PUBLIC_SUPABASE_URL=
EXPO_PUBLIC_SUPABASE_ANON_KEY=
```

---

## 🧠 Project Status

### Completed
- Expo + Router 환경 세팅
- Supabase 연동
- Google OAuth 로그인
- EAS projectId 설정
- Android Push Token 발급

### Next
- Supabase Edge Function → Push 발송
- Errand(Task) 플로우
- Google Sheets API 연동
- User ↔ Device ↔ Push Token 매핑

---

## 📌 Philosophy

> **Apps may disappear. Data should not.**

---

## 📄 License
Private / Personal Use
