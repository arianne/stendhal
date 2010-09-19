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

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Cloaks for Bario
 * 
 * PARTICIPANTS:
 * <ul>
 * <li> Bario, a guy living in an underground house deep under the Ados Wildlife Refuge</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Bario asks you for a number of blue elf cloaks.</li>
 * <li> You get some of the cloaks somehow, e.g. by killing elves.</li>
 * <li> You bring the cloaks to Bario and give them to him.</li>
 * <li> Repeat until Bario received enough cloaks. (Of course you can bring up
 * all required cloaks at the same time.)</li>
 * <li> Bario gives you a golden shield in exchange.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li> golden shield</li>
 * <li> 15000 XP</li>
 * <li> Karma: 25</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class CloaksForBario extends AbstractQuest {

	private static final int REQUIRED_CLOAKS = 10;

	private static final String QUEST_SLOT = "cloaks_for_bario";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Bario");

		// player says hi before starting the quest
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Hey! How did you get down here? You did what? Huh. Well, I'm Bario. I don't suppose you could do a #task for me?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I don't dare go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here... Can you help me?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"You promised me to bring me ten blue elven cloaks. Remember?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I don't have anything for you to do, really.", null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.QUEST_OFFERED, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							// player has already finished the quest
							raiser.say("I don't have anything else for you to do, really. Thanks for the offer.");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						} else {
							if (player.hasQuest(QUEST_SLOT) && !"rejected".equals(player.getQuest(QUEST_SLOT))) {
								raiser.say("You promised me to bring me ten blue elven cloaks. Remember?");
							} else {
								raiser.say("I don't dare go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here... Can you help me?");
							}
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"I need some blue elven cloaks if I'm to survive the winter. Bring me ten of them, and I will give you a reward.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, Integer.toString(REQUIRED_CLOAKS), 5.0));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Oh dear... I'm going to be in trouble...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void step_2() {
		// Just find some of the cloaks somewhere and bring them to Bario.
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Bario");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						raiser.say("Hi again! I still need "
							+ player.getQuest(QUEST_SLOT)
							+ " blue elven "
							+ Grammar.plnoun(
									MathHelper.parseInt(player.getQuest(QUEST_SLOT)),
									"cloak") + ". Do you have any for me?");
					}
				});

		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Welcome! Thanks again for those cloaks.", null);

		// player says he doesn't have any blue elf cloaks with him
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Too bad.", null);

		// player says he has a blue elf cloak with him
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.drop("blue elf cloak")) {
							// find out how many cloaks the player still has to
							// bring
							final int toBring = Integer.parseInt(player.getQuest(QUEST_SLOT)) - 1;
							if (toBring > 0) {
								player.setQuest(QUEST_SLOT,
										Integer.toString(toBring));
								raiser.say("Thank you very much! Do you have another one? I still need "
										+ Grammar.quantityplnoun(toBring,
												"cloak", "one") + ".");
								raiser.setCurrentState(ConversationStates.QUESTION_1);
							} else {
								final Item goldenShield = SingletonRepository.getEntityManager().getItem(
										"golden shield");
								goldenShield.setBoundTo(player.getName());
								player.equipOrPutOnGround(goldenShield);
								player.addXP(15000);
								player.addKarma(25);
								player.notifyWorldAboutChanges();
								player.setQuest(QUEST_SLOT, "done");
								raiser.say("Thank you very much! Now I have enough cloaks to survive the winter. Here, take this golden shield as a reward.");
							}
						} else {
							raiser.say("Really? I don't see any...");
						}
					}
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
		fillQuestInfo(
				"Cloaks for Bario",
				"Bario, the freezing dwarf, needs cloaks to keep him warm.",
				false);
	}
	@Override
	public String getName() {
		return "CloaksForBario";
	}
	
	@Override
	public int getMinLevel() {
		return 20;
	}
}
