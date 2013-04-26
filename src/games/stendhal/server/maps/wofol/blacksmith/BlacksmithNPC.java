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
package games.stendhal.server.maps.wofol.blacksmith;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ProducerBehaviourAction;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
/**
 * Configure Wofol Blacksmith (-1_semos_mine_nw).
 *
 * @author kymara
 */
public class BlacksmithNPC implements ZoneConfigurator {

	private static Logger logger = Logger.getLogger(BlacksmithNPC.class);

	private static final String QUEST_SLOT = "alrak_make_bobbin";

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
		buildBlacksmith(zone);
	}

	private void buildBlacksmith(final StendhalRPZone zone) {
		final SpeakerNPC dwarf = new SpeakerNPC("Alrak") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 8));
				nodes.add(new Node(22, 7));
				nodes.add(new Node(17, 7));
				nodes.add(new Node(17, 2));
				nodes.add(new Node(8, 2));
				nodes.add(new Node(8, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("How did you get down here? I usually only see #kobolds.");
				addJob("I am a blacksmith. I was a mountain dwarf but I left that lot behind me. Good riddance, I say!");
				addHelp("I've heard rumours of a fearsome creature living below these mines, and his small minions, evil imps. I wouldn't go down there even to look, if I were you. It's very dangerous.");
				addOffer("#Wrvil is the one who runs a shop, not me. I still #make the occasional #bobbin if you need one, though.");
				addReply("kobolds", "You know, those odd furry creatures. Don't get much conversation out of any except #Wrvil.");
				addReply("Wrvil", "He runs a trading business not far from here. I used to make the odd item for him, but don't have any energy left.");
				addGoodbye();

				addReply("bobbin", "Ask me to #make you a bobbin if you have some iron with you, and some cash. I'm a bit forgetful so when you return please say 'remind' to prompt me.");

				/* @author kymara */

				// bobbin from iron
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("iron", Integer.valueOf(1));
				requiredResources.put("money", Integer.valueOf(100));

				// make sure alrak tells player to remind him to get bobbin back by overriding transactAgreedDeal
				// override giveProduct so that he doesn't say 'welcome back', which is a greeting,
				// in the middle of an active conversation.
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
							player.setQuest(QUEST_SLOT, amount + ";" + getProductName() + ";" + timeNow);
							npc.say("OK, I will "
									+ getProductionActivity()
									+ " "
									+ amount
									+ " "
									+ getProductName()
									+ " for you, but that will take some time. Please come back in "
									+ getApproximateRemainingTime(player) + ". "
									+ "And, this is IMPORTANT - I am very forgetful so you MUST #remind me to give you your bobbin when you return!");
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
									+ getApproximateRemainingTime(player) + " to get it. Don't forget to #remind me again ... ");
						} else {
							final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(getProductName());

							products.setQuantity(numberOfProductItems);

							if (isProductBound()) {
								products.setBoundTo(player.getName());
							}

							player.equipOrPutOnGround(products);
							npc.say("I'm done! Here you have "
									+ Grammar.quantityplnoun(numberOfProductItems,
															 getProductName(), "the") + ".");
							player.setQuest(QUEST_SLOT, "done");
							// give some XP as a little bonus for industrious workers
							player.addXP(numberOfProductItems);
							player.notifyWorldAboutChanges();
						}
					}
				}
				
				final ProducerBehaviour behaviour = new SpecialProducerBehaviour("make", "bobbin",
				        requiredResources, 10 * 60);

				// we are not using producer adder at all here because that uses Conversations states IDLE and saying 'hi' heavily.
				// we can't do that here because Pequod uses that all the time in his fishing quest. so player is going to have to #remind
				// him if he wants his oil back!

				add(
				ConversationStates.ATTENDING,
				"make",
				new QuestNotActiveCondition(behaviour.getQuestSlot()),
				ConversationStates.ATTENDING, null,
				new ProducerBehaviourAction(behaviour) {
					@Override
					public void fireRequestOK(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser npc) {
						// Find out how much items we shall produce.
						if (res.getAmount() > 1000) {
							logger.warn("Decreasing very large amount of "
									+ res.getAmount()
									+ " " + res.getChosenItemName()
									+ " to 1 for player "
									+ player.getName() + " talking to "
									+ npc.getName() + " saying " + sentence);
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
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						behaviour.transactAgreedDeal(currentBehavRes, npc, player);

						currentBehavRes = null;
					}
				});

				add(ConversationStates.PRODUCTION_OFFERED,
						ConversationPhrases.NO_MESSAGES, null,
						ConversationStates.ATTENDING, "OK, no problem.", null);

				add(ConversationStates.ATTENDING,
						Arrays.asList(behaviour.getProductionActivity(), "remind"),
						new QuestActiveCondition(behaviour.getQuestSlot()),
						ConversationStates.ATTENDING, null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						behaviour.giveProduct(npc, player);
					}
				});
			
			}


		//remaining behaviour defined in maps.quests.ObsidianKnife
		};

		dwarf.setDescription("You see Alrak, a reclusive dwarf smith.");
		dwarf.setEntityClass("dwarfnpc");
		dwarf.setPosition(22, 8);
		dwarf.initHP(100);
		zone.add(dwarf);
	}
}
