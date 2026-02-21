package com.ohana.ohanaserver.export.controller

import com.ohana.ohanaserver.export.service.FeedingSheetsExportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "내보내기 (테스트용)", description = "데이터 내보내기(Export) 관련 테스트 API")
@RestController
@Profile("dev")
class TestBatchController(
    private val exportService: FeedingSheetsExportService
) {
    @Operation(summary = "오늘 날짜 수유 기록 강제 내보내기", description = "오늘 날짜의 수유 기록을 구글 시트로 강제 내보내기하는 배치를 실행합니다.")
    @PostMapping("/test/export-now")
    fun forceExport() {
        exportService.exportAllGroupsForDate(LocalDate.now())
    }
}
