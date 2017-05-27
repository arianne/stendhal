/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * creates signatures for the listed files
 *
 * @author hendrik
 */
public class UpdateSigner extends Task {

	/** list of files to sign */
	private final List<FileSet> filesets = new ArrayList<FileSet>();
	private final Signature signer;

	/**
	 * creates a UpdateSigner
	 * @throws Exception
	 */
	public UpdateSigner() throws Exception {
		Properties antProp = new Properties();
		InputStream is = UpdatePropUpdater.class.getClassLoader().getResourceAsStream("build.ant-private.properties");
		if (is == null) {
			throw new IOException("Loading build.ant-private.properties with parameters keystore.alias and keystore.password failed");
		}
		antProp.load(is);
		is.close();
		if ((antProp.getProperty("keystore.password") == null) || (antProp.getProperty("keystore.update-alias") == null)) {
			throw new IllegalArgumentException("build.ant-private.properties is missing parameters keystore.alias or keystore.password");
		}

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		// get user password and file input stream
		char[] password = antProp.getProperty("keystore.password").toCharArray();
		is = UpdatePropUpdater.class.getClassLoader().getResourceAsStream("keystore.ks");
		if (is == null) {
			throw new IOException("No keystore.ks in root folder.");
		}
		ks.load(is, password);
		is.close();

		// get my private key
		KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(password);
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(antProp.getProperty("keystore.update-alias"), protection);
		PrivateKey key = pkEntry.getPrivateKey();

		signer = Signature.getInstance("SHA1withRSA");
		signer.initSign(key);
	}


	public String sign(String fullFilename) throws IOException, SignatureException {
		InputStream is = new BufferedInputStream(new FileInputStream(fullFilename));
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) >= 0) {
			signer.update(buffer, 0, len);
		}
		is.close();
		byte[] realSig = signer.sign();
		return String.format("%x", new BigInteger(1, realSig));
	}


	/**
	 * Adds a set of files to copy.
	 *
	 * @param set
	 *            a set of files to copy
	 */
	public void addFileset(final FileSet set) {
		filesets.add(set);
	}

	/**
	 * ants execute method.
	 */
	@Override
	public void execute() {
		try {
			for (final FileSet fileset : filesets) {
				final DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
				final String[] includedFiles = ds.getIncludedFiles();
				for (final String filename : includedFiles) {
					String signature = sign(ds.getBasedir().getAbsolutePath()
							+ File.separator + filename);
					System.out.println("file-signature." + filename + "=" + signature);
				}
			}
		} catch (final Exception e) {
			throw new BuildException(e);
		}
	}

	/**
	 * signs files
	 *
	 * @param args file1 file2 ...
	 * @throws Exception in case of an error
	 */
	public static void main(String[] args) throws Exception {
		UpdateSigner signer = new UpdateSigner();
		for (String filename : args) {
			String signature = signer.sign(filename);
			System.out.println("file-signature." + new File(filename).getName() + "=" + signature);
		}
	}
}
