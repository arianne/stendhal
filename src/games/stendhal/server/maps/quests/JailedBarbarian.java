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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: The Jailed Barbarian
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Lorenz, the jailed barbarian in a hut on Amazon Island</li>
 * <li>Esclara the Amazon Princess</li>
 * <li>Ylflia the Princess of Kalavan</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>1. Lorenz ask you for a scythe to bring him</li>
 * <li>2. You have to ask Princess Esclara for a 'reason'</li>
 * <li>3. You have to bring him an egg</li>
 * <li>4. You have to inform Princess Ylflia</li>
 * <li>5. You have to bring him a barbarian armor</li>
 * <li>6. You get a reward.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>You get 20 gold bars</li>
 * <li>Karma: 15</li>
 * <li>You get 52,000 experience points in all</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Not repeatable.</li>
 * </ul>
 */

 public class JailedBarbarian extends AbstractQuest {
 	private static final String QUEST_SLOT = "jailedbarb";

	private static Logger logger = Logger.getLogger(JailedBarbarian.class);

 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step1() {
		final SpeakerNPC npc = npcs.get("Lorenz");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I need some help to escape from this prison. These ugly Amazonesses! Can you help me please?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for your help! Now I can escape!",
				null);


		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you! First I need a #scythe to cut down these ugly flowers. And beware of bringing me an old one! Let me know if you have one!",
				new SetQuestAction(QUEST_SLOT, "start"));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"So go away and someone who can help me!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	private void step2() {
	final SpeakerNPC npc = npcs.get("Lorenz");

	    final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("scythe"));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "capture"));
		reward.add(new IncreaseKarmaAction(10));

		npc.add(ConversationStates.ATTENDING, "scythe",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"),
				new PlayerHasItemWithHimCondition("scythe")),
				ConversationStates.ATTENDING,
				"Thank you!! First part is done! Now I can cut all flowers down! Now please ask Princess Esclara why I am here! I think saying my name should tell her something...",
				new MultipleActions(reward));

		npc.add(
			ConversationStates.ATTENDING, "scythe",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("scythe"))),
			ConversationStates.ATTENDING,
			"You don't have a scythe yet! Go and get one and hurry up!",
			null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"I already asked you to bring me a #scythe to cut the flowers down!",
				null);
	}

	private void step3() {
	final SpeakerNPC npc = npcs.get("Princess Esclara");

		npc.add(ConversationStates.ATTENDING, "Lorenz",
				new QuestInStateCondition(QUEST_SLOT, "capture"),
				ConversationStates.ATTENDING,
				"You want to know why he is in there? He and his ugly friends dug the #tunnel to our sweet Island! That's why he got jailed!",
				new SetQuestAction(QUEST_SLOT, "princess"));

		npc.add(ConversationStates.ATTENDING, "tunnel",
				new QuestInStateCondition(QUEST_SLOT, "princess"),
				ConversationStates.ATTENDING, "I am angry now and won't speak any more of it! If you want to learn more you'll have to ask him about the #tunnel!",
				null);

	}

	private void step4() {
	final SpeakerNPC npc = npcs.get("Lorenz");

		npc.add(ConversationStates.ATTENDING, "tunnel",
				new QuestInStateCondition(QUEST_SLOT, "princess"),
				ConversationStates.ATTENDING,
				"What she drives me nuts, like all the flowers! This makes me hungry, go and get an #egg for me! Just let me know, you got one.",
				new SetQuestAction(QUEST_SLOT, "egg"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "capture"),
				ConversationStates.ATTENDING,
				"Please go ask Princess Esclara why I am here! I think saying my name should prompt her to tell you",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "princess"),
				ConversationStates.ATTENDING,
				"I bet Princess Esclara said I was imprisoned because of the #tunnel ... ",
				null);
	}

	private void step5() {
		final SpeakerNPC npc = npcs.get("Lorenz");

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("egg"));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "jailed"));
		reward.add(new IncreaseKarmaAction(10));

		npc.add(ConversationStates.ATTENDING, "egg",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "egg"),
						new PlayerHasItemWithHimCondition("egg")),
						ConversationStates.ATTENDING,
						"Thank you again my friend. Now you have to tell Princess Ylflia, in Kalavan Castle, that I am #jailed here. Please hurry up!",
						new MultipleActions(reward));

		npc.add(
			ConversationStates.ATTENDING, "egg",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "egg"), new NotCondition(new PlayerHasItemWithHimCondition("egg"))),
			ConversationStates.ATTENDING,
			"I cannot see an egg!",
			null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "egg"),
				ConversationStates.ATTENDING,
				"I asked you to fetch an #egg for me!",
				null);

		npc.add(ConversationStates.ATTENDING, "jailed",
				new QuestInStateCondition(QUEST_SLOT, "jailed"),
				ConversationStates.ATTENDING, "I know that *I'm* jailed! I need you to go tell Princess Ylflia that I am here!",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "jailed"),
				ConversationStates.ATTENDING,
				"I need you to go tell Princess Ylflia that I am #jailed here.",
				null);
	}

	private void step6() {
	final SpeakerNPC npc = npcs.get("Princess Ylflia");

		npc.add(ConversationStates.ATTENDING, Arrays.asList("jailed", "Lorenz"),
				new QuestInStateCondition(QUEST_SLOT, "jailed"),
				ConversationStates.ATTENDING,
				"Oh my dear. My father should not know it. Hope he is fine! Thanks for this message! Send him #greetings! You better return to him, he could need more help.",
				new SetQuestAction(QUEST_SLOT, "spoken"));

		npc.add(ConversationStates.ATTENDING, "greetings",
				new QuestInStateCondition(QUEST_SLOT, "spoken"),
				ConversationStates.ATTENDING, "Please, go and give Lorenz my #greetings.",
				null);

	}

	private void step7() {
	final SpeakerNPC npc = npcs.get("Lorenz");

		npc.add(ConversationStates.ATTENDING, "greetings",
				new QuestInStateCondition(QUEST_SLOT, "spoken"),
				ConversationStates.ATTENDING,
				"Thanks my friend. Now a final task for you! Bring me a barbarian armor. Without this I cannot escape from here! Go! Go! And let me know when you have the #armor !",
				new SetQuestAction(QUEST_SLOT, "armor"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "greetings"),
				ConversationStates.ATTENDING,
				"I suppose you must have spoken with Princess Ylflia by now ... I do hope she sent her warmest #greetings to me...",
				null);
	}

	private void step8() {
		final SpeakerNPC npc = npcs.get("Lorenz");

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("barbarian armor"));
		reward.add(new IncreaseXPAction(50000));
		reward.add(new EquipItemAction("gold bar", 20));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(15));

		npc.add(ConversationStates.ATTENDING, "armor",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "armor"),
						new PlayerHasItemWithHimCondition("barbarian armor")),
						ConversationStates.ATTENDING,
						"Thats all! Now I am prepared for my escape! Here is something I have stolen from Princess Esclara! Do not let her know. And now leave me!",
						new MultipleActions(reward));

		npc.add(
			ConversationStates.ATTENDING, "armor",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "armor"), new NotCondition(new PlayerHasItemWithHimCondition("barbarian armor"))),
			ConversationStates.ATTENDING,
			"You have no barbarian armor with you! Go get one!",
			null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "armor"),
				ConversationStates.ATTENDING,
				"I am waiting for you to bring me a barbarian #armor so I am strong enough to escape.",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Jailed Barbarian",
				"Lorenz is a jailed barbarian on Amazon Island. It's a mystery why he is jailed there, but perhaps he needs help.",
				true);
		step1();
		step2();
		step3();
		step4();
		step5();
		step6();
		step7();
		step8();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("I found my way into Lorenz's hut.");
		res.add("Lorenz wants a scythe, and not an old one, to cut down the flowers he finds ugly.");
		if ("rejected".equals(questState)) {
			res.add("I don't want to help Lorenz cut down flowers. Whatever he's jailed for, I bet he deserves it.");
			return res;
		}
		if ("start".equals(questState)) {
			return res;
		}
		res.add("Lorenz wants me to ask Princess Esclara why he's captured. I must say his name to remind her.");
		if ("capture".equals(questState)) {
			return res;
		}
		res.add("The Princess told me Lorenz is jailed for digging that tunnel! So I should tell him, it was the tunnel.");
		if ("princess".equals(questState)) {
			return res;
		}
		res.add("Lorenz suddenly got hungry and demanded I bring him an egg. He's really bad tempered.");
		if ("egg".equals(questState)) {
			return res;
		}
		res.add("Now I have to tell Princess Ylflia, why Lorenz wasn't in touch ... I'm not even sure why he knows her! But anyway, I must say his name to her.");
		if ("jailed".equals(questState)) {
			return res;
		}
		res.add("Princess Ylflia begged me to send that barbarian her greetings.");
		if ("spoken".equals(questState)) {
			return res;
		}
		res.add("Lorenz finally resolved to try to break free and I need to get him a barbarian armor for that.");
		if ("armor".equals(questState)) {
			return res;
		}
		res.add("I brought Lorenz the armor! He gave me gold he'd stolen from the Princess and I earned a lot of experience.");
		if (isCompleted(player)) {
			return res;
		}
		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Quest state is: " + questState);
		logger.error("History doesn't have a matching quest state for " + questState);
		return debug;
	}

	@Override
	public String getName() {
		return "JailedBarbarian";
	}

	// Amazon is dangerous below this level - don't hint to go there
	@Override
	public int getMinLevel() {
		return 70;
	}

	@Override
	public String getRegion() {
		return Region.AMAZON_ISLAND;
	}
	@Override
	public String getNPCName() {
		return "Lorenz";
	}
}
