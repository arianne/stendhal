/* $Id$ */
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
package games.stendhal.tools.updateprop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Updates update.properties for a new release.
 *
 * @author hendrik
 */
public class UpdatePropUpdater {
	private String oldFile;
	private String newFile;
	private String oldVersion;
	private String newVersion;
	private String folder;
	private List<String> files;
	private Properties prop;
	private Signature signer;

	/**
	 * Creates a new UpdatePropUpdater.
	 *
	 * @param oldFile    name of old file
	 * @param newFile    name of new file
	 * @param oldVersion last version
	 * @param newVersion new version
	 * @param folder     folder the .jar files are in
	 * @param files      list of files
	 * @throws Exception 
	 */
	public UpdatePropUpdater(final String oldFile, final String newFile, final String oldVersion, final String newVersion, String folder, List<String> files) throws Exception {
		this.newFile = newFile;
		this.newVersion = newVersion;
		this.oldFile = oldFile;
		this.oldVersion = oldVersion;
		this.folder = folder;
		this.files = new ArrayList<String>(files);
		initSigner();
	}

	private void initSigner() throws Exception {
		Properties antProp = new Properties();
		try {
			InputStream is = UpdatePropUpdater.class.getClassLoader().getResourceAsStream("build.ant-private.properties");
			antProp.load(is);
			is.close();
		} catch (IOException e) {
			System.err.println("Loading build.ant-private.properties with parameters keystore.alias and keystore.password failed");
			System.exit(1);
		}

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		// get user password and file input stream
		char[] password = antProp.getProperty("keystore.password").toCharArray();
		InputStream is = UpdatePropUpdater.class.getClassLoader().getResourceAsStream("keystore.ks");
		ks.load(is, password);
		is.close();

		// get my private key
		KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(password);
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(antProp.getProperty("keystore.alias"), protection);
		PrivateKey key = pkEntry.getPrivateKey();

		signer = Signature.getInstance("SHA1withRSA");
		signer.initSign(key);
	}

	/**
	 * Updates the update.properties file.
	 *
	 * @throws Exception in case of an unexpected error
	 */
	public void process() throws Exception {
		loadOldUpdateProperties();
		updateVersion();
		updateInit();
		updateUpdateFileList();
		updateFileSizeAndSignature();
		writeNewUpdateProperties();
	}

	/**
	 * loads the current version of the update.properties
	 *
	 * @throws IOException in case of an input/output error
	 */
	private void loadOldUpdateProperties() throws IOException {
		prop = new Properties();
		InputStream is;
		if (oldFile.indexOf(":") > 2) {
			URL url = new URL(oldFile);
			is = url.openStream();
		} else {
			is = new FileInputStream(oldFile);
		}
		prop.load(is);
		is.close();
	}

	/**
	 * updates the version number.
	 */
	private void updateVersion() {
		prop.put("version." + oldVersion, "UPDATE_NEEDED");
		prop.put("version." + newVersion, "CURRENT");
		prop.put("version.destination." + oldVersion, newVersion);
	}

	/**
	 * updates the init statement
	 */
	private void updateInit() {
		prop.put("init.file-list", prop.getProperty("init.file-list").replaceAll(oldVersion, newVersion));
		prop.put("init.version", newVersion);

	}

	/**
	 * updates the update-file-list
	 */
	private void updateUpdateFileList() {
		StringBuilder updateList = new StringBuilder();
		for (String file : files) {
			if (updateList.length() > 0) {
				updateList.append(",");
			}
			if (file.indexOf("diff") > -1) {
				updateList.append(file);
			}
		}
		prop.put("update-file-list." + oldVersion, updateList.toString());
	}

	/**
	 * update the file size and signature sections
	 */
	private void updateFileSizeAndSignature() throws Exception{
		for (String filename : files) {
			File file = new File(folder + "/" + filename);
			prop.put("file-size." + filename, Long.toString(file.length()));
			
			InputStream is = new BufferedInputStream(new FileInputStream(folder + "/" + filename));
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) >= 0) {
				signer.update(buffer, 0, len);
			}
			is.close();
			byte[] realSig = signer.sign();
			prop.put("file-signature." + filename, String.format("%x", new BigInteger(1, realSig)));
		}
	}

	/**
	 * writes the new version of the update.properties
	 *
	 * @throws IOException in case of an input/output error
	 */
	private void writeNewUpdateProperties() throws IOException {
		PrintStream ps = new PrintStream(new FileOutputStream(newFile));
		UpdatePropertiesWriter writer = new UpdatePropertiesWriter(prop, ps);
		writer.process();
		ps.close();
	}

	/**
	 * generates a new update.properties based on an existing one
	 *
	 * @param args oldFile newFile oldVersion newVersion folder [legacy]
	 * @throws Exception in case of an unexpected error
	 */
	public static void main(final String[] args) throws Exception {
		if ((args.length < 4)) {
			System.err.println("java " + UpdatePropUpdater.class.getName() + " oldFile newFile oldVersion newVersion folder files");
			System.exit(1);
		}
		List<String> files = new LinkedList<String>();
		for (int i = 5; i < args.length; i++) {
			files.add(args[i]);
		}
		UpdatePropUpdater updater = new UpdatePropUpdater(args[0], args[1], args[2], args[3], args[4], files);
		updater.process();

		System.err.println("Check and update parameter init.file-list.");
	}
}
