package com.ohana.ohanaserver.inventory.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

enum class ItemType { DIAPER, FORMULA, WIPES }

@Entity
@Table(name = "inventory_item")
class InventoryItem(
    @Id val id: UUID = UUID.randomUUID(),
    @Column(name = "group_id", nullable = false) val groupId: UUID,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false) val itemType: ItemType,
    
    @Column(nullable = false) var name: String,
    @Column(name = "remaining_count", nullable = false) var remainingCount: Int = 0,
    @Column(name = "alert_threshold", nullable = false) var alertThreshold: Int = 20,
    
    @Column(name = "created_at", nullable = false) val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
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

    fun isLowStock(): Boolean = remainingCount <= alertThreshold
}
