CREATE TABLE IF NOT EXISTS family_schedule (
  id uuid PRIMARY KEY,
  group_id uuid NOT NULL,

  creator_id uuid NOT NULL,              -- 일정을 만든 사람 (예: 남편)
  assignee_id uuid NOT NULL,             -- 함께 할 사람 (예: 아내)

  title varchar(100) NOT NULL,           -- "주말 이마트 장보기"
  description text NULL,                 -- "기저귀, 분유 사기"

  start_time timestamptz NOT NULL,       -- 일정 시작 시간
  end_time timestamptz NOT NULL,         -- 일정 종료 시간

  status varchar(20) NOT NULL,           -- PENDING(대기), ACCEPTED(수락), REJECTED(거절)

  google_event_id varchar(255) NULL,     -- 수락 시 생성된 구글 캘린더 이벤트 ID

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT fk_schedule_group FOREIGN KEY (group_id) REFERENCES app_group(id)
);

-- 수신자(assignee)가 '대기 중'인 알림을 빨리 찾기 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_schedule_assignee_status ON family_schedule(assignee_id, status);
