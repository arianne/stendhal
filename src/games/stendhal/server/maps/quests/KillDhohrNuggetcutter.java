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

import java.util.ArrayList;
import java.util.HashMap;
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
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.Pair;

/**
 * QUEST: Kill Dhohr Nuggetcutter
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Zogfang
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Zogfang asks you to kill remaining dwarves from area
 * <li> You go kill Dhohr Nuggetcutter and you get the reward from Zogfang
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> mithril nugget
 * <li> 4000 XP
 * <li>35 karma in total
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> after 14 days.
 * </ul>
 */

public class KillDhohrNuggetcutter extends AbstractQuest {

	private static final String QUEST_SLOT = "kill_dhohr_nuggetcutter";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Zogfang");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("rejected")) {
							raiser.say("We are unable to rid our area of dwarves. Especially one mighty one named Dhohr Nuggetcutter. Would you please kill them?");
						}  else if (player.getQuest(QUEST_SLOT, 0).equals("start")) {
							raiser.say("I already asked you to kill Dhohr Nuggetcutter!");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}  else if (player.getQuest(QUEST_SLOT).startsWith("killed;")) {
							final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
							final long delay = 2 * MathHelper.MILLISECONDS_IN_ONE_WEEK;
							final long timeRemaining = Long.parseLong(tokens[1]) + delay - System.currentTimeMillis();
							if (timeRemaining > 0) {
								raiser.say("Thank you for helping us. Maybe you could come back later. The dwarves might return. Try coming back in " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
								raiser.setCurrentState(ConversationStates.ATTENDING);
								return;
							}
							raiser.say("Would you like to help again clearing this Keep of our enemies, those dwarves?");
						} else {
							raiser.say("Thank you for your help in our time of need. Now we feel much safer.");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		final HashMap<String, Pair<Integer, Integer>> toKill = new HashMap<String, Pair<Integer, Integer>>();
		toKill.put("Dhohr Nuggetcutter", new Pair<Integer, Integer>(0,1));
		toKill.put("mountain dwarf", new Pair<Integer, Integer>(0,2));
		toKill.put("mountain elder dwarf", new Pair<Integer, Integer>(0,2));
		toKill.put("mountain hero dwarf", 	new Pair<Integer, Integer>(0,2));
		toKill.put("mountain leader dwarf", new Pair<Integer, Integer>(0,2));

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "start"));
		actions.add(new IncreaseKarmaAction(10));
		actions.add(new StartRecordingKillsAction(QUEST_SLOT, 1, toKill));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Great! Please find all wandering #dwarves somewhere in this level of the keep and make them pay for their tresspassing!",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Ok, I will await someone having the guts to have the job done.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void step_2() {
		/* Player has to kill the dwarves*/
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Zogfang");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new NotCondition(new KilledForQuestCondition(QUEST_SLOT, 1))),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						raiser.say("Just go kill Dhohr Nuggetcutter and his minions; the mountain leader, hero and elder dwarves. Even the simple mountain dwarves are a danger to us, kill them too.");
				}
		});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new KilledForQuestCondition(QUEST_SLOT, 1)),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						raiser.say("Thank you so much. You are a warrior, indeed! Here, have one of these. We have found them scattered about. We have no idea what they are.");
							final Item mithrilnug = SingletonRepository.getEntityManager()
									.getItem("mithril nugget");
							player.equipOrPutOnGround(mithrilnug);
							player.addKarma(25.0);
							player.addXP(4000);
							player.setQuest(QUEST_SLOT, "killed;" + System.currentTimeMillis());
		 			}
				});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Kill Dhohr Nuggetcutter",
				"Zogfang, the orc who guards the entrance of the Abandoned Keep, isn't feeling safe while some dwarves still remain in the Keep.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			if (!isCompleted(player)) {
				res.add("I must kill the Dhohr Nuggetcutter and his cronies to satisfy Zogfang.");
			} else if(isRepeatable(player)){
				res.add("Zogfang is anxious again and will reward me if I help him.");
			} else {
				res.add("My attacks on the dwarves have calmed Zogfang's nerves for the time being.");
			}
			return res;
	}

	@Override
	public String getName() {
		return "KillDhohrNuggetcutter";
	}

	// The kill requirements and surviving in the zone requires at least this level
	@Override
	public int getMinLevel() {
		return 70;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
				 new TimePassedCondition(QUEST_SLOT, 1, 2*MathHelper.MINUTES_IN_ONE_WEEK)).fire(player,null, null);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"killed").fire(player, null, null);
	}

	@Override
	public String getNPCName() {
		return "Zogfang";
	}
}
