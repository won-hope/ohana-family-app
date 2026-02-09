package com.ohana.ohanaserver.export.scheduler

import com.ohana.ohanaserver.export.service.FeedingSheetsExportService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId

@EnableScheduling
@Component
class FeedingExportScheduler(
    private val exportService: FeedingSheetsExportService
) {
    private val zone = ZoneId.of("Asia/Seoul")

    // 매일 00:10 (KST)에 실행 -> "어제" 데이터를 보냄
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    fun runDaily() {
        val yesterday = LocalDate.now(zone).minusDays(1)
        exportService.exportAllGroupsForDate(yesterday)
    }
}