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
package games.stendhal.server.maps.fado.city;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.PassiveNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Rabbits
 * 
 * @author AntumDeluge
 */
public class RabbitNPC implements ZoneConfigurator {
	
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
	    
	    // All rabbits
	    List<PassiveNPC> rabbits = new LinkedList<PassiveNPC>();
	    
		final PassiveNPC r1 = new PassiveNPC();
        r1.setPosition(50, 29);
        rabbits.add(r1);
		
		final PassiveNPC r2 = new PassiveNPC();
		r2.setPosition(120, 97);
		rabbits.add(r2);
		
		// Add rabbits to zone
		for (PassiveNPC mammal : rabbits) {
	        mammal.setDescription("You see a rabbit.");
	        mammal.setEntityClass("animal/rabbit");
	        mammal.setBaseSpeed(0.2);
	        mammal.moveRandomly();
	        mammal.setTitle("rabbit");
	        mammal.setFinishedPathPause(20);
	        zone.add(mammal);
		}
	}

}