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

import java.util.Map;

/**
 * A rabbit
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
		final PassiveNPC rabbit = new PassiveNPC() {
		};
		
		rabbit.setPosition(50, 29);
		rabbit.setDescription("You see a rabbit.");
		rabbit.setEntityClass("animal/rabbit");
		rabbit.setBaseSpeed(0.2);
		rabbit.moveRandomly();
		rabbit.setTitle("RabbitNPC");
		zone.add(rabbit);
	}

}