package org.r1.gde.demo;


import java.io.File;

/**
 * Hello world!
 *
 */
public class App {
	private static File bvFile;
	private static File decanteursFile;

	public static void main(String[] args) {
		System.out.println("Moulinette GDE en action ...");
		if (evaluateArguments(args)) {
			System.out.println("Les arguments sont corrects, on continue");
			GDEProcess p = new GDEProcess(bvFile, decanteursFile);
			p.start();
		} else {
			System.out.println("Les arguments doivent être <BV.dbf> <DECANTEURS.bdf");
		}
	}

	private static boolean evaluateArguments(String[] args) {
		boolean arguments = false;
		if (args.length == 2) {
			String bvFilePath = args[0];
			String decanteursFilePath = args[1];
			
			bvFile = new File(bvFilePath);
			decanteursFile = new File(decanteursFilePath);
			
			if (bvFile.exists() && decanteursFile.exists()) {
				arguments = true;
			}
		} 
		return arguments;
	}
}
