/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.mapeditor.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

import tiled.io.MapReader;
import tiled.io.MapWriter;
import tiled.io.PluggableMapIO;
import tiled.plugins.MapReaderPlugin;
import tiled.plugins.MapWriterPlugin;
import tiled.util.TiledConfiguration;

public final class PluginClassLoader extends URLClassLoader {
	private List<MapReader> readers;
	private List<MapWriter> writers;
	private Map<String, String> readerFormats;
	private Map<String, String> writerFormats;
	private static PluginClassLoader instance;

	private List<Class> readerPlugins;
	private List<Class> writerPlugins;

	public PluginClassLoader() {
		this(new URL[0]);
	}

	public PluginClassLoader(URL[] urls) {
		super(urls);
		readers = new ArrayList<MapReader>();
		writers = new ArrayList<MapWriter>();
		readerFormats = new HashMap<String, String>();
		writerFormats = new HashMap<String, String>();

		readerPlugins = new ArrayList<Class>();
		writerPlugins = new ArrayList<Class>();

	}

	public static PluginClassLoader getInstance() {
		if (instance == null) {
			instance = new PluginClassLoader();
		}
		return instance;
	}

	/** reads the new plugin format. */
	private void readPluginsNew(String base) {
		String baseURL = (base != null) ? base : TiledConfiguration.getInstance().getValue("tiled.plugins.dir");
		File dir = new File(baseURL);
		if (!dir.exists() || !dir.canRead()) {
			// not a valid readable directory
			return;
		}

		File[] files = dir.listFiles();

		// now read each jar
		for (int i = 0; i < files.length; i++) {
			String aPath = files[i].getAbsolutePath();

			if (aPath.endsWith(".jar")) {
				try {
					JarFile jarFile = new JarFile(files[i]);

					if (jarFile.getManifest() == null) {
						// cannot process jars without a manifest
						continue;
					}

					String readerClassName = jarFile.getManifest().getMainAttributes().getValue("Reader-Class");
					String writerClassName = jarFile.getManifest().getMainAttributes().getValue("Writer-Class");

					Class readerClass = null;
					Class writerClass = null;

					// Verify that the jar has the necessary files to be a
					// plugin
					if (readerClassName == null && writerClassName == null) {
						continue;
					}

					// add this jar to the class-loader
					addURL((new File(aPath)).toURL());

					if (readerClassName != null) {
						JarEntry reader = jarFile.getJarEntry(readerClassName.replace('.', '/') + ".class");

						if (reader != null) {
							readerClass = loadFromJar(jarFile, reader, readerClassName);
						}
					}
					if (writerClassName != null) {
						JarEntry writer = jarFile.getJarEntry(writerClassName.replace('.', '/') + ".class");

						if (writer != null) {
							writerClass = loadFromJar(jarFile, writer, writerClassName);
						}
					}

					if (readerClass != null && readerClass.isAssignableFrom(MapReaderPlugin.class)) {
						readerPlugins.add(readerClass);
					}
					if (writerClass != null && writerClass.isAssignableFrom(MapWriterPlugin.class)) {
						writerPlugins.add(writerClass);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void readPlugins(String base, JFrame parent) throws Exception {

		readPluginsNew(base);

		String baseURL = base;
		ProgressMonitor monitor;

		if (base == null) {
			baseURL = TiledConfiguration.getInstance().getValue("tiled.plugins.dir");
		}

		File dir = new File(baseURL);
		if (!dir.exists() || !dir.canRead()) {
			// FIXME: removed for webstart
			// throw new Exception(
			// "Could not open directory for reading plugins: " +
			// baseURL);
			return;
		}

		int total = 0;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String aPath = files[i].getAbsolutePath();
			if (aPath.endsWith(".jar")) {
				total++;
			}
			readers = new ArrayList<MapReader>();
			writers = new ArrayList<MapWriter>();
		}

		// Start the progress monitor
		monitor = new ProgressMonitor(parent, "Loading plugins", "", 0, total - 1);
		monitor.setProgress(0);
		monitor.setMillisToPopup(0);
		monitor.setMillisToDecideToPopup(0);

		for (int i = 0; i < files.length; i++) {
			String aPath = files[i].getAbsolutePath();
			String aName = aPath.substring(aPath.lastIndexOf(File.separatorChar) + 1);

			if (aPath.endsWith(".jar")) {
				try {
					monitor.setNote("Reading " + aName + "...");
					JarFile jf = new JarFile(files[i]);

					monitor.setProgress(i);

					if (jf.getManifest() == null) {
						continue;
					}

					String readerClassName = jf.getManifest().getMainAttributes().getValue("Reader-Class");
					String writerClassName = jf.getManifest().getMainAttributes().getValue("Writer-Class");

					Class readerClass = null;
					Class writerClass = null;

					// Verify that the jar has the necessary files to be a
					// plugin
					if (readerClassName == null && writerClassName == null) {
						continue;
					}

					monitor.setNote("Loading " + aName + "...");
					addURL((new File(aPath)).toURL());

					if (readerClassName != null) {
						JarEntry reader = jf.getJarEntry(readerClassName.replace('.', '/') + ".class");

						if (reader != null) {
							readerClass = loadFromJar(jf, reader, readerClassName);
						}
					}
					if (writerClassName != null) {
						JarEntry writer = jf.getJarEntry(writerClassName.replace('.', '/') + ".class");

						if (writer != null) {
							writerClass = loadFromJar(jf, writer, writerClassName);
						}
					}

					boolean bPlugin = false;
					if (doesImplement(readerClass, "tiled.io.MapReader")) {
						bPlugin = true;
					}
					if (doesImplement(writerClass, "tiled.io.MapWriter")) {
						bPlugin = true;
					}

					if (bPlugin) {
						if (readerClass != null) {
							_add(readerClass);
						}
						if (writerClass != null) {
							_add(writerClass);
						// System.out.println(
						// "Added " + files[i].getCanonicalPath());
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public MapReader[] getReaders() {
		return readers.toArray(new MapReader[readers.size()]);
	}

	public MapWriter[] getWriters() {
		return writers.toArray(new MapWriter[writers.size()]);
	}

	public Object getReaderFor(String file) throws Exception {
		Iterator itr = readerFormats.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			String ext = key.substring(1);
			if (file.toLowerCase().endsWith(ext)) {
				return loadClass((String) readerFormats.get(key)).newInstance();
			}
		}
		throw new Exception("No reader plugin exists for this file type.");
	}

	public Object getWriterFor(String file) throws Exception {
		Iterator itr = writerFormats.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			String ext = key.substring(1);
			if (file.toLowerCase().endsWith(ext)) {
				return loadClass((String) writerFormats.get(key)).newInstance();
			}
		}
		throw new Exception("No writer plugin exists for this file type.");
	}

	public Class loadFromJar(JarFile jf, JarEntry je, String className) throws IOException {
		byte[] buffer = new byte[(int) je.getSize()];
		int n;

		InputStream in = jf.getInputStream(je);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((n = in.read(buffer)) > 0) {
			baos.write(buffer, 0, n);
		}
		buffer = baos.toByteArray();

		if (buffer.length < je.getSize()) {
			throw new IOException("Failed to read entire entry! (" + buffer.length + "<" + je.getSize() + ")");
		}

		return defineClass(className, buffer, 0, buffer.length);
	}

	public boolean doesImplement(Class c, String interfaceName) throws Exception {
		if (c == null) {
			return false;
		}

		Class[] interfaces = c.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			String name = interfaces[i].toString();
			if (name.substring(name.indexOf(' ') + 1).equals(interfaceName)) {
				return true;
			}
		}
		return false;
	}

	private boolean isReader(Class c) throws Exception {
		return doesImplement(c, "tiled.io.MapReader");
	}

	private void _add(Class c) throws Exception {
		try {
			PluggableMapIO p = (PluggableMapIO) c.newInstance();
			String clname = c.toString();
			clname = clname.substring(clname.indexOf(' ') + 1);
			String filter = p.getFilter();
			String[] ext = filter.split(",");

			if (isReader(c)) {
				for (int i = 0; i < ext.length; i++) {
					readerFormats.put(ext[i], clname);
				}
				readers.add((MapReader) p);
			} else {
				for (int i = 0; i < ext.length; i++) {
					writerFormats.put(ext[i], clname);
				}
				writers.add((MapWriter) p);
			}
		} catch (NoClassDefFoundError e) {
			System.err.println("**Failed loading plugin: " + e.toString());
		}
	}
}
