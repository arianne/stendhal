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
package games.stendhal.server.maps.ados.rock;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.QuestCompletedBuyerBehaviour;

public class WeaponsCollectorNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildRockArea(zone);
	}

	private void buildRockArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Balduin") {

			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				// This greeting is mostly not used as the quests override it
				addGreeting("Greetings, friend.");
				addHelp("There is a swamp east of this mountain where you might get some rare weapons.");
				addJob("I'm much too old for hard work. I'm just living here as a hermit.");
				addGoodbye("It was nice to meet you.");
				// will buy black items once the Ultimate Collector quest is completed
				new BuyerAdder().addBuyer(this, new QuestCompletedBuyerBehaviour("ultimate_collector", "I'll buy black items from you when you have completed each #challenge I set you.", shops.get("buyblack")), false);
			}
			/* remaining behaviour is defined in:
			 * maps.quests.WeaponsCollector,
			 * maps.quests.WeaponsCollector2 and
			 * maps.quests.UltimateCollector. */
		};

		npc.setEntityClass("oldwizardnpc");
		npc.setPosition(16, 8);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setDescription("You see Balduin. He lives on these mountains as a hermit, but maybe he has something for you to do.");
		zone.add(npc);
	}
}
