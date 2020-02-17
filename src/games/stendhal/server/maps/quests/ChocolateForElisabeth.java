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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
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
 * Quest to buy chocolate for a little girl called Elisabeth.
 * Ask her mother Carey for a quest and she will ask you to get some chocolate for her daughter.
 * Get some chocolate and bring it to Elisabeth.
 *
 * @author Vanessa Julius idea by miasma
 *
 *
 * QUEST: Chocolate for Elisabeth
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Elisabeth (a young girl who loves chocolate)</li>
 * <li>Carey (Elisabeth's mother)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Elisabeth asks you to bring her a chocolate bar.</li>
 * <li>Get some chocolate .</li>
 * <li>Ask Carey if she allows you to give the chocolate to her daughter.</li>
 * <li>Make Elisabeth happy and get a lovely reward.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>a random flower</li>
 * <li>500 XP</li>
 * <li>12 karma total (2 + 10)</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Every 60 minutes</li>
 * </ul>
 */
public class ChocolateForElisabeth extends AbstractQuest {

	// constants
	private static final String QUEST_SLOT = "chocolate_for_elisabeth";

	/** The delay between repeating quests. */
	private static final int REQUIRED_MINUTES = 60;
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void chocolateStep() {
		final SpeakerNPC npc = npcs.get("Elisabeth");

		// first conversation with Elisabeth.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotStartedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
				ConversationStates.ATTENDING,
				"I can't remember when I smelt the good taste of #chocolate the last time...",
				null);

		npc.addReply("chocolate", "My mom told me, that chocolate can be found in an assassin school, which is quite #dangerous. She said also that someone sells it in Ados...");

		npc.addReply("dangerous", "Some bandits wait on the road to the school and assassins guard the way there, so mom and I have to stay in Kirdneh because it's safe here...");

		// player is supposed to speak to mummy now
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("chocolate bar")),
				ConversationStates.IDLE,
				"My mum wants to know who I was asking for chocolate from now :(",
				null);

		// player didn't get chocolate, meanie
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("chocolate bar"))),
				ConversationStates.ATTENDING,
				"I hope that someone will bring me some chocolate soon...:(",
				null);

		// player got chocolate and spoke to mummy
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "mummy"), new PlayerHasItemWithHimCondition("chocolate bar")),
				ConversationStates.QUESTION_1,
				"Awesome! Is that chocolate for me?",
				null);

		// player spoke to mummy and hasn't got chocolate
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "mummy"), new NotCondition(new PlayerHasItemWithHimCondition("chocolate bar"))),
				ConversationStates.ATTENDING,
				"I hope that someone will bring me some chocolate soon...:(",
				null);

		// player is in another state like eating
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStartedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestNotInStateCondition(QUEST_SLOT, "mummy")),
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
				"I would really love to have some chocolate. I'd like one bar, please. A dark brown one or a sweet white one or some with flakes. Will you get me one?",
				null);

		// shouldn't happen
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I still enjoy the last chocolate bar you brought me, thanks!",
				null);

		// player can repeat quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "eating;"), new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"I hope another chocolate bar wouldn't be greedy. Can you get me another one?",
				null);

		// player can't repeat quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "eating;"), new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				"I've had too much chocolate. I feel sick.",
				null);

		// player should be bringing chocolate not asking about the quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "eating;"))),
				ConversationStates.ATTENDING,
				"Waaaaaaaa! Where is my chocolate ...",
				null);

		// Player agrees to get the chocolate
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
				"Ok, I'll wait till mommy finds some helpers...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// Player has got chocolate bar and spoken to mummy
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("chocolate bar"));
		reward.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				// pick a random flower
				String rewardClass = Rand.rand(Arrays.asList("daisies","zantedeschia","pansy"));

				final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem(rewardClass);
				item.setQuantity(1);
				player.equipOrPutOnGround(item);
				player.notifyWorldAboutChanges();
			}
		});
		reward.add(new IncreaseXPAction(500));
		reward.add(new SetQuestAction(QUEST_SLOT, "eating;"));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT,1));
		reward.add(new IncreaseKarmaAction(10.0));
		reward.add(new InflictStatusOnNPCAction("chocolate bar"));

		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("chocolate bar"),
				ConversationStates.ATTENDING,
				"Thank you EVER so much! You are very kind. Here, take a fresh flower as a present.",
				new MultipleActions(reward));


		// player did have chocolate but put it on ground after question?
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("chocolate bar")),
				ConversationStates.ATTENDING,
				"Hey, where's my chocolate gone?!",
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
		final SpeakerNPC mummyNPC = npcs.get("Carey");

		// player speaks to mummy before Elisabeth
		mummyNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(mummyNPC.getName()),
							new QuestNotStartedCondition(QUEST_SLOT)),
					ConversationStates.ATTENDING, "Hello, nice to meet you.",
					null);

		// player is supposed to begetting chocolate
		mummyNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(mummyNPC.getName()),
							new QuestInStateCondition(QUEST_SLOT, "start")),
					ConversationStates.ATTENDING,
					"Oh you met my daughter Elisabeth already. You seem like a nice person so it would be really kind, if you can bring her a chocolate bar because I'm not #strong enough for that.",
					new SetQuestAction(QUEST_SLOT, "mummy"));

		mummyNPC.addReply("strong", "I tried to get some chocolate for Elisabeth a few times, but I couldn't make my way through the assassins and bandits running around #there.");

		mummyNPC.addReply("there", "They live in and around the Ados castle. Take care there! I also heard about #someone who sells chocolate bars.");

		mummyNPC.addReply("someone", "I never visited that guy because he seems to be really... well he works somewhere where I don't want to be in Ados.");

		// any other state
		mummyNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES, new GreetingMatchesNameCondition(mummyNPC.getName()), true,
					ConversationStates.ATTENDING, "Hello again.", null);
	}
	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Chocolate for Elisabeth",
				"Sweet, sweet chocolate! No one can live without it! And Elisabeth loooves to have some...",
				true);
		chocolateStep();
		meetMummyStep();
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Elisabeth is a sweet little girl who lives in Kirdneh together with her family.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I don't like sweet little girls.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start","mummy") || isCompleted(player)) {
			res.add("Little Elisabeth wants a chocolate bar.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start","mummy") && player.isEquipped("chocolate bar") || isCompleted(player)) {
			res.add("I found a tasty chocolate bar for Elisabeth.");
		}
        if ("mummy".equals(questState) || isCompleted(player)) {
            res.add("I spoke to Carey, Elisabeth's mom and she agreed I could give a chocolate bar to her daughter.");
        }
        if (isCompleted(player)) {
            if (isRepeatable(player)) {
                res.add("I took some chocolate to Elisabeth, she gave me some flowers in return. Perhaps she'd like more chocolate now.");
            } else {
                res.add("Elisabeth is eating the chocolate bar I gave her, and she gave me some flowers in return.");
            }
		}
		return res;
	}
	@Override
	public String getName() {
		return "ChocolateForElisabeth";
	}

	// Getting to Kirdneh is not too feasible till this level
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
		return Region.KIRDNEH;
	}
	@Override
	public String getNPCName() {
		return "Elisabeth";
	}
}
