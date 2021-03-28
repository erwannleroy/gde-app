package org.r1.gde;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BrowserLauncher {

	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;

	@EventListener(ApplicationReadyEvent.class)
	public void launchBrowser() {

		int port = webServerAppCtxt.getWebServer().getPort();
		String homepage = "http://localhost:" + port + "/index.html";
		log.info("Application lancée : on démarre le browser : " + homepage);
		;
		System.setProperty("java.awt.headless", "false");
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI(homepage));
			GDEApplication.splashScreen.setVisible(false);
		} catch (Exception e) {
			log.error("Impossible de démarrer le navigateur");
		}
	}


}
