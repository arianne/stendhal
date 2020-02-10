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
package games.stendhal.server.maps.semos.bakery;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

/**
 * A woman who bakes bread for players.
 *
 * Erna will lend tools.
 *
 * @author daniel / kymara
 */
public class ShopAssistantNPC implements ZoneConfigurator  {

	private static final int COST = 3000;
	private static final String QUEST_SLOT = "borrow_kitchen_equipment";

	private static final List<String> ITEMS = Arrays.asList("sugar mill", "pestle and mortar", "rotary cutter");


	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Erna") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(26,9));
                nodes.add(new Node(26,6));
                nodes.add(new Node(28,6));
                nodes.add(new Node(28,2));
                nodes.add(new Node(28,5));
                nodes.add(new Node(22,5));
                nodes.add(new Node(22,4));
                nodes.add(new Node(22,7));
                nodes.add(new Node(26,7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addJob("I'm the shop assistant at this bakery.");
				addReply("flour",
				"We usually get our #flour from a mill northeast of here, but the wolves ate their delivery boy! If you help us out by bringing some, we can #bake delicious bread for you.");
				addHelp("Bread is very good for you, especially for you adventurers who are always gulping down red meat. And my boss, Leander, happens to make the best sandwiches on the island!");
				addGoodbye();

				// Erna bakes bread if you bring her flour.
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", 2);

				final ProducerBehaviour behaviour = new ProducerBehaviour("erna_bake_bread",
						"bake", "bread", requiredResources, 10 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.");

				addOffer("Our pizza delivery team can #borrow some kitchen equipment from me.");

				add(ConversationStates.ATTENDING, "borrow",
				    new LevelLessThanCondition(6),
				    ConversationStates.ATTENDING,
				    "Oh sorry, I don't lend equipment to people with so little experience as you.",
				    null);

				add(ConversationStates.ATTENDING, "borrow",
				    new AndCondition(new LevelGreaterThanCondition(5), new QuestNotCompletedCondition("pizza_delivery")),
				    ConversationStates.ATTENDING,
				    "You'll have to speak to Leander and ask if you can help with the pizza before I'm allowed to lend you anything.",
				    null);

				add(ConversationStates.ATTENDING, "borrow",
				    new AndCondition(
				        new LevelGreaterThanCondition(5),
				        new QuestCompletedCondition("pizza_delivery"),
				        new QuestNotActiveCondition(QUEST_SLOT)),
				    ConversationStates.ATTENDING,
				    "I lend out " + Grammar.enumerateCollectionWithHash(ITEMS) + ". If you're interested, please say which you want.",
				    null);

				// player already has borrowed something it didn't return and will pay for it
				add(ConversationStates.ATTENDING, "borrow",
				    new AndCondition(new QuestActiveCondition(QUEST_SLOT), new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT))),
				    ConversationStates.QUESTION_1,
				    "You didn't return what I last lent you! Do you want to pay for it at a cost of " + COST + " money?",
				    null);

				// player already has borrowed something it didn't return and will return it
				add(ConversationStates.ATTENDING, "borrow",
				    new AndCondition(new QuestActiveCondition(QUEST_SLOT), new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT)),
				    ConversationStates.QUESTION_2,
				    "You didn't return what I last lent you! Do you want to return it now?",
				    null);

				// player wants to pay for previous item
				final List<ChatAction> payment = new LinkedList<ChatAction>();
				payment.add(new DropItemAction("money", COST));
				payment.add(new SetQuestAction(QUEST_SLOT, "done"));
				payment.add(new DecreaseKarmaAction(10));
				add(ConversationStates.QUESTION_1,
				    ConversationPhrases.YES_MESSAGES,
				    new PlayerHasItemWithHimCondition("money", COST),
				    ConversationStates.ATTENDING,
				    "Thanks. Just let me know if you want to #borrow any tools again.",
				    new MultipleActions(payment));

				// player already has borrowed something and wants to return it
				final List<ChatAction> returnitem = new LinkedList<ChatAction>();
				returnitem.add(new DropRecordedItemAction(QUEST_SLOT));
				returnitem.add(new SetQuestAction(QUEST_SLOT, "done"));
				add(ConversationStates.QUESTION_2,
				    ConversationPhrases.YES_MESSAGES,
				    new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT),
				    ConversationStates.ATTENDING,
				    "Thank you! Just let me know if you want to #borrow any tools again.",
				    new MultipleActions(returnitem));

				// don't want to pay for it now
				add(ConversationStates.QUESTION_1,
				    ConversationPhrases.NO_MESSAGES,
				    null,
				    ConversationStates.ATTENDING,
				    "No problem. Take as long as you need, but you can't borrow other tools till you return the last, or pay for it.",
				    null);
				// does want to pay for it now
				add(ConversationStates.QUESTION_1,
				    ConversationPhrases.YES_MESSAGES,
				    new NotCondition(new PlayerHasItemWithHimCondition("money", COST)),
				    ConversationStates.ATTENDING,
				    "Sorry, but it seems you dont have enough money with you.",
				    null);

				// don't want to return it now
				add(ConversationStates.QUESTION_2,
				    ConversationPhrases.NO_MESSAGES,
				    null,
				    ConversationStates.ATTENDING,
				    "No problem. Take as long as you need, but you can't borrow other tools till you return the last, or pay for it.",
				    null);


				// saying the item name and storing that item name into the quest slot, and giving the item
				for(final String itemName : ITEMS) {
					add(ConversationStates.ATTENDING,
					    itemName,
					    new AndCondition(
					        new LevelGreaterThanCondition(5),
					        new QuestCompletedCondition("pizza_delivery"),
					        new QuestNotActiveCondition(QUEST_SLOT)),
					    ConversationStates.ATTENDING,
					    null,
					    new ChatAction() {
							@Override
							public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
								final Item item =  SingletonRepository.getEntityManager().getItem(itemName);
								if (item == null) {
									npc.say("Sorry, something went wrong. Could you say correctly the item, please?");
								} else {
									player.equipOrPutOnGround(item);
									player.setQuest(QUEST_SLOT, itemName);
									npc.say("Here you are! Don't forget to #return it or you have to pay!");
								}
							}
						});
				}

				// additionally add "sugar" as trigger word
				add(ConversationStates.ATTENDING,
					    "sugar",
					    new AndCondition(
					        new LevelGreaterThanCondition(5),
					        new QuestCompletedCondition("pizza_delivery"),
					        new QuestNotActiveCondition(QUEST_SLOT)),
					    ConversationStates.ATTENDING,
					    "Sorry, I can't lend out sugar, only a #sugar #mill.",
					    null);

				// too low level
				add(ConversationStates.ATTENDING,
					    ITEMS,
					    new LevelLessThanCondition(6),
					    ConversationStates.ATTENDING,
					    "Sorry, as you have little experience in this world I can't trust you with my tools.",
					    null);

				// currently has borrowed an item
				add(ConversationStates.ATTENDING,
					    ITEMS,
					    new QuestActiveCondition(QUEST_SLOT),
					    ConversationStates.ATTENDING,
					    "You can't borrow from me again till you #return the last tool I lent you.",
					    null);

				// haven't done pizza
				add(ConversationStates.ATTENDING,
					    ITEMS,
					    new QuestNotCompletedCondition("pizza_delivery"),
					    ConversationStates.ATTENDING,
					    "Only pizza deliverers can borrow tools, please deliver one for Leander and then ask me again.",
					    null);

				// player asks about pay from attending state
				add(ConversationStates.ATTENDING, "pay",
				    new QuestActiveCondition(QUEST_SLOT),
				    ConversationStates.QUESTION_1,
				    "If you lost what I lent you, you can pay " + COST + " money. Do you want to pay now?",
				    null);

				// player asks about return from attending state
				add(ConversationStates.ATTENDING, "return",
				    new AndCondition(new QuestActiveCondition(QUEST_SLOT), new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT)),
				    ConversationStates.QUESTION_2,
				    "Do you want to return what you borrowed now?",
				    null);

				// player asks about return from attending state
				add(ConversationStates.ATTENDING, "return",
				    new AndCondition(new QuestActiveCondition(QUEST_SLOT), new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT))),
				    ConversationStates.QUESTION_1,
				    "You don't have it with you! Do you want to pay " + COST + " money for it now?",
				    null);

			}};
			npc.setPosition(26, 9);
			npc.setEntityClass("housewifenpc");
			npc.setDescription("You see Erna. She's worked a long time for Leander and is his loyal assistant.");
			zone.add(npc);
	}
}
