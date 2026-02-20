package com.ohana.ohanaserver.google.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.google.service.GoogleOAuthService
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/google/sheets/connect")
class GoogleSheetsConnectController(
    private val oauthService: GoogleOAuthService,
    private val groupMemberRepository: GroupMemberRepository
) {
    data class StartResponse(val url: String)

    // 1. 연결 시작 (URL 받기)
    @PostMapping("/start")
    fun start(): StartResponse {
        val userId = SecurityUtil.currentUserId()
        val groupMember = groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?: throw IllegalStateException("No group membership for user: $userId")
        val groupId = groupMember.groupId

        val url = oauthService.buildConsentUrl(state = groupId.toString())
        return StartResponse(url)
    }

    // 2. 동의 후 콜백 (여기서 시트 만들고 저장)
    @GetMapping("/callback")
    fun callback(
        @RequestParam code: String,
        @RequestParam state: String
    ): String {
        // state 파라미터가 groupId로 전달됨
        val groupId = UUID.fromString(state)
        val sheetUrl = oauthService.processCallback(code, groupId)
        return "연동 성공! 시트 주소: $sheetUrl"
    }
}
