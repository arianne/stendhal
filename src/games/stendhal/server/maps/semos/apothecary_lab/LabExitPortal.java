/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.apothecary_lab;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.portal.Portal;


/*
 * NOTE: because the lab entrance portal is not configured in .xml, neither can
 *       this portal else there is a reference error during build.
 */
public class LabExitPortal implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		final Portal labExit = new Portal();
		labExit.setPosition(33, 13);
		labExit.setIdentifier("lab_exit");
		labExit.setDestination("int_apothecary_cabin", "lab_entrance");

		zone.add(labExit);
	}
}
