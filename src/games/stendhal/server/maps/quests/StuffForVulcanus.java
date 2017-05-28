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
package games.stendhal.server.maps.quests;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.logic.BringOrderedListOfItemsQuestLogic;
import games.stendhal.server.maps.quests.logic.ItemCollector;
import games.stendhal.server.util.TimeUtil;

/**
 * QUEST: The immortal sword forging.
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Vulcanus, son of Zeus itself, will forge for you the god's sword.
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Vulcanus tells you about the sword.
 * <li> He offers to forge a immortal sword for you if you bring him what it
 * needs.
 * <li> You give him all what he ask you.
 * <li> He tells you you must have killed a giant to get the shield
 * <li> Vulcanus forges the immortal sword for you
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> immortal sword
 * <li> 15000 XP
 * <li> some karma (25)
 * </ul>
 *
 *
 * REPETITIONS:
 * <ul>
 * <li> None.
 * </ul>
 */
public class StuffForVulcanus extends AbstractQuest {

	private static final String I_WILL_NEED_SEVERAL_THINGS = "I will need several things: ";

	private static final String IN_EXACT_ORDER = "Come back when you have them in the same #exact order!";

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "immortalsword_quest";

	private final ItemCollector itemCollector = new ItemCollector();

	private final BringOrderedListOfItemsQuestLogic questLogic = new BringOrderedListOfItemsQuestLogic();

	public StuffForVulcanus() {
		itemCollector.require().item("iron").pieces(15).bySaying("I cannot #forge it without the missing %s.");
		itemCollector.require().item("wood").pieces(26).bySaying("How do you expect me to #forge it without missing %s for the fire?");
		itemCollector.require().item("gold bar").pieces(12).bySaying("I must pay a bill to spirits in order to cast the enchantment over the sword. I need %s more.");
		itemCollector.require().item("giant heart").pieces(6).bySaying("It is the base element of the enchantment. I need %s still.");

		questLogic.setItemCollector(itemCollector);
		questLogic.setQuest(this);
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Vulcanus");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
						raiser.say("I once forged the most powerful of swords. I can do it again for you. Are you interested?");
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						raiser.say("Oh! I am so tired. Look for me later. I need a few years of relaxing.");
						raiser.setCurrentState(ConversationStates.ATTENDING);
					} else {
						raiser.say("Why are you bothering me when you haven't completed your quest yet?");
						raiser.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					raiser.say(I_WILL_NEED_SEVERAL_THINGS + questLogic.itemsStillNeeded(player) + ". " + IN_EXACT_ORDER);
					player.setQuest(QUEST_SLOT, "start;0;0;0;0");
				}
			});

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Oh, well forget it then, if you don't want an immortal sword...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));

		npc.addReply("exact",
			"This archaic magic requires that the ingredients are added on an exact order.");
	}

	private void step_2() {
		/* Get the stuff. */
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Vulcanus");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "start")),
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					boolean missingSomething = questLogic.proceedItems(player, raiser);

					if (player.hasKilled("giant") && !missingSomething) {
						raiser.say("You've brought everything I need to make the immortal sword, and what is more, you are strong enough to handle it. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					} else {
						if (!player.hasKilled("giant") && !missingSomething) {
							raiser.say("Did you really get those giant hearts yourself? I don't think so! This powerful sword can only be given to those that are strong enough to kill a #giant.");
						}

						questLogic.updateQuantitiesInQuestStatus(player);
					}
				}
			});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "forging;")),
			ConversationStates.IDLE, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {

					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
					final long timeRemaining = Long.parseLong(tokens[1]) + delay
							- System.currentTimeMillis();

					if (timeRemaining > 0L) {
						raiser.say("I haven't finished forging the sword. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
						return;
					}

					raiser.say("I have finished forging the mighty immortal sword. You deserve this. Now I'm going to have a long rest, so, goodbye!");
					player.addXP(15000);
					player.addKarma(25);
					final Item magicSword = SingletonRepository.getEntityManager().getItem("immortal sword");
					magicSword.setBoundTo(player.getName());
					player.equipOrPutOnGround(magicSword);
					player.notifyWorldAboutChanges();
					player.setQuest(QUEST_SLOT, "done");
				}
			});

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("forge", "missing"),
			new QuestStateStartsWithCondition(QUEST_SLOT, "start;"),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final String questState = player.getQuest(QUEST_SLOT);
					if (!broughtAllItems(questState)) {
						raiser.say("I will need " + questLogic.itemsStillNeededWithHash(player) + ".");
					}
				}
			});

		npc.add(ConversationStates.ANY,
				"iron",
				null,
				ConversationStates.ATTENDING,
				"Collect some iron ore from the mines which are rich in minerals.",
				null);
		npc.add(ConversationStates.ANY,
				"wood",
				null,
				ConversationStates.ATTENDING,
				"The forest is full of wood logs.",
				null);
		npc.add(ConversationStates.ANY,
				Arrays.asList("gold", "gold bar"),
				null,
				ConversationStates.ATTENDING,
				"A smith in Ados can forge the gold into gold bars for you.",
				null);
		npc.add(ConversationStates.ANY,
				Arrays.asList("giant", "giant heart"),
				null,
				ConversationStates.ATTENDING,
				"There are ancient stories of giants living in the mountains at the north of Semos and Ados.",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Stuff for Vulcanus",
				"Vulcanus, the son of Zeus himself, will forge the god's sword.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "StuffForVulcanus";
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new LinkedList<>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("I met Vulcanus in Kotoch.");
		if (questState.equals("rejected")) {
			res.add("I don't want an immortal sword.");
			return res;
		}
		res.add("To forge the immortal sword I must bring several things to Vulcanus.");
		if (questState.startsWith("start") && !broughtAllItems(questState)) {
			String suffix = ".";
			if (questLogic.neededItemsWithAmounts(player).size() > 1) {
				suffix = ", in this order.";
			}
			res.add("I still need to bring " + questLogic.itemsStillNeeded(player) + suffix);
		} else if (broughtAllItems(questState) || !questState.startsWith("start")) {
			res.add("I took all the special items to Vulcanus.");
		}
		if (broughtAllItems(questState) && !player.hasKilled("giant")) {
			res.add("I must prove my worth and kill a giant, before I am worthy of this prize.");
		}
		if (questState.startsWith("forging")) {
			res.add("Vulcanus, son of gods himself, now forges my immortal sword.");
		}
		if (isCompleted(player)) {
			res.add("Gold bars and giant hearts together with the forging from a god's son made me a sword of which I can be proud.");
		}
		return res;
	}

	private boolean broughtAllItems(final String questState) {
		return "start;15;26;12;6".equals(questState);
	}

	// match to the min level of the immortal sword
	@Override
	public int getMinLevel() {
		return 80;
	}

	@Override
	public String getNPCName() {
		return "Vulcanus";
	}

	@Override
	public String getRegion() {
		return Region.KOTOCH;
	}
}
