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
package games.stendhal.server.maps.semos.village;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

/**
 * Configure semos village rats not to be cowards. (For helping newbies kill them)
 */
public class RatsCreatures implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildVillage(zone);
	}

	private void buildVillage(final StendhalRPZone zone) {

		for(CreatureRespawnPoint p:zone.getRespawnPointList()) {
			if(p!=null) {
				if("rat".equals(p.getPrototypeCreature().getName())) {
					// it is a rat, we will remove ai profile of stupid coward
					final Creature creature = p.getPrototypeCreature();
					final Map<String, String> aiProfiles = new HashMap<String, String>(creature.getAIProfiles());
					aiProfiles.remove("stupid coward");
					creature.setAIProfiles(aiProfiles);
				}
			}
		}
	}
}
