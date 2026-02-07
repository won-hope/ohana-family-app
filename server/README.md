# Ohana Server

Backend for Ohana family app. Provides auth, groups, subjects, feeding logs, care events, and dashboard timeline/summary.

## Flow
1) **Auth**
   - Dev: `POST /auth/dev` (dev profile only) -> `accessToken`
   - Google: `POST /auth/google` with `idToken` -> `accessToken`
2) **Group**
   - `POST /groups` to create the first group
   - `GET /groups` to list groups
3) **Subject**
   - `POST /subjects` to create a baby/pet
   - `GET /subjects` / `GET /subjects/{id}` to list/read
4) **Feeding / Care**
   - `POST /feedings` to log feeding
   - `POST /care-events` to log diaper/bath/temp/sleep, etc.
5) **Dashboard**
   - `GET /subjects/{subjectId}/timeline?date=YYYY-MM-DD`
   - `GET /subjects/{subjectId}/summary?date=YYYY-MM-DD`

## Current Development Status
- **Auth**: Google login + dev login (dev profile only), JWT resource server.
- **Group/Subject**: CRUD-style creation + listing, group membership enforcement.
- **Feeding**: idempotent create, subject ownership checks, timeline query.
- **Care Event**: idempotent create, subject ownership checks, timeline query, latest-by-type.
- **Dashboard**: unified timeline (feeding + care) and daily summary.
- **Validation**: request validation + payload validation for care events.
- **Errors**: unified error response format with HTTP status mapping.
- **Flyway**: migrations V1~V6, care_event table included.
- **API tests**: `requests/auth.http` includes auth/group/subject/feeding/care/dashboard flows.

## Run Locally
1) Start Postgres
   - `docker compose up -d`
2) Run server
   - `./gradlew bootRun`

## Configuration
Environment variables (defaults in `application.yml`):
- `DB_URL` (default `jdbc:postgresql://localhost:5432/ohana`)
- `DB_USER` (default `ohana`)
- `DB_PASSWORD` (default `ohana`)
- `OHANA_GOOGLE_CLIENT_ID`
- `OHANA_JWT_SECRET` (32+ bytes recommended)
 - `OHANA_DEV_AUTH_ENABLED` (set `true` only in dev to enable `/auth/dev` and dev seed data)
 - `OHANA_ALLOW_DEV_PROFILE` (set `true` to allow running with `dev` profile)
 - `OHANA_RATE_LIMIT_ENABLED` (default `true`)
 - `OHANA_RATE_LIMIT_AUTH_PER_MINUTE` (default `60`)

## Useful Files
- `requests/auth.http` - API test requests and flow
- `src/main/resources/db/migration` - Flyway migrations

## Endpoints
| Area | Method | Path | Notes |
| --- | --- | --- | --- |
| Auth | POST | `/auth/dev` | Dev-only token |
| Auth | POST | `/auth/google` | Google ID token -> access token |
| Group | POST | `/groups` | Create group |
| Group | GET | `/groups` | List groups |
| Subject | POST | `/subjects` | Create subject |
| Subject | GET | `/subjects` | List subjects |
| Subject | GET | `/subjects/{id}` | Get subject |
| Feeding | POST | `/feedings` | Create feeding |
| Feeding | GET | `/feedings/subject/{subjectId}` | List feedings |
| Care | POST | `/care-events` | Create care event |
| Care | GET | `/care-events` | List care events by date |
| Care | GET | `/care-events/latest` | Latest care event by type |
| Dashboard | GET | `/subjects/{subjectId}/timeline` | Unified timeline |
| Dashboard | GET | `/subjects/{subjectId}/summary` | Daily summary |

## Database Overview (ERD-lite)
```
app_group (id, owner_user_id, name, created_at)
group_member (id, group_id -> app_group.id, user_id, role, created_at)
subject (id, group_id -> app_group.id, type, name, birth_date, notes, created_at)
feeding_log (id, group_id -> app_group.id, subject_id -> subject.id, fed_at, amount_ml, method, note, created_by, idempotency_key, created_at)
care_event (id, group_id -> app_group.id, subject_id -> subject.id, type, occurred_at, payload, created_by_user_id, idempotency_key, created_at)
user (id, google_sub, email, name, picture_url, created_at)
```

## Auth/Authorization Flow (Sequence)
```
Client -> POST /auth/dev or /auth/google
Server -> accessToken (JWT)

Client -> API Request with Authorization: Bearer <token>
Server -> JWT validation -> userId
Server -> group membership check -> subject ownership check
Server -> data access (feeding/care/dashboard)
```

## Sample Requests
### Dev Login
```
POST /auth/dev
```

### Create Feeding
```
POST /feedings
Authorization: Bearer <token>
Content-Type: application/json

{
  "subjectId": "<uuid>",
  "idempotencyKey": "<uuid>",
  "fedAt": "2026-02-06T12:30:00+09:00",
  "amountMl": 160,
  "method": "BOTTLE",
  "note": "꿀꺽꿀꺽 잘 먹음"
}
```

### Create Care Event (Diaper)
```
POST /care-events
Authorization: Bearer <token>
Content-Type: application/json

{
  "subjectId": "<uuid>",
  "type": "DIAPER_POO",
  "occurredAt": "2026-02-06T14:00:00+09:00",
  "idempotencyKey": "<uuid>",
  "payload": {
    "color": "GOLD",
    "amount": "MUCH",
    "memo": "황금변! 상태 좋음"
  }
}
```

### Dashboard Timeline
```
GET /subjects/<subjectId>/timeline?date=2026-02-06
Authorization: Bearer <token>
```

## Error Response Format
```
{
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "errors": [
    { "field": "payload.memo", "message": "size must be between 0 and 500" }
  ]
}
```
