package com.ohana.ohanaserver.inventory.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "재고 아이템")
@Entity
@Table(name = "inventory_item")
class InventoryItem(
    @Id
    @Schema(description = "재고 아이템 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,
    
    @Schema(description = "아이템 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    val itemType: ItemType,
    
    @Schema(description = "아이템 이름 (예: 하기스 네이처메이드 3단계)")
    @Column(nullable = false)
    var name: String,

    @Schema(description = "남은 수량")
    @Column(name = "remaining_count", nullable = false)
    var remainingCount: Int = 0,

    @Schema(description = "재고 부족 알림 기준 수량")
    @Column(name = "alert_threshold", nullable = false)
    var alertThreshold: Int = 20,
    
    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    fun addStock(amount: Int) {
        this.remainingCount += amount
        this.updatedAt = OffsetDateTime.now()
    }

    fun decreaseStock(amount: Int) {
        this.remainingCount -= amount
        if (this.remainingCount < 0) this.remainingCount = 0
        this.updatedAt = OffsetDateTime.now()
    }

    @Schema(hidden = true)
    fun isLowStock(): Boolean = remainingCount <= alertThreshold
}

@Schema(description = "재고 아이템 타입 (DIAPER: 기저귀, FORMULA: 분유, WIPES: 물티슈)")
enum class ItemType { DIAPER, FORMULA, WIPES }
