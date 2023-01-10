/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityoutside;

import java.util.Date;
import java.util.Map;
import java.util.Set;
//~ import java.util.TreeMap;
import java.util.TreeSet;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
//~ import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;


public class FletcherNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC fletcher = new SpeakerNPC("Fletcher");
		fletcher.setEntityClass("rangernpc");

		fletcher.addGreeting();
		fletcher.addGoodbye();
		fletcher.addOffer("I can #soak arrows in poison.");

		fletcher.setPosition(20, 99);

		/*
		final Map<String, Integer> required = new TreeMap<String, Integer>() {{
			put("wooden arrow", 5);
			put("poison", 1);
			put("money", 500);
		}};
		new ProducerAdder().addProducer(fletcher,
				new FletcherProducerBehaviour(required),
				"What can I #offer you today?");
		*/

		return fletcher;
	}

	// FIXME: math is wrong somewhere, want to create 5 poison arrows with 5 woodon arrows & 1 poison
	private class FletcherProducerBehaviour extends ProducerBehaviour {

		// FIXME: activity & product name interfere if they match (e.g. "poison" -> "poison arrow")
		private FletcherProducerBehaviour(final Map<String, Integer> required) {
			super("fletcher_soak_arrows", "soak", "poison arrow", required,
				20 * 60);
		}

		@Override
		public int getProductionTime(final int amount) {
			return super.getProductionTime(amount) / 5;
		}

		@Override
		protected String getRequiredResourceNamesWithHashes(final int amount) {
			// use sorted TreeSet instead of HashSet
			final Set<String> requiredResourcesWithHashes = new TreeSet<String>();
			for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
				requiredResourcesWithHashes.add(Grammar.quantityplnounWithHash(amount / 5
						* entry.getValue(), entry.getKey()));
			}
			return Grammar.enumerateCollection(requiredResourcesWithHashes);
		}

		@Override
		public boolean askForResources(final ItemParserResult res, final EventRaiser npc, final Player player) {
			if (res.getAmount() % 5 != 0) {
				npc.say("I can only produce poison arrows in quantities of 5.");
				return false;
			}
			return super.askForResources(res, npc, player);
		}

		@Override
		public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser npc, final Player player) {
			final int amount = res.getAmount();
			final String product = getProductName();
			if (getMaximalAmount(player) < amount) {
				// The player tried to cheat us by placing the resource
				// onto the ground after saying "yes"
				npc.say("Hey! I'm over here! You'd better not be trying to trick me...");
				return false;
			} else {
				for (final Map.Entry<String, Integer> entry: getRequiredResourcesPerItem().entrySet()) {
					player.drop(entry.getKey(), amount / 5 * entry.getValue());
				}
				final long timeNow = new Date().getTime();
				player.setQuest(getQuestSlot(), amount + ";" + product + ";" + timeNow);
				npc.say("OK, I will "
						+ getProductionActivity()
						+ " "
						+ Grammar.quantityplnoun(amount, product, "a")
						+ " for you, but that will take some time. Please come back in "
						+ getApproximateRemainingTime(player) + ".");
				return true;
			}
		}
	}
}
