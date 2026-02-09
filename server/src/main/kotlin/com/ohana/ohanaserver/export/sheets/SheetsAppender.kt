package com.ohana.ohanaserver.export.sheets

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

interface SheetsAppender {
    fun appendRows(groupId: UUID, values: List<List<Any>>)
}

@Component
class LoggingSheetsAppender : SheetsAppender {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun appendRows(groupId: UUID, values: List<List<Any>>) {
        // 실제 연동 전까지는 로그로 확인
        log.info("📝 [Sheets Export] Group={}, Rows={}", groupId, values.size)
        values.forEach { row ->
            log.info("   -> Data: {}", row)
        }
    }
}