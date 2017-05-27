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
 * QUEST: The mithril shield forging.
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Baldemar, mithrilbourgh elite wizard, will forge a mithril shield.
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Baldemar tells you about shield.
 * <li> He offers to forge a mithril shield for you if you bring him what he
 * needs.
 * <li> You give him all he asks for.
 * <li> Baldemar checks if you have ever killed a black giant alone, or not
 * <li> Baldemar forges the shield for you
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> mithril shield
 * <li> 95000 XP
 * <li> some karma (25)
 * </ul>
 *
 *
 * REPETITIONS:
 * <ul>
 * <li> None.
 * </ul>
 */
public class StuffForBaldemar extends AbstractQuest {

	static final String TALK_NEED_KILL_GIANT = "This shield can only be given to those who have killed a black giant, and without the help of others.";

	private static final String I_WILL_NEED_MANY_THINGS = "I will need many, many things: ";

	private static final String IN_EXACT_ORDER = "Come back when you have them in the same #exact order!";

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "mithrilshield_quest";

	private final ItemCollector itemCollector = new ItemCollector();

	private final BringOrderedListOfItemsQuestLogic questLogic = new BringOrderedListOfItemsQuestLogic();

	public StuffForBaldemar() {
		itemCollector.require().item("mithril bar").pieces(20)
				.bySaying("I cannot #forge it without the missing %s. After all, this IS a mithril shield.");
		itemCollector.require().item("obsidian")
				.bySaying("I need several gems to grind into dust to mix with the mithril. I need %s still.");
		itemCollector.require().item("diamond")
				.bySaying("I need several gems to grind into dust to mix with the mithril. I need %s still.");
		itemCollector.require().item("emerald").pieces(5)
				.bySaying("I need several gems to grind into dust to mix with the mithril. I need %s still.");
		itemCollector.require().item("carbuncle").pieces(10)
				.bySaying("I need several gems to grind into dust to mix with the mithril. I need %s still.");
		itemCollector.require().item("sapphire").pieces(10)
				.bySaying("I need several gems to grind into dust to mix with the mithril. I need %s still.");
		itemCollector.require().item("black shield").bySaying("I need %s to form the framework for your new shield.");
		itemCollector.require().item("magic plate shield")
				.bySaying("I need %s for the pieces and parts for your new shield.");
		itemCollector.require().item("gold bar").pieces(10)
				.bySaying("I need %s to melt down with the mithril and iron.");
		itemCollector.require().item("iron").pieces(20).bySaying("I need %s to melt down with the mithril and gold.");
		itemCollector.require().item("black pearl").pieces(10)
				.bySaying("I need %s to crush into fine powder to sprinkle onto shield to give it a nice sheen.");
		itemCollector.require().item("shuriken").pieces(20).bySaying(
				"I need %s to melt down with the mithril, gold and iron. It is a 'secret' ingredient that only you and I know about. ;)");
		itemCollector.require().item("marbles").pieces(15).bySaying("My son wants some new toys. I need %s still.");
		itemCollector.require().item("snowglobe").bySaying("I just LOVE those trinkets from Athor. I need %s still.");

		questLogic.setItemCollector(itemCollector);
		questLogic.setQuest(this);
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Baldemar");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
						raiser.say("I can forge a shield made from mithril along with several other items. Would you like me to do that?");
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						raiser.say("I would prefer you left me to my entertainment.");
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
					String need = I_WILL_NEED_MANY_THINGS + questLogic.itemsStillNeeded(player) + ". " + IN_EXACT_ORDER;
					raiser.say(need);
					player.setQuest(QUEST_SLOT, "start;0;0;0;0;0;0;0;0;0;0;0;0;0;0");

				}
			});

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"I can't believe you are going to pass up this opportunity! You must be daft!!!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));

		npc.addReply("exact",
			"As I have listed them here, you must provide them in that order.");
	}

	private void step_2() {
		/* Get the stuff. */
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Baldemar");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestStateStartsWithCondition(QUEST_SLOT, "start")),
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					boolean missingSomething = questLogic.proceedItems(player, raiser);

					if (player.hasKilledSolo("black giant") && !missingSomething) {
						raiser.say("You've brought everything I need to forge the shield. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					} else {
						if (!player.hasKilledSolo("black giant") && !missingSomething) {
							raiser.say(TALK_NEED_KILL_GIANT);
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
						raiser.say("I haven't finished forging your shield. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
						return;
					}

					raiser.say("I have finished forging your new mithril shield. Enjoy. Now I will see what Trillium has stored behind the counter for me. ;)");
					player.addXP(95000);
					player.addKarma(25);
					final Item mithrilshield = SingletonRepository.getEntityManager().getItem("mithril shield");
					mithrilshield.setBoundTo(player.getName());
					player.equipOrPutOnGround(mithrilshield);
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
						raiser.say("I need " + questLogic.itemsStillNeeded(player) + ".");
					} else {
						if(!player.hasKilledSolo("black giant")) {
							raiser.say(TALK_NEED_KILL_GIANT);
						}
					}
				}
			});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Stuff for Baldemar",
				"Baldemar, a friendly mithrilbourgh elite wizard, will forge a special shield.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "StuffForBaldemar";
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new LinkedList<>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("I met Baldemar in the magic theater.");
		if (questState.equals("rejected")) {
			res.add("I'm not interested in his ideas about shields made from mithril.");
			return res;
		}
		res.add("Baldemar asked me to bring him many things.");
		if (questState.startsWith("start") && !broughtAllItems(questState)) {
			String suffix = ".";
			if (questLogic.neededItemsWithAmounts(player).size() > 1) {
				suffix = ", in this order.";
			}
			res.add("I still need to bring " + questLogic.itemsStillNeeded(player) + suffix);
		} else if (broughtAllItems(questState) || !questState.startsWith("start")) {
			res.add("I took all the special items to Baldemar.");
		}
		if (broughtAllItems(questState) && !player.hasKilledSolo("black giant")) {
			res.add("I will need to bravely face a black giant alone, before I am worthy of this shield.");
		}
		if (questState.startsWith("forging")) {
			res.add("Baldemar is forging my mithril shield!");
		}
		if (isCompleted(player)) {
			res.add("I brought Baldemar many items, killed a black giant solo, and he forged me a mithril shield.");
		}
		return res;
	}

	private boolean broughtAllItems(final String questState) {
		return "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1".equals(questState);
	}

	@Override
	public int getMinLevel() {
		return 100;
	}

	@Override
	public String getNPCName() {
		return "Baldemar";
	}

	@Override
	public String getRegion() {
		return Region.FADO_CAVES;
	}
}
