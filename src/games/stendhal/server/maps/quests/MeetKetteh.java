/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.JailAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NakedCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Speak with Ketteh
 *
 * PARTICIPANTS: - Ketteh Wehoh, a woman
 *
 * STEPS: - Talk to Ketteh to activate the quest and keep speaking with Ketteh.
 *
 * REWARD: - No XP - No money
 *
 * REPETITIONS: - As much as wanted.
 */
public class MeetKetteh extends AbstractQuest {
	private static final String QUEST_SLOT = "Ketteh";
	private static final int GRACE_PERIOD = 5;
	private static final int JAIL_TIME = 10;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step1() {
		final SpeakerNPC npc = npcs.get("Ketteh Wehoh");

		// force Ketteh to notice naked players that she has already warned
		// but leave a 5 minute (or GRACE_PERIOD) gap if she only just warned them
		npc.addInitChatMessage(
				new AndCondition(
						new NakedCondition(),
						new OrCondition(
								new AndCondition(
										new QuestInStateCondition(QUEST_SLOT, 0,"seen_naked"),
										new TimePassedCondition(QUEST_SLOT,1,GRACE_PERIOD)),
						        new QuestInStateCondition(QUEST_SLOT,"seen"),
						        new QuestInStateCondition(QUEST_SLOT,"learnt_manners"),
						        // done was an old state that was used when naked but then clothed,
						        // but they should do learnt_manners too
						        new QuestInStateCondition(QUEST_SLOT,"done"))),
				new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
		});

		// player is naked but may not have been warned recently, warn them and stamp the quest slot
		// this can be initiated by the npc as above
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NakedCondition(),
						new QuestNotInStateCondition(QUEST_SLOT, 0,"seen_naked")),
				ConversationStates.ATTENDING,
				"Who are you? Aiiieeeee!!! You're naked! Quickly, right-click on yourself and choose SET OUTFIT! If you don't I'll call the guards!",
				new MultipleActions(
						new SetQuestAction(QUEST_SLOT,0, "seen_naked"),
						new SetQuestToTimeStampAction(QUEST_SLOT,1)));

		// player is naked and has been warned,
		// they started another conversation or the init chat message prompted this interaction as above
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NakedCondition(),
						new QuestInStateCondition(QUEST_SLOT, 0, "seen_naked")),
				ConversationStates.ATTENDING,
				// this message doesn't get seen by the player himself as he gets sent to jail, but it would explain to bystanders why he is gone
				"Ugh, you STILL haven't put any clothes on. To jail for you!",
				// Jail the player
				new MultipleActions(
						new SetQuestAction(QUEST_SLOT,0, "seen_naked"),
						new SetQuestToTimeStampAction(QUEST_SLOT,1),
						new JailAction(JAIL_TIME,"Ketteh Wehoh jailed you for being naked in the town hall after warning")));

		// player was previously seen naked but is now clothed
		// continue the quest to learn manners
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NotCondition(new NakedCondition()),
						new QuestInStateCondition(QUEST_SLOT, 0,"seen_naked")),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new SayTextAction("Hi again, [name]. I'm so glad you have some clothes on now. Now we can continue with the lesson in #manners. Did you know that if someone says something in #blue it is polite to repeat it back to them? So, repeat after me: #manners." ),
						new SetQuestAction(QUEST_SLOT, "seen")));

		// normal situation: player is clothed and meets Ketteh for the first time.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NotCondition(new NakedCondition()),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new SayTextAction("Hi [name], nice to meet you. You know, we have something in common - good #manners. Did you know that if someone says something in #blue it is polite to repeat it back to them? So, repeat after me: #manners."),
						new SetQuestAction(QUEST_SLOT, "seen")));

		// player has finished the quest by learning manners or was marked as done in an old state
		// also, they are still clothed
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NotCondition(new NakedCondition()),
						new OrCondition(new QuestInStateCondition(QUEST_SLOT, "learnt_manners"),new QuestInStateCondition(QUEST_SLOT, "done"))),
				ConversationStates.ATTENDING,
				null,
				new SayTextAction("Hi again, [name]."));

		// player had started the quest but didn't finish it and are still clothed
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new NotCondition(new NakedCondition()),
						new QuestInStateCondition(QUEST_SLOT, "seen")),
				ConversationStates.ATTENDING,
				null,
				new SayTextAction("Hi again, [name]. I hope you are here to continue the lesson in #manners."));

		// player refuses to put clothes on (or just says No while naked)
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES, new NakedCondition(),
				ConversationStates.IDLE,
				"If you don't put on some clothes and leave, I shall scream for the guards!",
				null);

		// only allow quest completed if not naked
		npc.add(ConversationStates.ATTENDING, "manners",
				new NotCondition(new NakedCondition()),
				ConversationStates.ATTENDING,
				"If you happen to talk to any of the other citizens, you should always begin the conversation saying \"hi\". People here are quite predictable and will always enjoy talking about their \"job\", they will respond if you ask for \"help\" and if you want to do a \"task\" for them, just say it. If they look like the trading type, you can ask for their \"offers\". To end the conversation, just say \"bye\".",
				new SetQuestAction(QUEST_SLOT, "learnt_manners"));

		// not prompted to say this any more when naked, but just in case we don't want them to have an empty reply
		npc.add(ConversationStates.ATTENDING, "manners",
				new NakedCondition(),
				ConversationStates.ATTENDING,
				"Good manners starts with putting some clothes on! You can have the advanced lesson when you are fully dressed.",
				null);

		// this is just because the blue highlighting was used as a demonstration
		npc.add(ConversationStates.ATTENDING, "blue",
				null,
				ConversationStates.ATTENDING,
				"Oh, aren't you the clever one!",
				null);
	}



	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Meet Ketteh Wehoh",
				"An elegant lady waits in the town hall in Semos and takes care that new inhabitants of Faiumoni will walk around without freezing. She is the town Decency and Manners Warden.",
				false);
		step1();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I met Ketteh Wehoh in the Semos townhall.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.startsWith("seen_naked")) {
			res.add("She was shocked and yelled at me that I shouldn't go around naked. I better put some clothes on before she sees me again.");
		}
		if ("seen".equals(questState)) {
			res.add("She offered to teach me how to behave nicely but I need to return to finish the lesson.");
		}
		if ("done".equals(questState) || "learnt_manners".equals(questState)) {
			res.add("She has been very polite and she has taught me the basics of proper behaviour in Faiumoni.");
		}
        if (isCompleted(player)) {
            res.add("I can meet and have a chat with her again anytime. I better remain on good behaviour though!");
		}
		return res;
	}

	@Override
	public String getName() {
		return "MeetKetteh";
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new OrCondition(
            new QuestInStateCondition(QUEST_SLOT,"learnt_manners"),
            new QuestInStateCondition(QUEST_SLOT,"done")).fire(player, null, null);
	}

    @Override
    public boolean isRepeatable(final Player player) {
        return true;
    }

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Ketteh Wehoh";
	}
}
