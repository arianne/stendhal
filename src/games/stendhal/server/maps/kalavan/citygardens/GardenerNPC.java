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
package games.stendhal.server.maps.kalavan.citygardens;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Builds the gardener in Kalavan city gardens.
 *
 * @author kymara
 */
public class GardenerNPC implements ZoneConfigurator {

	private static final String QUEST_SLOT = "sue_swap_kalavan_city_scroll";
    private static final Integer MAX_LUNCHES = 7;

	//
	// ZoneConfigurator
	//

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
		final SpeakerNPC npc = new SpeakerNPC("Sue") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(100, 123));
				nodes.add(new Node(110, 123));
				nodes.add(new Node(110, 110));
				nodes.add(new Node(119, 110));
				nodes.add(new Node(119, 122));
				nodes.add(new Node(127, 122));
				nodes.add(new Node(127, 111));
				nodes.add(new Node(118, 111));
				nodes.add(new Node(118, 123));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class SpecialProducerBehaviour extends ProducerBehaviour { 
					SpecialProducerBehaviour(final String productionActivity,
                        final String productName, final Map<String, Integer> requiredResourcesPerItem,
											 final int productionTimePerItem) {
						super(QUEST_SLOT, productionActivity, productName,
							  requiredResourcesPerItem, productionTimePerItem, false);
					}

					@Override
						public boolean askForResources(ItemParserResult res, final EventRaiser npc, final Player player) {
						int amount = res.getAmount();

						if (player.hasQuest(QUEST_SLOT) && player.getQuest(QUEST_SLOT).startsWith("done;")) {
							// she is eating. number of lunches is in tokens[1]
							final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
							// delay is number of lunches * one day - eats one lunch per day
							final long delay = (Long.parseLong(tokens[1])) * MathHelper.MILLISECONDS_IN_ONE_DAY;
							final long timeRemaining = (Long.parseLong(tokens[2]) + delay)
								- System.currentTimeMillis();
							if (timeRemaining > 0) {
								npc.say("I'm still eating the lunch you brought me last time. It's enough to last me for another "
                                        + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000))
                                        + "!");
                                return false;
							}
					    } 
						if (amount > MAX_LUNCHES) {
							npc.say("I can't take more than a week's worth of sandwiches at once! They'll go stale!");
							return false;
						} else if (getMaximalAmount(player) < amount) {
							npc.say("I would only " + getProductionActivity() + " you "
									+ Grammar.quantityplnoun(amount, getProductName(), "a")
									+ " if you bring me "
									+ getRequiredResourceNamesWithHashes(amount) + ".");
							return false;
						} else {
							res.setAmount(amount);
							npc.say("Then I'll want "
									+ getRequiredResourceNamesWithHashes(amount)
									+ ". Did you bring that?");
							return true;
						}
					}

					@Override
					public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser npc, final Player player) {
						int amount = res.getAmount();

						if (getMaximalAmount(player) < amount) {
							// The player tried to cheat us by placing the resource
							// onto the ground after saying "yes"
							npc.say("Hey! I'm over here! You'd better not be trying to trick me...");
							return false;
						} else {
							for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
                                final int amountToDrop = amount * entry.getValue();
                                player.drop(entry.getKey(), amountToDrop);
							}
							final long timeNow = new Date().getTime();
							player.setQuest(QUEST_SLOT, amount + ";" + getProductName() + ";"
											+ timeNow);
							npc.say("Thanks! Come back in "
									+ getApproximateRemainingTime(player) + ", and I'll have got " 
									+ Grammar.quantityplnoun(amount, getProductName(), "a") + " for you.");
							return true;
						}
					}

					@Override
					public void giveProduct(final EventRaiser npc, final Player player) {
						final String orderString = player.getQuest(QUEST_SLOT);
						final String[] order = orderString.split(";");
						final int numberOfProductItems = Integer.parseInt(order[0]);
						// String productName = order[1];
						final long orderTime = Long.parseLong(order[2]);
						final long timeNow = new Date().getTime();
						if (timeNow - orderTime < getProductionTime(numberOfProductItems) * 1000) {
							npc.say("Hello again! Oops, I still don't have your scrolls! Come back in "
									+ getApproximateRemainingTime(player) + " to get them.");
						} else {
                        final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(
                                        getProductName());

                        products.setQuantity(numberOfProductItems);

                        if (isProductBound()) {
							products.setBoundTo(player.getName());
                        }

                        player.equipOrPutOnGround(products);
                        npc.say("Welcome back! I've put my lunch inside ready to eat later. In exchange here you have "
								+ Grammar.quantityplnoun(numberOfProductItems,
                                                        getProductName(), "a") + ".");
                        // store the number of lunches given and the time so we know how long she eats for
						player.setQuest(QUEST_SLOT, "done" + ";" + numberOfProductItems + ";"
										+ System.currentTimeMillis());
                        // give some XP as a little bonus for industrious workers
                        player.addXP(15 * numberOfProductItems);
                        player.notifyWorldAboutChanges();
						}
					}
				}
				addReply(ConversationPhrases.YES_MESSAGES, "Very warm...");
				addReply(ConversationPhrases.NO_MESSAGES, "It's better than rain!");
				addJob("I am the gardener. I hope you like the flowerbeds.");
				addHelp("If you bring me some #lunch I'll #swap you for a magic scroll.");
				addOffer("My tomatoes and garlic are doing well, I have enough that I am selling some.");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
                offerings.put("tomato", 30);
                offerings.put("garlic", 50);
                new SellerAdder().addSeller(this, new SellerBehaviour(offerings), false);
				addReply("lunch", "Tea and a sandwich, please!");
				addReply("sandwich", "Mmm.. I'd like a ham and cheese one.");
				addReply(Arrays.asList("kalavan city scroll", "scroll"), "It's a magic scroll that would take you back to Kalavan. Just don't ask me how it works!");
				
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();	
				requiredResources.put("tea", 1);
				requiredResources.put("sandwich", 1);

				final ProducerBehaviour behaviour = new SpecialProducerBehaviour("swap", "kalavan city scroll", requiredResources, 1 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Fine day, isn't it?");
				addQuest("I'd love a cup of #tea, it's thirsty work, gardening. If you bring me a #sandwich too I'll #swap you for a magic scroll.");
				addReply(Arrays.asList("tea", "cup of tea"), "Old Granny Graham may brew you a cup. She's in that big cottage over there.");
				addGoodbye("Bye. Enjoy the rest of the gardens.");
			}
		};

		npc.setEntityClass("gardenernpc");
		npc.setPosition(100, 123);
		npc.initHP(100);
		npc.setDescription("You see Sue. Her flowers smell fantastic. She really has green fingers.");
		zone.add(npc);
	}

}
