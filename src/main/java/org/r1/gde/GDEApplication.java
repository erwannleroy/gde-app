package org.r1.gde;

import org.r1.gde.controller.GDEController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Configuration
@Slf4j
public class GDEApplication {
	
	public static SplashScreen splashScreen;

	public static void main(String[] args) {
		log.info("Démarrage de l'application GDE");

		SpringApplicationBuilder builder = new SpringApplicationBuilder(GDEApplication.class);
		builder.headless(false);
		
		splashScreen = new SplashScreen();
		splashScreen.setVisible(true);
		
		ConfigurableApplicationContext context = builder.run(args);
	}

	
}
