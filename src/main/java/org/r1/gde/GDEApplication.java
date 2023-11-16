package org.r1.gde;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

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

		try {
			LicenseUtils.checkLicense();
		} catch (LicenseException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "GDE: Erreur au démarrage", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(GDEApplication.class);
		builder.headless(false);

		splashScreen = new SplashScreen();
		splashScreen.setVisible(true);

		ConfigurableApplicationContext context = builder.run(args);
	}

	


}
