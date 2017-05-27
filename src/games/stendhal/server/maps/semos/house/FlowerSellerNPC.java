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
package games.stendhal.server.maps.semos.house;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;

/**
 * Builds a Flower Seller NPC for the Elf Princess quest.
 *
 * @author kymara
 */
public class FlowerSellerNPC implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {

        	new TeleporterBehaviour(buildSemosHouseArea(), null, "0", "Flowers! Get your fresh flowers here!");
	}

	private SpeakerNPC buildSemosHouseArea() {

	    final SpeakerNPC rose = new SpeakerNPC("Rose Leigh") {
	                @Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}
	                @Override
			protected void createDialog() {
			    addJob("I'm a wandering flower woman.");
			    addGoodbye("Everything's coming up roses ... bye ...");
			    // the rest is in the ElfPrincess quest
			}
		};

		rose.setEntityClass("gypsywomannpc");
		rose.initHP(100);
		rose.setCollisionAction(CollisionAction.REVERSE);
		rose.setDescription("You see Rose Leigh. She jumps from place to place with a basket filled with lovely rhosyds.");

		// start in int_semos_house
		final StendhalRPZone	zone = SingletonRepository.getRPWorld().getZone("int_semos_house");
		rose.setPosition(5, 6);
		zone.add(rose);

		return rose;
	}
}
