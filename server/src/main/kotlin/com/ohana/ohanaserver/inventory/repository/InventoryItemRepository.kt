package com.ohana.ohanaserver.inventory.repository

import com.ohana.ohanaserver.inventory.domain.InventoryItem
import com.ohana.ohanaserver.inventory.domain.ItemType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InventoryItemRepository : JpaRepository<InventoryItem, UUID> {
    fun findByGroupIdAndItemType(groupId: UUID, itemType: ItemType): InventoryItem?
    fun findAllByGroupId(groupId: UUID): List<InventoryItem>
}
