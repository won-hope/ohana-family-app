package com.ohana.ohanaserver.schedule.service

import com.ohana.ohanaserver.google.service.GoogleCalendarService
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.schedule.domain.FamilySchedule
import com.ohana.ohanaserver.schedule.domain.ScheduleStatus
import com.ohana.ohanaserver.schedule.repository.FamilyScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class FamilyScheduleService(
    private val scheduleRepository: FamilyScheduleRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val googleCalendarService: GoogleCalendarService
) {
    // 1. 일정 제안하기 (상대방에게 요청)
    @Transactional
    fun proposeSchedule(
        creatorId: UUID, assigneeId: UUID, title: String, desc: String?, 
        start: OffsetDateTime, end: OffsetDateTime
    ): FamilySchedule {
        val groupMember = groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(creatorId)
            ?: throw IllegalStateException("그룹 없음")

        // 💡 팁: 여기서 Firebase Cloud Messaging (FCM) API를 호출하면 
        // 와이프 폰에 "남편님이 일정을 제안했습니다!" 푸시 알림이 날아가!
        
        return scheduleRepository.save(
            FamilySchedule(
                groupId = groupMember.groupId,
                creatorId = creatorId,
                assigneeId = assigneeId,
                title = title,
                description = desc,
                startTime = start,
                endTime = end,
                status = ScheduleStatus.PENDING
            )
        )
    }

    // 2. 상대방이 "추가(수락)" 버튼을 눌렀을 때
    @Transactional
    fun acceptSchedule(assigneeId: UUID, scheduleId: UUID) {
        val schedule = scheduleRepository.findById(scheduleId).orElseThrow()
        
        // 본인에게 온 요청이 맞는지 확인
        if (schedule.assigneeId != assigneeId) throw IllegalArgumentException("권한 없음")
        if (schedule.status != ScheduleStatus.PENDING) throw IllegalArgumentException("이미 처리된 일정")

        // 1. 📅 구글 캘린더에 동시 등록!
        val eventId = googleCalendarService.createTimeBoundEvent(
            groupId = schedule.groupId,
            title = schedule.title,
            desc = schedule.description ?: "",
            start = schedule.startTime,
            end = schedule.endTime
        )

        // 2. DB 상태 업데이트
        schedule.status = ScheduleStatus.ACCEPTED
        schedule.googleEventId = eventId
        schedule.updatedAt = OffsetDateTime.now()
    }

    // 3. 내게 온 대기 중인 요청 목록 조회 (알림창 용도)
    fun getPendingRequests(userId: UUID): List<FamilySchedule> {
        return scheduleRepository.findAllByAssigneeIdAndStatusOrderByCreatedAtDesc(userId, ScheduleStatus.PENDING)
    }
}
