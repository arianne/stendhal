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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ProducerBehaviourAction;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
 * Builds a shady Spirit Trapper NPC for the Empty Bottle quest.
 *
 * @author soniccuz based on FlowerSellerNPC by kymara and fishermanNPC by dine.
 */
public class SpiritTrapperNPC implements ZoneConfigurator {
	
	private static final String QUEST_SLOT = "trade_arrows";
	
	/**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;
	
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {

			final List<String> setZones = new ArrayList<String>();
			setZones.add("0_ados_swamp");
			setZones.add("0_ados_outside_w");
			setZones.add("0_ados_wall_n2");
			setZones.add("0_ados_wall_s");
			setZones.add("0_ados_city_s");
        	new TeleporterBehaviour(buildSemosHouseArea(), setZones, "0_ados", "..., ....");
	}

	private SpeakerNPC buildSemosHouseArea() {

	    final SpeakerNPC mizuno = new SpeakerNPC("Mizuno") {
	                @Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}
	        @Override
			protected void createDialog() {
	        	addGreeting("What do you want?");
			    addJob("I prefer to keep that to myself.");
			    addOffer("Look I must move on soon but, quickly, if you have any #black #pearls I will trade some of my magic #arrows for them. Care to #buy some #arrows?");
			    addGoodbye("Then get going... I can't get any work done with you mucking around.");
			    
			    addReply("arrows","I enchant arrows with elemental power. I have #ice, #fire, and #light.");
			    addReply("buy fire","Maybe later when I don't have need of them.");
			    addReply("buy light","Maybe later when I don't have need of them.");
			    // the rest is in the MessageInABottle quest
			    
			 // Mizuno exchanges elemental arrows for black pearls.
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("black pearl", Integer.valueOf(1));
				 
				// Mizuno is more of a "trader" then a producer. There shouldn't be
				// any production time in his transactions but he doesn't deal with money.
				class SpecialProducerBehaviour extends ProducerBehaviour { 
					SpecialProducerBehaviour(final String productionActivity,
							final String productName, final Map<String, Integer> requiredResourcesPerItem,
							final int productionTimePerItem) {
						super(QUEST_SLOT, productionActivity, productName,
								requiredResourcesPerItem, productionTimePerItem, false);
					}

					/**
					 * Tries to take all the resources required to produce the agreed amount of
					 * the product from the player. If this is possible, initiates an order.
					 * 
					 * @param res
					 *
					 * @param npc
					 *            the involved NPC
					 * @param player
					 *            the involved player
					 */
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
							return true;
						}
					}
					
					@Override
					public boolean askForResources(final ItemParserResult res, final EventRaiser npc, final Player player) {
						int amount = res.getAmount();

						if (getMaximalAmount(player) < amount) {
							npc.say(Grammar.quantityplnoun(amount, getProductName(), "A") 
									+ " costs "
									+ getRequiredResourceNamesWithHashes(amount) + ". "
									+ " You don't have enough.");
							return false;
						} else {
							res.setAmount(amount);
							npc.say(Grammar.quantityplnoun(amount, getProductName(), "A") 
									+ " for "
									+ getRequiredResourceNamesWithHashes(amount) + ". "
									+ " Correct?");
							return true;
						}
					}
					

					/**
					 * This method is called when the player returns to pick up the finished
					 * product. It checks if the NPC is already done with the order. If that is
					 * the case, the player is given the product. Otherwise, the NPC asks the
					 * player to come back later.
					 * 
					 * @param npc
					 *            The producing NPC
					 * @param player
					 *            The player who wants to fetch the product
					 */
					@Override
					public void giveProduct(final EventRaiser npc, final Player player) {
						final String orderString = player.getQuest(QUEST_SLOT);
						final String[] order = orderString.split(";");
						final int numberOfProductItems = Integer.parseInt(order[0]);
						if (!isOrderReady(player)) {
							npc.say("I'm still working on your request to "
									+ getProductionActivity() + " " + getProductName()
									+ " for you. Please return in "
									+ getApproximateRemainingTime(player) + " to get it.");
						} else {
							final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(
									getProductName());
							

							products.setQuantity(numberOfProductItems);

							if (isProductBound()) {
								products.setBoundTo(player.getName());
							}

							player.equipOrPutOnGround(products);
							npc.say("Here " + Grammar.isare(numberOfProductItems) 
									+ " your " + Grammar.quantityplnoun(numberOfProductItems,
											getProductName(), "") + ". Now get going I can't get anyting done with you mucking about.");
							player.setQuest(QUEST_SLOT, "done");
							npc.setCurrentState(ConversationStates.IDLE);
							player.notifyWorldAboutChanges();
						}
					}
				}

				final ProducerBehaviour behaviour = new SpecialProducerBehaviour("buy", "ice arrow",
						requiredResources, 0);

				add(
						ConversationStates.ATTENDING,
						"buy",
						new QuestNotActiveCondition(behaviour.getQuestSlot()),
						ConversationStates.ATTENDING, null,
						new ProducerBehaviourAction(behaviour, "produce") {
							@Override
							public void fireRequestOK(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser npc) {
								// Find out how much items we shall produce.
								if (res.getAmount() > 1000) {
									res.setAmount(1);
								}

								if (behaviour.askForResources(res, npc, player)) {
									currentBehavRes = res;
									npc.setCurrentState(ConversationStates.PRODUCTION_OFFERED);
								}
							}
						});

				add(ConversationStates.PRODUCTION_OFFERED,
						ConversationPhrases.YES_MESSAGES, null,
						ConversationStates.ATTENDING, null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (behaviour.transactAgreedDeal(currentBehavRes, npc, player)) {
							behaviour.giveProduct(npc, player);
						}

						currentBehavRes = null;
					}
				});

				add(ConversationStates.PRODUCTION_OFFERED,
						ConversationPhrases.NO_MESSAGES, null,
						ConversationStates.ATTENDING, "OK, no problem.", null);
			}
		};

		mizuno.setEntityClass("man_001_npc");
		mizuno.initHP(100);
		mizuno.setHP(80);
		mizuno.setCollisionAction(CollisionAction.REVERSE);
		mizuno.setDescription("You see Mizuno. Like a spirit he wanders the vacant haunts of Ados doing who know what.");

		// start in int_semos_house
		final StendhalRPZone	zone = SingletonRepository.getRPWorld().getZone("int_semos_house");
		mizuno.setPosition(5, 6);
		zone.add(mizuno);

		return mizuno;
	}
}
