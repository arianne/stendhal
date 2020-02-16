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
package games.stendhal.server.maps.fado.dressingrooms;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;

/**
 * Dressing rooms at fado hotel.
 *
 * @author kymara
 */
public class BrideAssistantNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDressingRoom(zone);
	}

	private void buildDressingRoom(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Tamara") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				// greetings in Marriage quest
				addJob("I assist brides with getting dressed for their wedding.");
				addHelp("Just tell me if you want to #'wear a gown' for your wedding.");
				addQuest("You don't want to be thinking about that kind of thing ahead of your big day!");
				addReply("gown", "Every bride needs a beautiful wedding dress! It's a charge of 100 money if you want to #wear a #gown.");
				addGoodbye("Have a lovely time!");

				final Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("gown", 100);
				final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList, true);
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "wear");
			}
		};

		npc.setEntityClass("woman_003_npc");
		npc.setDirection(Direction.RIGHT);
		npc.setPosition(3, 10);
		npc.initHP(100);
		npc.setDescription("You see Tamara. She waits for new brides-to-be and is curious, who will tie the knot next.");
		zone.add(npc);
	}
}
