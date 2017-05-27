/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.city;

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.area.Allotment;

/**
 * Configures ground to be fertile in zone. usage in zones.xml.
 * <p>
 * example to create an area at (15,25) with widht of 10 and height of 20
 * <p>
 * &lt;configurator
 * class-name="games.stendhal.server.maps.semos.city.FertileGrounds" &gt;
 * <p>
 * &lt;parameter name="x"&gt;15&lt;/parameter&gt;
 * <p>
 * &lt;parameter name="y"&gt;25&lt;/parameter&gt;
 * <p>
 * &lt;parameter name="width"&gt;10&lt;/parameter&gt; &lt;
 * <p>
 * &lt;parameter name="height"&gt;20&lt;/parameter&gt;
 * <p>
 * &lt;parameter name="name"&gt;allotmentName&lt;/parameter&gt;
 * <p>
 *&lt;/configurator&gt;
 *
 */
public class FertileGrounds implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		if (zone != null) {
			if (isValid(attributes)) {

				try {
					final Allotment all = new Allotment();
					all.setPosition(Integer.parseInt(attributes.get("x")), Integer.parseInt(attributes.get("y")));
					all.setSize(Integer.parseInt(attributes.get("width")), Integer.parseInt(attributes.get("height")));

					if (attributes.containsKey("name")) {
						all.setName(attributes.get("name"));
					}

					all.hide();
					zone.add(all);
				} catch (final NumberFormatException e) {
					Logger.getLogger(FertileGrounds.class).error(
							"cannot create allotment in " + zone.getName() + ": " + e);
				}
			}
		}
	}

	private boolean isValid(final Map<String, String> attributes) {
		return attributes.containsKey("x") && attributes.containsKey("y") && attributes.containsKey("width")
				&& attributes.containsKey("height");
	}

}
