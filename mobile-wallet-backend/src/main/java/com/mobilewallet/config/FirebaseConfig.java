// FILE: src/main/java/com/mobilewallet/config/FirebaseConfig.java
package com.mobilewallet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

	@Value("${app.firebase.enabled:false}")
	private boolean firebaseEnabled;

	@Value("${app.firebase.config-path:firebase/service-account.json}")
	private String firebaseConfigPath;

	@PostConstruct
	public void initialize() {
		if (!firebaseEnabled) {
			log.info("Firebase is disabled");
			return;
		}

		try {
			log.info("Initializing Firebase...");

			// Try to load from classpath first
			ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
			GoogleCredentials credentials;

			if (resource.exists()) {
				credentials = GoogleCredentials.fromStream(resource.getInputStream());
			} else {
				// Try to load from file system
				try (FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
					credentials = GoogleCredentials.fromStream(serviceAccount);
				}
			}

			FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				log.info("Firebase initialized successfully");
			}
		} catch (IOException e) {
			log.error("Failed to initialize Firebase", e);
		}
	}

	@Bean
	FirebaseMessaging firebaseMessaging() {
		if (!firebaseEnabled) {
			log.info("Firebase messaging is disabled");
			return null;
		}
		return FirebaseMessaging.getInstance();
	}
}