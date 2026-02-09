package com.ohana.ohanaserver.export.controller

import com.ohana.ohanaserver.export.service.FeedingSheetsExportService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class TestBatchController(
    private val exportService: FeedingSheetsExportService
) {
    // Test-only: force export for today.
    @PostMapping("/test/export-now")
    fun forceExport() {
        exportService.exportAllGroupsForDate(LocalDate.now())
    }
}
