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
package games.stendhal.server.maps.sedah.gatehouse;

import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.BehaviourAction;
import games.stendhal.server.entity.npc.behaviour.impl.Behaviour;
import games.stendhal.server.entity.player.Player;

/**
 * Builds a gatekeeper NPC Bribe him with at least 300 money to get the key for
 * the Sedah city walls. He stands in the doorway of the gatehouse till the
 * interior is made.
 *
 * @author kymara
 */
public class GateKeeperNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Revi Borak") {

			@Override
			protected void createPath() {
				// not moving.
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.isEquipped("sedah gate key")) {
							// toss a coin to see if he notices player still has
							// the gate key
							if (Rand.throwCoin() == 1) {
								player.drop("sedah gate key");
								raiser.say("You shouldn't still have that key! I'll take that right back.");
							} else {
								raiser.say("Hi, again.");
							}
						} else {
							raiser.say("What do you want?");
						}
					}
				});
				addReply("nothing", "Good.");
				addReply("key", "I'm open to bribery...");
				addJob("I am the gatekeeper for the imperial city of Sedah. I am not supposed to let anyone pass, but perhaps you can make me an #offer.");
				addHelp("You can't get into the imperial city of Sedah without a key.");
				addQuest("The only favour I need is cold hard cash.");
				addOffer("Only a #bribe could persuade me to hand over the key to that gate.");

				addReply("bribe", null,
					new BehaviourAction(new Behaviour("money"), "bribe", "offer") {
						@Override
						public void fireSentenceError(Player player, Sentence sentence, EventRaiser raiser) {
							raiser.say(sentence.getErrorString() + " Are you trying to trick me? Bribe me some number of coins!");
						}

						@Override
						public void fireRequestOK(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser raiser) {
				        	final int amount = res.getAmount();

				        	if (sentence.getExpressions().size() == 1) {
        						// player only said 'bribe'
        						raiser.say("A bribe of no money is no bribe! Bribe me with some amount!");
				        	} else {
				        		if (amount < 300) {
									// Less than 300 is not money for him
									raiser.say("You think that amount will persuade me?! That's more than my job is worth!");
								} else {
									if (player.isEquipped("money", amount)) {
										player.drop("money", amount);
										raiser.say("Ok, I got your money, here's the key.");
										final Item key = SingletonRepository.getEntityManager().getItem(
												"sedah gate key");
										player.equipOrPutOnGround(key);
									} else {
										// player bribed enough but doesn't have
										// the cash
										raiser.say("Criminal! You don't have " + amount + " money!");
									}
								}
							}
			        	}

						@Override
						public void fireRequestError(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser raiser) {
							if (res.getChosenItemName() == null) {
								fireRequestOK(res, player, sentence, raiser);
        			        } else {
        						// This bit is just in case the player says 'bribe X potatoes', not money
        						raiser.say("You can't bribe me with anything but money!");
							}
						}
				});

				addGoodbye("Bye. Don't say I didn't warn you!");
			}
		};

		npc.setDescription("You see a tough looking soldier. He looks open to bribery.");
		/*
		 * We don't seem to be using the recruiter images that lenocas made for
		 * the Fado Raid area so I'm going to put him to use here. If the raid
		 * part ever gets done, this image can change.
		 */
		npc.setEntityClass("recruiter2npc");
		npc.setPosition(120, 67);
		npc.initHP(100);
		zone.add(npc);
	}
}
