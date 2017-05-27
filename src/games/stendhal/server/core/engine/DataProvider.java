/***************************************************************************
 *                   (C) Copyright 2012 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import games.stendhal.common.IO;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.net.message.TransferContent;

/**
 * provides data to the client
 *
 * @author hendrik
 */
public class DataProvider {
	private static Logger logger = Logger.getLogger(DataProvider.class);
	private Map<String, List<TransferContent>> data;

	/**
	 * creates a new DataProvider.
	 */
	public DataProvider() {
		Properties prop = new Properties();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("provider/provider.txt");
		if (is == null) {
			return;
		}
		try {
			prop.load(is);
			is.close();
		} catch (IOException e) {
			logger.error(e, e);
		}

		if (prop.isEmpty()) {
			return;
		}

		data = new HashMap<String, List<TransferContent>>();
		for (Map.Entry<Object, Object> entry : prop.entrySet()) {
			File file = new File(entry.getValue().toString());
			TransferContent content = new TransferContent();
			content.name = file.getName();
			content.cacheable = true;
			content.data = IO.readFileContent(file.getAbsolutePath());
			data.put(entry.getKey().toString(), Arrays.asList(content));
		}
	}

	/**
	 * sends data to the client, if available
	 *
	 * @param player  player to send data to
	 * @param version version of the client
	 */
	public void provideData(Player player, String version) {
		if (data == null) {
			return;
		}

		List<TransferContent> contents = data.get(version);
		if (contents != null) {
			StendhalRPAction.transferContent(player, contents);
		}
	}

	/**
	 * gets the data
	 *
	 * @param version version
	 * @return content
	 */
	public List<TransferContent> getData(String version) {
		if (data == null) {
			return null;
		}
		return data.get(version);
	}
}
