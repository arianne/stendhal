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
package games.stendhal.server.maps.semos.wizardstower;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Cassandra, the ice sorceress of the Wizards Tower
 *
 * see games.stendhal.server.maps.quests.SorceressCassandraPlainQuest
 */
public class BlueIceSorceressNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildCassandra(zone);
	}

	private void buildCassandra(final StendhalRPZone zone) {
		final SpeakerNPC cassandra = new SpeakerNPC("Cassandra") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(37, 3));
				nodes.add(new Node(41, 3));
				nodes.add(new Node(33, 3));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(32, 9));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(33, 12));
				nodes.add(new Node(31, 12));
				nodes.add(new Node(31, 13));
				nodes.add(new Node(31, 11));
				nodes.add(new Node(33, 11));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(32, 9));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(33, 5));
				nodes.add(new Node(40, 5));
				nodes.add(new Node(40, 9));
				nodes.add(new Node(39, 9));
				nodes.add(new Node(39, 12));
				nodes.add(new Node(36, 12));
				nodes.add(new Node(40, 12));
				nodes.add(new Node(40, 3));
				nodes.add(new Node(41, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello dear!");

				//addHelp("I can conjure an #ice #scroll for you. It can't help you in the battle. But it can protect you from natural heat on certain dangerous terrains. You will know when you need it.");
				addHelp("Excuse me, I am very busy establishing the sorceresses part at the wizards circle.");

				addJob("I am Cassandra, the sorceress of water and ice. I represent #Frostshade at the wizards circle.");

				//addOffer("I can conjure an #ice #scroll for you. It can't help you in the battle. But it can protect you from natural heat on certain dangerous terrains. You will know when you need it.");
				addOffer("Excuse me, I am very busy establishing the sorceresses part at the wizards circle.");

				addReply("Frostshade", "Frostshade is the school of the magic of water and ice. It lies deep in the northern glaciers.");
				addReply("blank scroll", "Zekiel is the storekeeper of the tower, I am sure he can help you.");
				addQuest("The magic in this world has just begun and I am very busy to establish the sorceresses of #Frostshade at the wizards circle. I will tell you in time, when I have a quest for you.");
				addGoodbye("Bye!");

/**				addReply(Arrays.asList("ice scroll", "ice scrolls"),
*				        "I need a #blank #scroll for that. If you bring me one, I will #enchant it for you.");
*				add(
*				        ConversationStates.ATTENDING,
*				        Arrays.asList("enchant"),
*				        new PlayerHasItemWithHimCondition("blank scroll"),
*				        ConversationStates.ATTENDING,
*				        "I enchanted your blank scroll to an ice scroll. May it cool off your feets on your travels.",
*				        new MultipleActions(
*				        		new DropItemAction("blank scroll", 1),
*				        		new EquipItemAction("ice scroll", 1, true),
*				        		new IncreaseXPAction(250)));
*				add(
*				        ConversationStates.ATTENDING,
*				        Arrays.asList("enchant"),
*				    new NotCondition(new PlayerHasItemWithHimCondition("blank scroll")),
*				        ConversationStates.ATTENDING,
*					"You don't have a #blank #scroll that I could enchant.", null);
*/

			} //remaining behaviour defined in maps.quests.SorceressCassandraPlainQuest
		};

		cassandra.setDescription("You see Cassandra, a beautiful woman and powerful sorceress.");
		cassandra.setEntityClass("bluesorceressnpc");
		cassandra.setPosition(37, 2);
		cassandra.initHP(100);
		zone.add(cassandra);
	}
}
