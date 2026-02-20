package com.ohana.ohanaserver.inventory.service

import com.ohana.ohanaserver.inventory.domain.InventoryItem
import com.ohana.ohanaserver.inventory.domain.ItemType
import com.ohana.ohanaserver.inventory.repository.InventoryItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class InventoryService(
    private val inventoryRepository: InventoryItemRepository
) {
    // 1. 재고 추가 (예: 쿠팡에서 기저귀 400개 샀을 때)
    @Transactional
    fun addStock(groupId: UUID, type: ItemType, name: String, amount: Int, threshold: Int): InventoryItem {
        val item = inventoryRepository.findByGroupIdAndItemType(groupId, type)
            ?: InventoryItem(groupId = groupId, itemType = type, name = name, alertThreshold = threshold)
        
        item.name = name // 이름 업데이트
        item.alertThreshold = threshold
        item.addStock(amount)
        
        return inventoryRepository.save(item)
    }

    // 2. 재고 차감 (앱에서 대소변 버튼 눌렀을 때 호출됨)
    @Transactional
    fun decreaseStock(groupId: UUID, type: ItemType, amount: Int) {
        val item = inventoryRepository.findByGroupIdAndItemType(groupId, type) ?: return
        
        item.decreaseStock(amount)
        inventoryRepository.save(item)

        // ⚠️ 재고 부족 경고
        if (item.isLowStock()) {
            println("🚨 [알림] ${item.name} 재고가 ${item.remainingCount}개 남았습니다! (주문 필요)")
            // TODO: 추후 여기에 FCM 푸시 알림 발송 로직 연결
        }
    }
}
