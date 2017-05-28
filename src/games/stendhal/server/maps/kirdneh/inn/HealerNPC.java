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
package games.stendhal.server.maps.kirdneh.inn;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ListProducedItemDetailAction;
import games.stendhal.server.entity.npc.action.ListProducedItemsOfClassAction;
import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.condition.TriggerIsProducedItemOfClassCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

/**
 * Builds a Healer NPC for kirdneh.
 * She likes a drink
 *
 * @author kymara
 */
public class HealerNPC implements ZoneConfigurator {

	/**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Katerina") {

			@Override
			protected void createPath() {
			    // sits still on stool
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Gis' a kiss!");
				addReply("drinks", null, new ListProducedItemsOfClassAction("drink","I like [#items]. *hic*"));
				add(
					ConversationStates.ATTENDING,
					"",
					new TriggerIsProducedItemOfClassCondition("drink"),
					ConversationStates.ATTENDING,
					null,
					new ListProducedItemDetailAction()
				);
				addReply("kiss", "ew sloppy");
				addReply(":*", "*:");
				addJob("Wuh? Uhh. Heal. Yeah. tha's it.");
				addHealer(this, 250);
				addHelp("Gimme money for #drinks. I heal, gis' cash.");
				addQuest("Bah.");
 				addGoodbye("pffff bye");
			}
		};

		npc.setDescription("You see a woman who was perhaps once beautiful but now a little the worse for wear...");
		npc.setEntityClass("womanonstoolnpc");
		npc.setPosition(25, 9);
		npc.setDirection(Direction.UP);
		npc.initHP(100);
		zone.add(npc);
	}

    // Don't want to use standard responses for Heal, in fact what to modify them all, so just configure it all here.
    private void addHealer(final SpeakerNPC npc, final int cost) {
	    final HealerBehaviour healerBehaviour = new HealerBehaviour(cost);
		final Engine engine = npc.getEngine();

		engine.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				null,
				false,
				ConversationStates.ATTENDING,
				"Gimme money for beer. I heal, gis' cash.", null);

		engine.add(ConversationStates.ATTENDING,
				"heal",
				null,
				false,
				ConversationStates.HEAL_OFFERED,
		        null, new ChatAction() {
			        @Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
			        	currentBehavRes = new ItemParserResult(true, "heal", 1, null);
                        String badboymsg = "";
			        	int cost = healerBehaviour.getCharge(currentBehavRes, player);
			        	if (player.isBadBoy()) {
			        		cost = cost * 2;
			        		badboymsg = " Its more for nasty ones.";
			        		currentBehavRes.setAmount(2);
			        	}

						if (cost != 0) {
	                    	raiser.say("For " + cost + " cash, ok?" + badboymsg);
	                    }
			        }
		        });

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
		        false,
		        ConversationStates.IDLE,
		        null, new ChatAction() {
			        @Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				        if (player.drop("money", healerBehaviour.getCharge(currentBehavRes, player))) {
					        healerBehaviour.heal(player);
					        raiser.say("All better now, everyone better. I love you, I do. Bye bye.");
				        } else {
					        raiser.say("Pff, no money, no heal. Bye.");
				        }

						currentBehavRes = null;
			        }
		        });

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
		        false,
		        ConversationStates.IDLE,
		        "Bye then,", null);
	}

}
