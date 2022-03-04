/***************************************************************************
 *                 (C) Copyright 2012-2022 Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sprite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * loads data
 *
 * @author hendrik
 *
 */
public class DataLoader {
	private static Logger logger = Logger.getLogger(DataLoader.class);
	private static Set<String> knownFiles = new HashSet<String>();
	private static Map<String, File> contentFilenameMapping = new HashMap<String, File>();
	private static Map<String, ZipFile> contentZipFilesMapping = new HashMap<String, ZipFile>();

	/**
	 * Finds the resource with the given name. A resource is some data (images,
	 * audio, text, etc) that can be accessed by class code in a way that is
	 * independent of the location of the code.
	 *
	 * <p>
	 * The name of a resource is a '<tt>/</tt>'-separated path name that
	 * identifies the resource.
	 *
	 * @param name
	 *            The resource name
	 *
	 * @return A <tt>URL</tt> object for reading the resource, or <tt>null</tt>
	 *         if the resource could not be found or the invoker doesn't have
	 *         adequate privileges to get the resource.
	 */
	public static URL getResource(String name) {
		String slashlessName = normalizeFilenames(name);
		File file = contentFilenameMapping.get(slashlessName);
		if (file != null) {
			try {
				return new URL("jar:" + file.toURI().toASCIIString() + "!/" + slashlessName);
			} catch (MalformedURLException e) {
				logger.error(e, e);
			}
		}
		return DataLoader.class.getClassLoader().getResource(slashlessName);
	}

	/**
	 * Returns an input stream for reading the specified resource.
	 *
	 * <p>
	 * The search order is described in the documentation for
	 * {@link #getResource(String)}.
	 * </p>
	 *
	 * @param name
	 *            The resource name
	 *
	 * @return An input stream for reading the resource, or <tt>null</tt> if the
	 *         resource could not be found
	 *
	 * @since 1.1
	 */
	public static InputStream getResourceAsStream(String name) {
		String slashlessName = normalizeFilenames(name);
		ZipFile file = contentZipFilesMapping.get(slashlessName);
		if (file != null) {
			ZipEntry entry = file.getEntry(slashlessName);
			try {
				return file.getInputStream(entry);
			} catch (IOException e) {
				logger.error(e, e);
			}
		}
		return DataLoader.class.getClassLoader().getResourceAsStream(slashlessName);
	}

	/**
	 * removes a leading slash and normalize parent references
	 *
	 * @param name filename with or without leading slash
	 * @return filename without leading slash
	 */
	static String normalizeFilenames(String name) {
		name = Paths.get(name).normalize().toString().replace('\\', '/');
		if (name.length() < 1 || name.charAt(0) != '/') {
			return name;
		}
		return name.substring(1);
	}

	/**
	 * adds a .jar file to the repository
	 *
	 * @param filename name of .jar file
	 */
	public static void addJarFile(String filename) {
		try {
			if (knownFiles.contains(filename)) {
				return;
			}
			File file = new File(filename);
			if (!file.canRead()) {
				return;
			}

			knownFiles.add(filename);
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					String name = normalizeFilenames(entry.getName());
					contentFilenameMapping.put(name, file);
					contentZipFilesMapping.put(name, zipFile);
				}
			}
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

}
