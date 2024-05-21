package org.r1.gde;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LicenseUtils {

	public static void main(String[] args) throws LicenseException  {
		System.out.println("Génération de license pour " + args[0]);
		File lic = encrypt(args[0]);
		System.out.println("License générée ici : " + lic.getAbsolutePath());
	}

	public static String decrypt() throws LicenseException {
		try {
			String encryptionKeyString = "thisisa128bitkey";
			byte[] encryptionKeyBytes = encryptionKeyString.getBytes();

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKey key = new SecretKeySpec(encryptionKeyBytes, "AES");

			File licenseFile = findLicenseFile();
			FileInputStream fileIn = new FileInputStream(licenseFile);
//			byte[] fileIv = new byte[16];
//			fileIn.read(fileIv);
//			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(fileIv));
			cipher.init(Cipher.DECRYPT_MODE, key);

			String content = "";
			try (CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
					InputStreamReader inputReader = new InputStreamReader(cipherIn);
					BufferedReader reader = new BufferedReader(inputReader)) {

				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				content = sb.toString();
			}
			return content;

		} catch (Exception e) {
			throw new LicenseException(e.getMessage());
		}
	}

	public static File encrypt(String input) throws LicenseException {
		try {
			String encryptionKeyString = "thisisa128bitkey";
			byte[] encryptionKeyBytes = encryptionKeyString.getBytes();

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKey key = new SecretKeySpec(encryptionKeyBytes, "AES");

			cipher.init(Cipher.ENCRYPT_MODE, key);
//			byte[] iv = cipher.getIV();

			File lic = new File("license");
			FileOutputStream fileOut = new FileOutputStream(lic);
			CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
//			fileOut.write(iv);
			cipherOut.write(input.getBytes());
			cipherOut.close();
			fileOut.close();
			return lic;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new LicenseException(e.getMessage());
		}
	}

	public static File findLicenseFile() throws IOException, ParseException, LicenseException {
		File file = new File("license");
		if (file.exists()) {
			return file;
		} else {
			String msg = "Aucun fichier license trouvé";
			log.error(msg);
			throw new LicenseException(msg);
		}
	}


	private static void checkDateValidity(String date) throws LicenseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate licenseDate = LocalDate.parse(date, formatter);

		LocalDate now = LocalDate.now();

		log.info("License valide jusqu'au " + licenseDate.format(formatter));
		log.info("Et nous sommes le " + now.format(formatter));
		if (now.isBefore(licenseDate)) {
			log.info("License valide jusqu'au " + licenseDate.format(formatter));
		} else {
			String msg = "License dépassée depuis " + licenseDate.format(formatter);
			log.error(msg);
			throw new LicenseException(msg);
		}
	}

	public static void checkLicense() throws LicenseException {
		String licenseContent = decrypt();
		checkDateValidity(licenseContent);
	}

}
