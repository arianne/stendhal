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
package games.stendhal.server.maps.deniran.cityinterior.potionsshop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;

public class PotionsDealerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("foo");

		final List<Node> nodes = Arrays.asList(
				new Node(6, 5),
				new Node(9, 5)
		);

		final Map<String, Integer> pricesBuy = new HashMap<String, Integer>() {{
			put("mandragora", 1500);
			put("toadstool", 60);
			put("greater poison", 60);
			put("mega poison", 500);
			put("deadly poison", 2000);
			put("sedative", 200);
		}};

		new BuyerAdder().addBuyer(npc, new BuyerBehaviour(pricesBuy));

		npc.addGreeting("Welcome to Deniran's potion shop.");
		npc.addJob("I manage this potion shop. Ask me about my #prices.");
		npc.addHelp("If you would like to sell something, ask me about my #prices and I will tell you what I #offer.");
		npc.addQuest("I don't have anything for you to do. But I could tell you my #prices.");

		npc.add(ConversationStates.ANY,
				Arrays.asList("price", "prices"),
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final StringBuilder sb = new StringBuilder("I buy");
						int idx = 0;
						for (final String itemName: pricesBuy.keySet()) {
							if (idx == pricesBuy.size() - 1) {
								sb.append(" and");
							}
							sb.append(" " + Grammar.plural(itemName) + " for " + Integer.toString(pricesBuy.get(itemName)));
							if (idx < pricesBuy.size() - 1) {
								sb.append(",");
							}
							idx++;
						}
						sb.append(".");

						raiser.say(sb.toString());
					}
				});

		npc.add(ConversationStates.ANY,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new MultipleActions(
						new PlaySoundAction("kiss-female-01"),
						new SayTextAction("Please come again.")
				)
		);

		npc.setPosition(nodes.get(0).getX(), nodes.get(0).getY());
		npc.setPath(new FixedPath(nodes, true));
		npc.setOutfit(new Outfit("body=1,head=0,mouth=2,eyes=1,dress=46,mask=1,hair=3"));

		zone.add(npc);
	}
}
