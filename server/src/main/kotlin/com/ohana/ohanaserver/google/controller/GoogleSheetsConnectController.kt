package com.ohana.ohanaserver.google.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.google.service.GoogleOAuthService
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "구글 연동", description = "구글 시트/캘린더 연동 관련 API")
@RestController
@RequestMapping("/google/sheets/connect")
class GoogleSheetsConnectController(
    private val oauthService: GoogleOAuthService,
    private val groupMemberRepository: GroupMemberRepository
) {
    data class StartResponse(val url: String)

    @Operation(summary = "구글 연동 시작", description = "구글 계정 연동을 위한 OAuth 동의 URL을 발급받습니다.")
    @PostMapping("/start")
    fun start(): StartResponse {
        val userId = SecurityUtil.currentUserId()
        val groupMember = groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?: throw IllegalStateException("No group membership for user: $userId")
        val groupId = groupMember.groupId

        val url = oauthService.buildConsentUrl(state = groupId.toString())
        return StartResponse(url)
    }

    @Operation(summary = "구글 연동 콜백", description = "OAuth 동의 후 리다이렉트되는 주소입니다. (직접 호출X)", hidden = true)
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
