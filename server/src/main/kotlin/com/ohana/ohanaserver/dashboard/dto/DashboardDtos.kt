package com.ohana.ohanaserver.dashboard.dto

import com.ohana.ohanaserver.care.domain.CareEventType
import com.ohana.ohanaserver.feeding.domain.FeedingMethod
import java.time.OffsetDateTime
import java.util.UUID

data class TimelineItem(
    val id: UUID,
    val subjectId: UUID,
    val occurredAt: OffsetDateTime,
    val category: TimelineCategory,
    val feeding: FeedingItem? = null,
    val care: CareItem? = null
) {
    init {
        require((feeding != null) xor (care != null)) {
            "TimelineItem must contain exactly one of feeding or care"
        }
        require(
            (category == TimelineCategory.FEEDING && feeding != null) ||
                (category == TimelineCategory.CARE && care != null)
        ) {
            "TimelineItem category must match payload"
        }
    }
}

enum class TimelineCategory {
    FEEDING,
    CARE
}

data class FeedingItem(
    val amountMl: Int?,
    val method: FeedingMethod,
    val note: String?,
    val durationSeconds: Int?
)

data class CareItem(
    val type: CareEventType,
    val payload: CarePayload?
)

sealed interface CarePayload

data class DiaperPayload(
    val color: String?,
    val amount: String?,
    val memo: String?
) : CarePayload

data class BathPayload(
    val memo: String?
) : CarePayload

data class TempPayload(
    val celsius: Double?,
    val memo: String?
) : CarePayload

data class SleepPayload(
    val startedAt: OffsetDateTime?,
    val endedAt: OffsetDateTime?,
    val durationMinutes: Int?,
    val memo: String?
) : CarePayload

data class DailySummaryResponse(
    val date: String,
    val subjectId: UUID,
    val lastFeedingAt: OffsetDateTime?,
    val totalFeedingAmountMl: Int,
    val feedingCount: Int,
    val peeCount: Int,
    val pooCount: Int,
    val bathDone: Boolean,
    val latestTemp: Double?,
    val latestTempAt: OffsetDateTime?
)
