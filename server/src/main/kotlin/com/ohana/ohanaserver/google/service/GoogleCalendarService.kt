package com.ohana.ohanaserver.google.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import com.ohana.ohanaserver.common.crypto.TokenCrypto
import com.ohana.ohanaserver.google.repository.GroupGoogleSheetsConnectionRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

@Service
class GoogleCalendarService(
    private val oauthService: GoogleOAuthService,
    private val connRepo: GroupGoogleSheetsConnectionRepository,
    private val tokenCrypto: TokenCrypto
) {
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val transport = GoogleNetHttpTransport.newTrustedTransport()

    // 0. 캘린더 클라이언트 생성 (Sheets 때랑 비슷함)
    private fun getClient(groupId: UUID): Calendar {
        val conn = connRepo.findByGroupId(groupId)
            ?: throw IllegalStateException("구글 연동이 필요합니다.")

        // 토큰 복호화 & 갱신
        val refreshToken = tokenCrypto.decrypt(conn.refreshTokenEncrypted)
        val tokenRes = oauthService.refreshAccessToken(refreshToken) // 이 메소드 public이어야 함!

        return Calendar.Builder(transport, jsonFactory) { req ->
            req.headers.setAuthorization("Bearer ${tokenRes.access_token}")
        }.setApplicationName("Ohana").build()
    }

    // 1. 일정 생성 (이벤트 ID 반환)
    fun createEvent(groupId: UUID, title: String, date: LocalDate, description: String): String? {
        return try {
            val calendar = getClient(groupId)

            // 날짜 변환 (하루 종일 이벤트)
            val eventDate = DateTime(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            
            val event = Event()
                .setSummary("💉 $title") // 예: "💉 도현 - B형간염 2차"
                .setDescription(description)
                .setStart(EventDateTime().setDate(eventDate))
                .setEnd(EventDateTime().setDate(eventDate))
                // 알림 설정 (전날, 당일 아침 9시)
                .setReminders(
                    Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(listOf(
                            EventReminder().setMethod("popup").setMinutes(24 * 60), // 1일 전
                            EventReminder().setMethod("popup").setMinutes(9 * 60)   // 9시간 전 (보통 당일 아침)
                        ))
                )

            // 'primary'는 로그인한 계정의 기본 캘린더
            val created = calendar.events().insert("primary", event).execute()
            created.id
        } catch (e: Exception) {
            e.printStackTrace()
            null // 실패해도 DB 저장은 되어야 하니까 null 반환
        }
    }

    // 2. 일정 제목 수정 (접종 완료 시 체크 표시 ✅)
    fun updateEventTitle(groupId: UUID, eventId: String, newTitle: String) {
        try {
            val calendar = getClient(groupId)
            // 기존 이벤트 가져와서 제목만 바꾸고 업데이트
            val event = calendar.events().get("primary", eventId).execute()
            event.summary = newTitle
            calendar.events().update("primary", eventId, event).execute()
        } catch (e: Exception) {
            println("캘린더 수정 실패 (삭제된 일정일 수 있음): ${e.message}")
        }
    }
}
