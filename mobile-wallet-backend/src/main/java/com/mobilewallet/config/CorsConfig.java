// FILE: src/main/java/com/mobilewallet/config/CorsConfig.java
package com.mobilewallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

	@Value("${app.cors.allowed-origins:*}")
	private String allowedOrigins;

	@Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
	private String allowedMethods;

	@Bean
	CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
		config.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("Authorization", "X-Refresh-Token", "X-Requested-With"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}