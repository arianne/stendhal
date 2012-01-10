/***************************************************************************
 *                   (C) Copyright 2012 Faiumoni e. V.                     *
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

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * loads data
 * 
 * @author hendrik
 * 
 */
public class DataLoader {
	private static URLClassLoader classLoader = null;
	private static List<URL> urls;

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
		URL url = null;
		if (classLoader != null) {
			url = classLoader.getResource(name);
		}
		if (url == null) {
			url = DataLoader.class.getClassLoader().getResource(name);
		}
		return url;
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
		InputStream is = null;
		if (classLoader != null) {
			is = classLoader.getResourceAsStream(name);
		}
		if (is == null) {
			is = DataLoader.class.getClassLoader().getResourceAsStream(name);
		}
		return is;
	}

	/**
	 * adds an URL
	 *
	 * @param url url
	 */
	public static void addURL(URL url) {
		urls.add(url);
		classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
	}

}
