/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.mithrilcloak;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.ProducerBehaviourAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author kymara
*/

class MakingFabric {

	private static final int REQUIRED_MINUTES_THREAD = 10;
	private static final int REQUIRED_HOURS_MITHRIL_THREAD = 4;
	private static final int REQUIRED_HOURS_FABRIC = 2;

	private MithrilCloakQuestInfo mithrilcloak;
	
	private final NPCList npcs = SingletonRepository.getNPCList();

	/**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;

	public MakingFabric(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}

	private void makeThreadStep() {
    	final SpeakerNPC npc = npcs.get("Vincento Price");

		npc.addReply("silk", "Keep this quiet, ok? I'll spin silk thread from the silk glands of a giant spider. Just ask me to #make it.");
		npc.addReply("silk gland", "Like I said, they come from giant spiders.");
				
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("silk gland", Integer.valueOf(1));
		

		// we want to add something to the beginning of quest slot so override classes using it.

		class SpecialProducerBehaviour extends ProducerBehaviour { 
			SpecialProducerBehaviour(final String productionActivity,
									 final String productName, final Map<String, Integer> requiredResourcesPerItem,
									 final int productionTimePerItem) {
				super(mithrilcloak.getQuestSlot(), productionActivity, productName,
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
					player.setQuest(mithrilcloak.getQuestSlot(), "makingthread;" + amount + ";" + getProductName() + ";"
									+ timeNow);
					npc.say("It's unorthodox, but I will "
							+ getProductionActivity()
							+ " "
							+ amount
							+ " "
							+ getProductName()
							+ " for you. Please be discreet and come back in "
							+ TimeUtil.approxTimeUntil(REQUIRED_MINUTES_THREAD * amount * MathHelper.SECONDS_IN_ONE_MINUTE) + ".");
					return true;
				}
			}
			
			/**
			 * This method is called when the player returns to pick up the finished
			 * product. It checks if the NPC is already done with the order. If that is
			 * the case, the player is told to get the product from another NPC. 
			 * Otherwise, the NPC asks the player to come back later.
			 * 
			 * @param npc
			 *            The producing NPC
			 * @param player
			 *            The player who wants to fetch the product
			 */
			@Override
				public void giveProduct(final EventRaiser npc, final Player player) {
				final String orderString = player.getQuest(mithrilcloak.getQuestSlot());
				final String[] order = orderString.split(";");
				final int numberOfProductItems = Integer.parseInt(order[1]);
				// String productName = order[1];
				final long orderTime = Long.parseLong(order[3]);
				final long timeNow = new Date().getTime();
				final long timeRemaining = orderTime + ((long)REQUIRED_MINUTES_THREAD * numberOfProductItems * MathHelper.MILLISECONDS_IN_ONE_MINUTE) - timeNow;
				if (timeRemaining > 0L) {
					npc.say("Shhhh, I'm still working on your request to "
							+ getProductionActivity() + " " + getProductName()
							+ " for you. I'll be done in " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
				} else {
					npc.say("Oh, I gave your "
							+ Grammar.quantityplnoun(numberOfProductItems,
													 getProductName(), "") + " to my research student Boris Karlova. Go collect them from him.");
					player.notifyWorldAboutChanges();
				}
			}
		}
		
		final ProducerBehaviour behaviour = new SpecialProducerBehaviour("make", "silk thread",
																		 requiredResources, REQUIRED_MINUTES_THREAD * MathHelper.SECONDS_IN_ONE_MINUTE);

		npc.add(ConversationStates.ATTENDING,
				"make",
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_fabric"), ConversationStates.ATTENDING, null,
				new ProducerBehaviourAction(behaviour) {
					@Override
					public void fireRequestOK(final ItemParserResult res, Player player, Sentence sentence, EventRaiser npc) {
						// Find out how much items we shall produce.
						if (res.getAmount() < 40) {
							npc.say("Do you really want so few? I'm not wasting my time with that! Any decent sized pieces of fabric needs at least 40 spools of thread! You should at least #make #40.");
							return;
						} else if (res.getAmount() > 1000) {
							/*logger.warn("Decreasing very large amount of "
							 *		+ behaviour.getAmount()
							 *		+ " " + behaviour.getChosenItemName()
							 *		+ " to 40 for player "
							 *		+ player.getName() + " talking to "
							 *		+ npc.getName() + " saying " + sentence);
							 */
							res.setAmount(40);
						}

						if (behaviour.askForResources(res, npc, player)) {
							currentBehavRes = res;
							npc.setCurrentState(ConversationStates.PRODUCTION_OFFERED);
						}
					}

					@Override
					public void fireRequestError(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser raiser) {
						raiser.say(behaviour.getErrormessage(res, "#make", "produce"));
					}
				});
		
		npc.add(ConversationStates.PRODUCTION_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						behaviour.transactAgreedDeal(currentBehavRes, npc, player);

						currentBehavRes = null;
					}
				});

		npc.add(ConversationStates.PRODUCTION_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "OK, no problem.", null);

		npc.add(ConversationStates.ATTENDING,
				behaviour.getProductionActivity(),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingthread;"), 
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						npc.say("I still haven't finished your last order!");
					}
				});
		// player returns and says hi while sacs being made
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingthread;")),
			ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						behaviour.giveProduct(npc, player);
					}
				});
		// player returns and doesn't need fabric and sacs not being made
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new NotCondition(
							new OrCondition(
									 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_fabric"),
									 new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingthread;")
							)
					)),
			ConversationStates.IDLE, "Ha ha he he woo hoo!!!",
			null);


		// player returns and needs fabric
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_fabric")),
			ConversationStates.ATTENDING, "Ha ha he he woo hoo ... ha ... Sorry, I get carried away sometimes. What do you want?",
			null);


	}
	private void fetchThreadStep() {
		final SpeakerNPC npc = npcs.get("Boris Karlova");

		// player returns and says hi while sacs being made
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingthread;")),
			ConversationStates.IDLE, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
									 final EventRaiser npc) {
						final String orderString = player.getQuest(mithrilcloak.getQuestSlot());
						final String[] order = orderString.split(";");
						final int numberOfProductItems = Integer.parseInt(order[1]);
						final long orderTime = Long.parseLong(order[3]);
						final long timeNow = new Date().getTime();
						if (timeNow - orderTime < (long)REQUIRED_MINUTES_THREAD * numberOfProductItems * MathHelper.MILLISECONDS_IN_ONE_MINUTE) {
							npc.say("Haaaa heee woooo hoo!");
						} else {
							npc.say("The boss gave me these "  
									+ Grammar.quantityplnoun(numberOfProductItems, "silk thread", "") 
									+ ". Price gets his students to do his dirty work for him.");
							final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(
																														  "silk thread");
							
							player.addXP(100);
							products.setQuantity(numberOfProductItems);
							products.setBoundTo(player.getName());
							player.setQuest(mithrilcloak.getQuestSlot(), "got_thread");
							player.equipOrPutOnGround(products);
							player.notifyWorldAboutChanges();
						}
					}
				}
				);

		// player returns and doesn't need fabric and sacs not being made
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new NotCondition(new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingthread;"))),
			ConversationStates.IDLE, "Ha ha he he woo hoo!!!",
			null);

	}

	private void makeMithrilThreadStep() {
		final SpeakerNPC npc = npcs.get("Kampusch");
		
		npc.addReply("balloon", "Ah! They are dropped by the charming little baby angels who dwell in Kikareukin Islands. I want one for my daughter.");
		npc.addReply("silk thread", "That is from the silk glands of giant spiders. You need 40 spools of silk thread to make something as large as a cloak, say.");
		npc.addReply("silk", "That is from the silk glands of giant spiders.");
		npc.addReply("mithril nuggets", "You can find them for yourself.");
		npc.addReply("Whiggins", "Find the wizard Whiggins inside his house in the magic city.");
		npc.addReply("scientists", "I hear of experiments deep in Kalavan Castle. The scientists are a crazy bunch, but look for the lead researcher, Vincento Price, he may be sane enough to help you.");


		// player says yes they brought the items needed
		// we can't use the nice ChatActions here because we have to timestamp the quest slot
		npc.add(
			ConversationStates.ATTENDING,
			"fuse", 
			new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_thread"),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.isEquipped("silk thread", 40)
						&& player.isEquipped("mithril nugget", 7)
						&& player.isEquipped("balloon")) {
						player.drop("silk thread", 40);
					    player.drop("mithril nugget", 7);
						player.drop("balloon");
						final long timeNow = new Date().getTime();
						player.setQuest(mithrilcloak.getQuestSlot(), "fusingthread;" + timeNow);
						npc.say("I will fuse 40 mithril thread for you. Please come back in "
								+ TimeUtil.approxTimeUntil((int) (REQUIRED_HOURS_MITHRIL_THREAD * MathHelper.MILLISECONDS_IN_ONE_HOUR / 1000L)) 
								+ ".");
						player.notifyWorldAboutChanges();
					} else {
						npc.say("For 40 spools of mithril thread to make your cloak, I need 40 spools of #silk #thread, 7 #mithril #nuggets and a #balloon.");
					}
				}
			});

		// player returns while fabric is still being woven, or is ready
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "fusingthread;")),
				ConversationStates.ATTENDING, null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						final String orderString = player.getQuest(mithrilcloak.getQuestSlot());
						final String[] order = orderString.split(";");
						final long delay = REQUIRED_HOURS_MITHRIL_THREAD * MathHelper.MILLISECONDS_IN_ONE_HOUR;
						final long timeRemaining = (Long.parseLong(order[1]) + delay)
							- System.currentTimeMillis();
						if (timeRemaining > 0L) {
							npc.say("Welcome. I'm still working on your request to fuse mithril thread"
									+ " for you. Come back in "
									+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
						} else {
							final StackableItem products = (StackableItem) SingletonRepository.
										getEntityManager().getItem("mithril thread");
	
							products.setQuantity(40);
						
							products.setBoundTo(player.getName());
							player.equipOrPutOnGround(products);
							npc.say("Hello again. The magic is completed. Here you have your 40 spools of mithril thread. Now, you must go to #Whiggins to get the #fabric made.");
							player.setQuest(mithrilcloak.getQuestSlot(), "got_mithril_thread");
							// give some XP as a little bonus for industrious workers
							player.addXP(100);
							player.notifyWorldAboutChanges();	
					}
				  }
				}
		);

		// don't fuse thread unless state correct
		npc.add(
				ConversationStates.ATTENDING,
				"fuse",
				new NotCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_thread")), 
				ConversationStates.ATTENDING, "I can only create mithril thread when you have got some silk #thread. And remember, I will know if you really need the magic performed or not.", null);
		
		// player returns and hasn't got thread yet/got thread already and 
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NotCondition(
								 new OrCondition(
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_thread"),
												 new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "fusingthread;")
												 )
						)),
				ConversationStates.ATTENDING, "Greetings. What an interesting place this is.",
				null);

		// player needs thread fused
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_thread")),
				ConversationStates.ATTENDING, "Greetings, can I #offer you anything?",
				null);

	}
	private void makeMithrilFabricStep() {

		final SpeakerNPC npc = npcs.get("Whiggins");

		// player asks about fabric/quest
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("weave", "fabric", "magical", "mithril fabric", "ida", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_mithril_thread"),
				ConversationStates.QUEST_OFFERED,
				"I would love to weave you some fabric but I'm afraid my mind is full of other things. I have offended a fellow wizard. I was up all night writing him an apology letter, but I have no-one to deliver it to him. Unless ... that is ... would YOU deliver this letter for me?",
				null);
			
		// Player says yes they want to help 
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Wonderful! I'm so relieved! Please take this note to Pedinghaus, you will find him in Ados goldsmiths. Tell him you have a #letter for him.",			
				new MultipleActions(new EquipItemAction("sealed envelope", 1, true),
									new SetQuestAction(mithrilcloak.getQuestSlot(), "taking_letter"))
				);
		
		// player said no they didn't want to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.QUEST_OFFERED,
			"Oh dear, I'm ever so worried. Please help?",
			null);
	
		// player returns without having taking letter
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("weave", "fabric", "magical", "mithril fabric", "ida", "mithril", "cloak", "mithril cloak", "pedinghaus", "task", "quest", "letter", "note"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_letter"),
				ConversationStates.ATTENDING,
				"Please don't forget to take that letter to Pedinghaus. It means a lot to me.", null);

		// player returns having taking letter
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("weave", "fabric", "magical", "mithril fabric", "ida", "mithril", "cloak", 
							  "mithril cloak", "pedinghaus", "regards", "forgiven", "task", "quest"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "took_letter"),
				ConversationStates.SERVICE_OFFERED,
				"Thank you so much for taking that letter! Now, do you have the 40 spools of mithril thread "
				+ "so that I may weave you a couple yards of fabric?", null);

		// player's quest state is in nothing to do with the letter, thread or weaving.
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("weave", "fabric", "magical", "mithril fabric", "ida", "mithril", "cloak", "mithril cloak", "pedinghaus", "task", "quest"),
				new NotCondition(
								 new OrCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_mithril_thread"),
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_letter"),
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "took_letter"),
												 new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "weavingfabric;")
												 )
								 ),
				ConversationStates.ATTENDING,
				"I haven't got any quest for you now.", null);
									

		// player says yes they brought the items needed
		// we can't use the nice ChatActions here because we have to timestamp the quest slot
		npc.add(
			ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.YES_MESSAGES, 
			new QuestInStateCondition(mithrilcloak.getQuestSlot(), "took_letter"),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.isEquipped("mithril thread", 40)) {
						
							player.drop("mithril thread", 40);
							npc.say("Lovely. In " 
									   + REQUIRED_HOURS_FABRIC + " hours your fabric will be ready.");
							player.setQuest(mithrilcloak.getQuestSlot(), "weavingfabric;" + System.currentTimeMillis());
							player.notifyWorldAboutChanges();
						} else {
							npc.say("You don't appear to have 40 spools of mithril thread with you. Sorry, I can't do anything without it.");
						}
				}
			});

		// player says they didn't bring the stuff yet
		npc.add(
			ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.NO_MESSAGES, 
			null,
			ConversationStates.ATTENDING,
			"Oh, ok, well I hope you haven't lost them, they are precious!",
			null);

		// player returns while fabric is still being woven, or is ready
		npc.add(ConversationStates.ATTENDING, 
			Arrays.asList("weave", "fabric", "magical", "mithril fabric", "ida", "mithril", "cloak", "mithril cloak", "task", "quest"),
			new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "weavingfabric;"),
			ConversationStates.ATTENDING, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String[] tokens = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
					final long delay = REQUIRED_HOURS_FABRIC * MathHelper.MILLISECONDS_IN_ONE_HOUR;
					final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();
					if (timeRemaining > 0L) {
						npc.say("I'm sorry, you're too early. Come back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
						return;
					}
					npc.say("Here your fabric is ready! Isn't it gorgeous?");
					player.addXP(100);
					player.addKarma(15);
					final Item fabric = SingletonRepository.getEntityManager().getItem(
									mithrilcloak.getFabricName());
					fabric.setBoundTo(player.getName());
					player.equipOrPutOnGround(fabric);
					player.setQuest(mithrilcloak.getQuestSlot(), "got_fabric");
					player.notifyWorldAboutChanges();
				}
			});
	}

	private void giveLetterStep() {	

		final SpeakerNPC npc = npcs.get("Pedinghaus");

		// accept the letter
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("letter", "note", "whiggins", "apology"),
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_letter"), new PlayerHasItemWithHimCondition("sealed envelope")),
				ConversationStates.ATTENDING,
				"*reads* ... *reads* ... Well, I must say, that is a weight off my mind. Thank you ever so much. Please convey my warmest regards to Whiggins. All is forgiven.",
				new MultipleActions(
									 new DropItemAction("sealed envelope"), 
									 new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "took_letter", 10.0)
				));
	}

	private void giveFabricStep() {	

		final SpeakerNPC npc = npcs.get("Ida");

		// accept the fabric and ask for scissors
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("fabric", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_fabric"), new PlayerHasItemWithHimCondition(mithrilcloak.getFabricName())),
				ConversationStates.ATTENDING,
				"Wow you got the " + mithrilcloak.getFabricName() + " , that took longer than I expected! Now, to cut it I need magical #scissors, if you would go get them from #Hogart. I will be waiting for you to return.",
				new MultipleActions(
									 new DropItemAction(mithrilcloak.getFabricName()), 
									 new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "need_scissors", 10.0)
				));

		// remind about fabric. there are so many steps to getting fabric 
		// that the player could be in many quest states and she still is just waiting for fabric
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("fabric", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new OrCondition(
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_fabric"),
								new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingthread;"),
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_thread"),
								new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "fusingthread;"),
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_mithril_thread"),
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_letter"),
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "took_letter"),
								new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_fabric"),
												 new NotCondition(new PlayerHasItemWithHimCondition(mithrilcloak.getFabricName()))
												)
								 ),
				ConversationStates.ATTENDING,
				"I'm still waiting for the " + mithrilcloak.getFabricName() 
				+ " so I can start work on your mithril cloak. You should ask #Kampusch about anything textile related.",				
				null);

		npc.addReply("Hogart", "He's that grumpy old dwarf in the Or'ril mines. I already sent him a message saying I wanted some new scissors but he didn't respond. Well, what he lacks in people skills he makes up for in his metal work.");
	}

	public void addToWorld() {
		makeThreadStep();
		fetchThreadStep();
		makeMithrilThreadStep();
		makeMithrilFabricStep();
		giveLetterStep();
		giveFabricStep();	
	}

}
