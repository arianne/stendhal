/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.settings;

import java.util.HashMap;
import java.util.Map;

public class ConvenienceMapper {

	final static Map<String, String[]> desc2data = new HashMap<String, String[]>();
	static Map<String, String[]> key2data = new HashMap<String, String[]>();

	/**
	 * Convenience mapping for getting the data rows from either short or long names.
	 *
	 * @param component
	 * @param data
	 */
	public static String createTooltip(final String descr, final String[][] data) {
		final StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html>" + descr + "<dl>");

		if (data != null) {
			for (String[] optionData : data) {
				tooltip.append("<dt><b>");
				tooltip.append(optionData[0]);
				if (optionData[1] != null) {
					tooltip.append(" <i>(" + optionData[1] + ")</i>");
				}
				tooltip.append("</b></dt>");
				tooltip.append("<dd>");
				tooltip.append(optionData[2]);
				tooltip.append("</dd>");
			}
		}

		tooltip.append("</dl></html>");

		return tooltip.toString();
	}
}
