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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tiled.plugins.MapReaderPlugin;
import tiled.plugins.MapWriterPlugin;
import tiled.plugins.TiledPlugin;
import tiled.plugins.stendhal.StendReader;
import tiled.plugins.stendhal.StendWriter;
import tiled.plugins.stendhal.XStendReader;
import tiled.plugins.stendhal.XStendWriter;
import tiled.plugins.tiled.MapReader;
import tiled.plugins.tiled.MapWriter;

/**
 * A generic PlugIn Manager.
 * 
 * @author mtotz
 */
public class PluginManager {
	private Map<Class<? extends TiledPlugin>, List<Class<? extends TiledPlugin>>> plugins;
	private static PluginManager instance;

	/**    */
	private PluginManager() {
		super();
	}

	public static PluginManager getInstance() {
		if (instance == null) {
			instance = new PluginManager();
			instance.readPlugins(null);
		}
		return instance;
	}

	/**
	 * @param base
	 *            base dir
	 * 
	 */
	public void readPlugins(String base) {
		plugins = new HashMap<Class<? extends TiledPlugin>, List<Class<? extends TiledPlugin>>>();

		// buildin plugins
		addPlugin(MapReaderPlugin.class, StendReader.class);
		addPlugin(MapReaderPlugin.class, XStendReader.class);
		addPlugin(MapReaderPlugin.class, MapReader.class);

		addPlugin(MapWriterPlugin.class, StendWriter.class);
		addPlugin(MapWriterPlugin.class, XStendWriter.class);
		addPlugin(MapWriterPlugin.class, MapWriter.class);
	}

	/**
	 * @param interfaceClass
	 *            the plugin interface
	 * @param pluginClass
	 */
	private void addPlugin(Class<? extends TiledPlugin> interfaceClass, Class< ? extends TiledPlugin> pluginClass) {
		if (!interfaceClass.isAssignableFrom(pluginClass)) {
			System.out.println(pluginClass.getName() + " is not an instance of " + interfaceClass.getName());
		}
		if (!plugins.containsKey(interfaceClass)) {
			plugins.put(interfaceClass, new ArrayList<Class<? extends TiledPlugin>>());
		}

		List<Class<? extends TiledPlugin>> list = plugins.get(interfaceClass);
		if (!list.contains(pluginClass)) {
			list.add(pluginClass);
		}
	}

	/**
	 * @param pluginInterface
	 *            the plugin interface
	 * @return the list of known plugins of this type
	 */
	public List<Class<? extends TiledPlugin>> getPlugins(Class<? extends TiledPlugin> pluginInterface) {
		return plugins.get(pluginInterface);
	}

}
