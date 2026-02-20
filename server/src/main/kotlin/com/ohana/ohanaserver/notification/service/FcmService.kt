package com.ohana.ohanaserver.notification.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.ohana.ohanaserver.notification.domain.UserDevice
import com.ohana.ohanaserver.notification.repository.UserDeviceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class FcmService(
    private val userDeviceRepository: UserDeviceRepository
) {
    // 폰에서 받은 토큰 저장
    @Transactional
    fun registerToken(userId: UUID, token: String) {
        val device = userDeviceRepository.findById(userId).orElse(null)
        if (device != null) {
            device.updateToken(token)
            userDeviceRepository.save(device)
        } else {
            userDeviceRepository.save(UserDevice(userId = userId, fcmToken = token))
        }
    }

    // 알림 쏘기
    fun sendPush(userId: UUID, title: String, body: String) {
        val device = userDeviceRepository.findById(userId).orElse(null) ?: return

        try {
            val message = Message.builder()
                .setToken(device.fcmToken)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build()

            FirebaseMessaging.getInstance().send(message)
            println("푸시 발송 완료: $title")
        } catch (e: Exception) {
            println("푸시 발송 에러: ${e.message}")
        }
    }
}
