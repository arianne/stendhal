/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.cert.CertificateException;

/**
 * verifies a signature
 */
class SignatureVerifier {
	private static SignatureVerifier instance;
	private KeyStore ks;

	private SignatureVerifier() {
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			String keystoreFilename = ClientGameConfiguration.get("UPDATE_CERTSTORE");
			InputStream is = UpdateManager.class.getClassLoader().getResourceAsStream(keystoreFilename);
			if (is != null) {
				ks.load(is, null);
				is.close();
			} else {
				System.err.println("Certstore " + keystoreFilename + " not found, configured as UPDATE_CERTSTORE in game.properties.");
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * gets the Signature Verifier
	 *
	 * @return SignatureVerifier
	 */
	static synchronized SignatureVerifier get() {
		if (instance == null) {
			instance = new SignatureVerifier();
		}
		return instance;
	}

	/**
	 * Checks the signature of a file
	 *
	 * @param filename name of file
	 * @param signature signature
	 * @return true, if the signature is fine, false otherwise.
	 */
	boolean checkSignature(String filename, String signature) {
		if ((ks == null) || (signature == null)) {
			System.out.println("No signature for " + filename);
			return false;
		}
		try {

			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(ks.getCertificate(ClientGameConfiguration.get("UPDATE_CERT_NAME")).getPublicKey());

			FileInputStream datafis = new FileInputStream(filename);
			InputStream buf = new BufferedInputStream(datafis);

			byte[] temp = new byte[1024];
			int length = 0;
			try {
				while (buf.available() != 0) {
					length = buf.read(temp);
					sig.update(temp, 0, length);
				}
			} finally {
				buf.close();
			}

			boolean isVaild = sig.verify(hexStringToByteArray(signature));
			System.out.println("Validated " + filename + ": " + isVaild);
			return isVaild;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * converts a hexadecimal string into an byte array
	 *
	 * @param hexString hexadecimal encoded string
	 * @return byte[]
	 */
	// http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java/140861#140861
	static byte[] hexStringToByteArray(String hexString) {
		String s = hexString;

		// handle uneven number of hex digits
		int len = s.length();
		if (len % 2 == 1) {
			s = "0" + s;
			len++;
		}

		// decode
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
