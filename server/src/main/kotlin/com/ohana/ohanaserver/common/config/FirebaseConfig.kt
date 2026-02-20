package com.ohana.ohanaserver.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp? {
        return try {
            // 이미 초기화되어 있는지 확인
            if (FirebaseApp.getApps().isNotEmpty()) {
                return FirebaseApp.getInstance()
            }

            // serviceAccountKey.json 파일이 resources 폴더에 있어야 함
            val resource = ClassPathResource("serviceAccountKey.json")
            
            // 파일이 없으면 초기화 건너뜀 (로컬 개발 편의성)
            if (!resource.exists()) {
                println("⚠️ Firebase serviceAccountKey.json not found. Push notifications will not work.")
                return null
            }

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
                .build()

            FirebaseApp.initializeApp(options)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
