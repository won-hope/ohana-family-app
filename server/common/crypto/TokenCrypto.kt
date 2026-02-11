package com.ohana.ohanaserver.common.crypto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

@Component
class TokenCrypto(@Value("\${ohana.crypto.token-secret}") secret: String) {
    private val key = SecretKeySpec(secret.toByteArray().copyOf(32), "AES") // 32byte 강제

    fun encrypt(plain: String): String {
        val iv = ByteArray(12).apply { SecureRandom().nextBytes(this) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        }
        return Base64.getEncoder().encodeToString(iv + cipher.doFinal(plain.toByteArray()))
    }

    fun decrypt(cipherText: String): String {
        val decoded = Base64.getDecoder().decode(cipherText)
        val iv = decoded.copyOfRange(0, 12)
        val content = decoded.copyOfRange(12, decoded.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        }
        return String(cipher.doFinal(content))
    }
}
