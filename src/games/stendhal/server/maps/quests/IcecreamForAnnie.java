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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * Quest to buy ice cream for a little girl.
 * You have to get approval from her mother before giving it to her
 *
 * @author kymara
 *
 *
 * QUEST: Ice Cream for Annie
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Annie Jones (a little girl playing in Kalavan City Gardens) </li>
 * <li> Mrs Jones (Annie's mum) </li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Annie asks you for an icecream. </li>
 * <li> You buy icecream from Sam who is nearby. </li>
 * <li> Speak to Mrs Jones, Annie's mum. </li>
 * <li> Now give the icecream to Annie. </li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>a <item>present</item></li>
 * <li>500 XP</li>
 * <li>12 karma total (2 + 10)</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Every 60 minutes</li>
 * </ul>
 */
public class IcecreamForAnnie extends AbstractQuest {

	// constants
	private static final String QUEST_SLOT = "icecream_for_annie";

	/** The delay between repeating quests. */
	private static final int REQUIRED_MINUTES = 60;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void icecreamStep() {
		final SpeakerNPC npc = npcs.get("Annie Jones");

		// first conversation with annie. be like [strike]every good child[/strike] kymara was when she was little and advertise name and age.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotStartedCondition(QUEST_SLOT),
						new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
				ConversationStates.ATTENDING,
				"Hello, my name is Annie. I am five years old.",
				null);

		// player is supposed to speak to mummy now
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new PlayerHasItemWithHimCondition("icecream")),
				ConversationStates.IDLE,
				"Mummy says I mustn't talk to you any more. You're a stranger.",
				null);

		// player didn't get ice cream, meanie
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(new PlayerHasItemWithHimCondition("icecream"))),
				ConversationStates.ATTENDING,
				"Hello. I'm hungry.",
				null);

		// player got ice cream and spoke to mummy
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "mummy"),
						new PlayerHasItemWithHimCondition("icecream")),
				ConversationStates.QUESTION_1,
				"Yummy! Is that ice cream for me?",
				null);

		// player spoke to mummy and hasn't got ice cream
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "mummy"),
						new NotCondition(new PlayerHasItemWithHimCondition("icecream"))),
				ConversationStates.ATTENDING,
				"Hello. I'm hungry.",
				null);

		// player is in another state like eating
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStartedCondition(QUEST_SLOT),
						new QuestNotInStateCondition(QUEST_SLOT, "start"),
						new QuestNotInStateCondition(QUEST_SLOT, "mummy")),
				ConversationStates.ATTENDING,
				"Hello.",
				null);

		// player rejected quest
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "rejected")),
				ConversationStates.ATTENDING,
				"Hello.",
				null);

		// player asks about quest for first time (or rejected)
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I'm hungry! I'd like an ice cream, please. Vanilla, with a chocolate flake. Will you get me one?",
				null);

		// shouldn't happen
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I'm full up now thank you!",
				null);

		// player can repeat quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "eating;"), new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"I hope another ice cream wouldn't be greedy. Can you get me one?",
				null);

		// player can't repeat quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "eating;"), new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				"I've had too much ice cream. I feel sick.",
				null);

		// player should be bringing ice cream not asking about the quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "eating;"))),
				ConversationStates.ATTENDING,
				"Waaaaaaaa! Where is my ice cream ....",
				null);

		// Player agrees to get the ice cream
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 2.0));

		// Player says no, they've lost karma
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Ok, I'll ask my mummy instead.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// Player has got ice cream and spoken to mummy
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("icecream"));
		reward.add(new EquipItemAction("present"));
		reward.add(new IncreaseXPAction(500));
		reward.add(new SetQuestAction(QUEST_SLOT, "eating;"));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT,1));
		reward.add(new IncreaseKarmaAction(10.0));
		reward.add(new InflictStatusOnNPCAction("icecream"));

		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("icecream"),
				ConversationStates.ATTENDING,
				"Thank you EVER so much! You are very kind. Here, take this present.",
				new MultipleActions(reward));

		// player did have ice cream but put it on ground after question?
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("icecream")),
				ConversationStates.ATTENDING,
				"Hey, where's my ice cream gone?!",
				null);

		// Player says no, they've lost karma
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Waaaaaa! You're a big fat meanie.",
				new DecreaseKarmaAction(5.0));
	}

	private void meetMummyStep() {
		final SpeakerNPC mummyNPC = npcs.get("Mrs Jones");

		// player speaks to mummy before annie
		mummyNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(mummyNPC.getName()),
							new QuestNotStartedCondition(QUEST_SLOT)),
					ConversationStates.ATTENDING, "Hello, nice to meet you.",
					null);

		// player is supposed to begetting ice cream
		mummyNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(mummyNPC.getName()),
							new QuestInStateCondition(QUEST_SLOT, "start")),
					ConversationStates.ATTENDING,
					"Hello, I see you've met my daughter Annie. I hope she wasn't too demanding. You seem like a nice person.",
					new SetQuestAction(QUEST_SLOT, "mummy"));

		// any other state
		mummyNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new GreetingMatchesNameCondition(mummyNPC.getName()), true,
					ConversationStates.ATTENDING, "Hello again.", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Ice Cream for Annie",
				"The best surprise for a litte girl like Annie is a cool ice cream on summer days while playing on the playground.",
				true);
		icecreamStep();
		meetMummyStep();
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Annie Jones is a sweet little girl playing in Kalavan city gardens.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I don't like sweet little girls.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start","mummy") || isCompleted(player)) {
			res.add("Little Annie wants an ice cream.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start","mummy") && player.isEquipped("icecream") || isCompleted(player)) {
			res.add("I found a tasty ice cream for Annie.");
		}
        if ("mummy".equals(questState) || isCompleted(player)) {
            res.add("I spoke to Mrs Jones, she agreed I could give an ice cream to her daughter.");
        }
        if (isCompleted(player)) {
            if (isRepeatable(player)) {
                res.add("I took ice cream to Annie, she gave me a present. Perhaps she'd like another now.");
            } else {
                res.add("Annie is eating the ice cream I gave her, and she gave me a present in return.");
            }
		}
		return res;
	}

	@Override
	public String getName() {
		return "IcecreamForAnnie";
	}

	// Getting to Kalavan is not too feasible till this level
	@Override
	public int getMinLevel() {
		return 10;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"eating;"),
				 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player,null, null);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"eating;").fire(player, null, null);
	}

	@Override
	public String getRegion() {
		return Region.KALAVAN;
	}
	@Override
	public String getNPCName() {
		return "Annie Jones";
	}
}
