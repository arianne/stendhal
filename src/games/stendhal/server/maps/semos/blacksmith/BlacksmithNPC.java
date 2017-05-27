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
package games.stendhal.server.maps.semos.blacksmith;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * The blacksmith (original name: Xoderos). Brother of the goldsmith in Ados.
 * He refuses to sell weapons, but he casts iron for the player, and he sells
 * tools.
 *
 * @author daniel
 *
 * @see games.stendhal.server.maps.quests.HungryJoshua
 */
public class BlacksmithNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Xoderos") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(23,12));
                nodes.add(new Node(29,12));
                nodes.add(new Node(29,9));
                nodes.add(new Node(17,9));
                nodes.add(new Node(17,5));
                nodes.add(new Node(16,5));
                nodes.add(new Node(16,3));
                nodes.add(new Node(28,3));
                nodes.add(new Node(28,5));
                nodes.add(new Node(23,5));
                nodes.add(new Node(23,9));
                nodes.add(new Node(28,9));
                nodes.add(new Node(28,13));
                nodes.add(new Node(21,12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addReply("wood",
						"I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.");

				addReply(Arrays.asList("ore", "iron", "iron ore"),
				"You can find iron ore up in the mountains west of Or'ril, near the dwarf mines. Be careful up there!");

				addReply("gold pan",
				"With this tool you are able to prospect for gold. Along Or'ril river, south of the castle, is a lake near a waterfall. I once found a #'gold nugget' there. Maybe you would be lucky too.");

				addReply("gold nugget",
				"My brother Joshua lives in Ados. He can cast gold nuggets to bars of pure gold.");

				addReply("bobbin", "I do #trade in tools but I don't have any bobbins, sorry. They are too fiddly for me to make. Try a dwarf.");
				addReply(Arrays.asList("oil", "can of oil"), "Oh, fishermen supply us with that.");

				addHelp("If you bring me #wood and #'iron ore', I can #cast the iron for you. Then you could sell it to the dwarves, to make yourself a little money.");
				addJob("I am a blacksmith. I #cast iron, and #trade tools.");
				addGoodbye();
				new SellerAdder().addSeller(this, new SellerBehaviour(SingletonRepository.getShopList().get("selltools")));

				// Xoderos casts iron if you bring him wood and iron ore.
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("wood", 1);
				requiredResources.put("iron ore", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour("xoderos_cast_iron",
						"cast", "iron", requiredResources, 5 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.");


			}};
			npc.setPosition(23, 12);
			npc.setEntityClass("blacksmithnpc");
			npc.setDescription("You see Xoderos, the strong Semos blacksmith.");
			zone.add(npc);
	}
}
