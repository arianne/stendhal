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

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
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
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private static final int REQUIRED_IRON = 15;

	private static final int REQUIRED_GOLD_BAR = 12;

	private static final int REQUIRED_WOOD = 26;

	private static final int REQUIRED_GIANT_HEART = 6;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "immortalsword_quest";

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
					raiser.say("I will need several things: "
						+ REQUIRED_IRON
						+ " iron, "
						+ REQUIRED_WOOD
						+ " wood logs, "
						+ REQUIRED_GOLD_BAR
						+ " gold bars and "
						+ REQUIRED_GIANT_HEART
						+ " giant hearts. Come back when you have them in the same #exact order!");
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
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					int neededIron = REQUIRED_IRON
							- Integer.parseInt(tokens[1]);
					int neededWoodLogs = REQUIRED_WOOD
							- Integer.parseInt(tokens[2]);
					int neededGoldBars = REQUIRED_GOLD_BAR
							- Integer.parseInt(tokens[3]);
					int neededGiantHearts = REQUIRED_GIANT_HEART
							- Integer.parseInt(tokens[4]);
					boolean missingSomething = false;

					if (!missingSomething && neededIron > 0) {
						if (player.isEquipped("iron", neededIron)) {
							player.drop("iron", neededIron);
							neededIron = 0;
						} else {
							final int amount = player.getNumberOfEquipped("iron");
							if (amount > 0) {
								player.drop("iron", amount);
								neededIron -= amount;
							}

							raiser.say("I cannot #forge it without the missing "
								+ Grammar.quantityplnoun(
										neededIron, "iron", "a")
								+ ".");
							missingSomething = true;
						}
					}

					if (!missingSomething && neededWoodLogs > 0) {
						if (player.isEquipped("wood", neededWoodLogs)) {
							player.drop("wood", neededWoodLogs);
							neededWoodLogs = 0;
						} else {
							final int amount = player.getNumberOfEquipped("wood");
							if (amount > 0) {
								player.drop("wood", amount);
								neededWoodLogs -= amount;
							}

							raiser.say("How do you expect me to #forge it without missing "
								+ Grammar.quantityplnoun(neededWoodLogs, "wood log", "a")
								+ " for the fire?");
							missingSomething = true;
						}
					}

					if (!missingSomething && neededGoldBars > 0) {
						if (player.isEquipped("gold bar", neededGoldBars)) {
							player.drop("gold bar", neededGoldBars);
							neededGoldBars = 0;
						} else {
							final int amount = player.getNumberOfEquipped("gold bar");
							if (amount > 0) {
								player.drop("gold bar", amount);
								neededGoldBars -= amount;
							}
							raiser.say("I must pay a bill to spirits in order to cast the enchantment over the sword. I need "
									+ Grammar.quantityplnoun(neededGoldBars, "gold bar", "one") + " more.");
							missingSomething = true;
						}
					}

					if (!missingSomething && neededGiantHearts > 0) {
						if (player.isEquipped("giant heart", neededGiantHearts)) {
							player.drop("giant heart", neededGiantHearts);
							neededGiantHearts = 0;
						} else {
							final int amount = player.getNumberOfEquipped("giant heart");
							if (amount > 0) {
								player.drop("giant heart", amount);
								neededGiantHearts -= amount;
							}
							raiser.say("It is the base element of the enchantment. I need "
								+ Grammar.quantityplnoun(neededGiantHearts, "giant heart", "one") + " still.");
							missingSomething = true;
						}
					}

					if (player.hasKilled("giant") && !missingSomething) {
						raiser.say("You've brought everything I need to make the immortal sword, and what is more, you are strong enough to handle it. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					} else {
						if (!player.hasKilled("giant") && !missingSomething) {
							raiser.say("Did you really get those giant hearts yourself? I don't think so! This powerful sword can only be given to those that are strong enough to kill a #giant.");
						}

						player.setQuest(QUEST_SLOT,
							"start;"
							+ (REQUIRED_IRON - neededIron)
							+ ";"
							+ (REQUIRED_WOOD - neededWoodLogs)
							+ ";"
							+ (REQUIRED_GOLD_BAR - neededGoldBars)
							+ ";"
							+ (REQUIRED_GIANT_HEART - neededGiantHearts));
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
			new QuestStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					final int neededIron = REQUIRED_IRON
							- Integer.parseInt(tokens[1]);
					final int neededWoodLogs = REQUIRED_WOOD
							- Integer.parseInt(tokens[2]);
					final int neededGoldBars = REQUIRED_GOLD_BAR
							- Integer.parseInt(tokens[3]);
					final int neededGiantHearts = REQUIRED_GIANT_HEART
							- Integer.parseInt(tokens[4]);

					raiser.say("I will need " + neededIron + " #iron, "
							+ neededWoodLogs + " #wood logs, "
							+ neededGoldBars + " #gold bars and "
							+ neededGiantHearts + " #giant hearts.");
				}
			});

		npc.add(
			ConversationStates.ANY,
			"iron",
			null,
			ConversationStates.ATTENDING,
			"Collect some iron ore from the mines which are rich in minerals.",
			null);

		npc.add(ConversationStates.ANY, "wood", null,
				ConversationStates.ATTENDING,
				"The forest is full of wood logs.", null);
		npc.add(ConversationStates.ANY, "gold", null,
				ConversationStates.ATTENDING,
				"A smith in Ados can forge the gold into gold bars for you.",
				null);
		npc.add(
			ConversationStates.ANY,
			"giant",
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
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			final String questState = player.getQuest(QUEST_SLOT);
			res.add("I met Vulcanus in Kotoch.");
			if (questState.equals("rejected")) {
				res.add("I don't want an immortal sword.");
				return res;
			} 
			res.add("To forge the immortal sword I must bring " + REQUIRED_IRON
					+ " iron, "
					+ REQUIRED_WOOD
					+ " wood logs, "
					+ REQUIRED_GOLD_BAR
					+ " gold bars and "
					+ REQUIRED_GIANT_HEART
					+ " giant hearts, in that order.");
			// yes, yes. this is the most horrible quest code and so you get a horrible quest history. 
			if(questState.startsWith("start") && !"start;15;26;12;6".equals(questState)){
				res.add("I haven't brought everything yet. Vulcanus will tell me what I need to take next.");
			} else if ("start;15;26;12;6".equals(questState) || !questState.startsWith("start")) {
				res.add("I took all the special items to Vulcanus.");
			}
			if("start;15;26;12;6".equals(questState) && !player.hasKilled("giant")){
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
