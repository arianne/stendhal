/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;


/**
 * QUEST: KillBlordroughs
 *
 * PARTICIPANTS: <ul>
 * <li> Mrotho
 * <li> some creatures
 * </ul>
 *
 * STEPS:<ul>
 * <li> Mrotho asking you to kill 100 blordrough warriors.
 * <li>
 * <li> Kill them and go back to Mrotho for your reward.
 * </ul>
 *
 *
 * REWARD:<ul>
 * <li> 500k XP
 * <li> 50k moneys
 * <li> 5 karma for killing 100 creatures
 * <li> 5 karma for killing every 50 next creatures
 * </ul>
 *
 * REPETITIONS: <ul><li> once a week.</ul>
 */

public class KillBlordroughs extends AbstractQuest {

	private static KillBlordroughs instance;

	private static final String QUEST_NPC = "Mrotho";
	private static final String QUEST_SLOT = "kill_blordroughs";
	private final long questdelay = MathHelper.MILLISECONDS_IN_ONE_WEEK;
	protected final int killsnumber = 100;
	private SpeakerNPC npc;
	private static Logger logger = Logger.getLogger(KillBlordroughs.class);

	protected static List<String> BLORDROUGHS = Arrays.asList(
			"blordrough quartermaster",
			"blordrough corporal",
			"blordrough storm trooper",
			"blordrough soldier",
			"blordrough elite",
			"blordrough infantry",
			"blordrough captain",
			"blordrough general"
			);


	/**
	 * Get the static instance.
	 *
	 * @return
	 * 		KillBlordroughs
	 */
	public static KillBlordroughs getInstance() {
		if (instance == null) {
			instance = new KillBlordroughs();
		}

		return instance;
	}

	@Override
	public int getMinLevel() {
		return 114; // level of weakest blordrough
	}

	/**
	 * function returns list of blordrough creatures.
	 * @return - list of blordrough creatures
	 */
	protected LinkedList<Creature> getBlordroughs() {
		LinkedList<Creature> blordroughs = new LinkedList<Creature>();
		final EntityManager manager = SingletonRepository.getEntityManager();
		for (int i=0; i<BLORDROUGHS.size(); i++) {
			Creature creature = manager.getCreature(BLORDROUGHS.get(i));
			if (!creature.isAbnormal()) {
				blordroughs.add(creature);
			}
		}
		return blordroughs;
	}

	/**
	 * function checking if quest is active for player or no.
	 * @param player - player for who we will check quest state.
	 * @return - true if player's quest is active.
	 */
	private boolean questInProgress(final Player player) {
		if(player.getQuest(QUEST_SLOT)!=null) {
			return !player.getQuest(QUEST_SLOT,0).equals("done");
		}
		return false;
	}

	/**
	 * function decides, if quest can be given to player
	 * @param player - player for which we will check quest slot
	 * @param currenttime
	 * @return - true if player can get quest.
	 */
	private boolean questCanBeGiven(final Player player, final Long currenttime) {
		if (!player.hasQuest(QUEST_SLOT)) {
			return true;
		}
		if (player.getQuest(QUEST_SLOT, 0).equals("done")) {
			final String questLast = player.getQuest(QUEST_SLOT, 1);
			final Long time = currenttime -
				Long.parseLong(questLast);
			if (time > questdelay) {
				return true;
			}
		}
		return false;
	}

	/**
	 * function will return NPC answer how much time remains.
	 * @param player - chatting player.
	 * @param currenttime
	 * @return - NPC's reply string
	 */
	private String getNPCTextReply(final Player player, final Long currenttime) {
		String reply = "";
		String questLast = player.getQuest(QUEST_SLOT, 1);
		if (questLast != null) {
			final long timeRemaining = Long.parseLong(questLast) +
					questdelay - currenttime;

			if (timeRemaining > 0) {
				reply = "Please check back in "
						+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
						+ ".";
			} else {
				// something wrong.
				reply = "I dont want to decide about you now.";
				logger.error("wrong time count	for player "+player.getName()+": "+
						"current time is "+currenttime+
						", last quest time is "+questLast,
						new Throwable());
			}
		}
		return reply;
	}

	/**
	 * function returns difference between recorded number of blordrough creatures
	 *     and currently killed creatures numbers.
	 * @param player - player for who we counting this
	 * @return - number of killed blordrough creatures
	 */
	private int getKilledCreaturesNumber(final Player player) {
		int count = 0;
		String temp;
		int solo;
		int shared;
		int recsolo;
		int recshared;
		final LinkedList<Creature> blordroughs = getBlordroughs();
		for(int i=0; i<blordroughs.size(); i++) {
			String tempName = blordroughs.get(i).getName();
			temp = player.getQuest(QUEST_SLOT, 1+i*2);
			if (temp == null) {
				recsolo = 0;
			} else if (temp.equals("")) {
				recsolo = 0;
			} else if (temp.startsWith("completed=")) {
				recsolo = 0;
			} else {
				recsolo = Integer.parseInt(temp);
			}
			temp = player.getQuest(QUEST_SLOT, 2+i*2);
			if (temp == null) {
				recshared = 0;
			} else if (temp.equals("")) {
				recshared = 0;
			} else if (temp.startsWith("completed=")) {
				recshared = 0;
			} else {
				recshared = Integer.parseInt(temp);
			}

			temp = player.getKeyedSlot("!kills", "solo."+tempName);
			if (temp==null) {
				solo = 0;
			} else {
				solo = Integer.parseInt(temp);
			}

			temp = player.getKeyedSlot("!kills", "shared."+tempName);
			if (temp==null) {
				shared = 0;
			} else {
				shared = Integer.parseInt(temp);
			}

			count = count + solo - recsolo + shared - recshared;
		}
		return count;
	}

	/**
	 * function will update player quest slot.
	 * @param player - player for which we will record quest.
	 */
	private void writeQuestRecord(final Player player) {
		StringBuilder sb = new StringBuilder();
		LinkedList<Creature> sortedcreatures = getBlordroughs();
		sb.append("given");
		for (int i=0; i<sortedcreatures.size(); i++) {
			String temp;
			int solo;
			int shared;
			temp = player.getKeyedSlot("!kills", "solo."+sortedcreatures.get(i).getName());
			if (temp==null) {
				solo = 0;
			} else {
				solo = Integer.parseInt(temp);
			}

			temp = player.getKeyedSlot("!kills", "shared."+sortedcreatures.get(i).getName());
			if (temp==null) {
				shared = 0;
			} else {
				shared = Integer.parseInt(temp);
			}

			sb.append(";" + solo);
			sb.append(";" + shared);
		}

		sb.append(";completed=" + getCompletedCount(player));

		//player.sendPrivateText(sb.toString());
		player.setQuest(QUEST_SLOT, sb.toString());
	}

	/**
	 * function will complete quest and reward player.
	 * @param player - player to be rewarded.
	 * @param killed - number of killed creatures.
	 */
	private void rewardPlayer(final Player player, int killed) {
		int karmabonus = 5*(2*killed/killsnumber-1);
		final StackableItem money = (StackableItem) SingletonRepository.getEntityManager()
			.getItem("money");
		money.setQuantity(50000);

		player.setQuest(QUEST_SLOT, "done;" + System.currentTimeMillis() + ";completed=" + Integer.toString(getCompletedCount(player) + 1));
		player.equipOrPutOnGround(money);
		player.addKarma(karmabonus);
		player.addXP(500000);
	}

	/**
	 * Checks how many times the player has completed the quest.
	 *
	 * @param player
	 * 		Player to check.
	 * @return
	 * 		Number of times player has completed quest.
	 */
	public int getCompletedCount(final Player player) {
		if (player.getQuest(QUEST_SLOT) != null) {
			final String[] slots = player.getQuest(QUEST_SLOT).split(";");

			final String temp = slots[slots.length - 1];
			if (temp.startsWith("completed=")) {
				return Integer.parseInt(temp.split("=")[1]);
			}

			// completion count was not previously tracked, so check if quest has been completed at least once
			if (slots[0].equals("done")) {
				return 1;
			}
		}

		return 0;
	}

	/**
	 * class for quest talking.
	 */
	class QuestAction implements ChatAction {

		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if(questInProgress(player)) {
				int killed = getKilledCreaturesNumber(player);

				if(killed==0) {
					// player killed no creatures but asked about quest again.
					npc.say("You have to kill #blordroughs, remember?");
					return;
				}
				if(killed < killsnumber) {
					// player killed less then needed soldiers.
					npc.say("You killed only "+killed+" blordrough "+Grammar.plnoun(killed, "soldier")+".");
					return;
				}
				if(killed == killsnumber) {
					// player killed no more no less then needed soldiers
					npc.say("Good work! Take this money. And if you need an assassin job again, ask me in one week. I think they will try to fight our army again.");
				} else {
					// player killed more then needed soldiers
					npc.say("Pretty good! You killed "+(killed-killsnumber)+" extra "+
							Grammar.plnoun(killed-killsnumber, "soldier")+"! Take this money, and remember, I may wish you to do this job again in one week!");
				}
				rewardPlayer(player, killed);
			} else {
				final Long currtime = System.currentTimeMillis();
				if (questCanBeGiven(player, currtime)) {
					// will give quest to player.
					npc.say("Ados army needs help in battles with #Blordrough warriors. They really annoy us. Kill at least 100 of any blordrough warriors and you will get a reward.");
					writeQuestRecord(player);
				} else {
					npc.say(getNPCTextReply(player, currtime));
				}
			}
		}
	}

	/**
	 * add quest state to npc's fsm.
	 */
	private void step_1() {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new GreetingMatchesNameCondition(npc.getName()),
				false,
				ConversationStates.ATTENDING,
				"Greetings. Have you come to enlist as a soldier?",
				null);
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				new GreetingMatchesNameCondition(npc.getName()),
				false,
				ConversationStates.ATTENDING,
				"Huh! Well, I would give you a #quest then...",
				null);
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				new GreetingMatchesNameCondition(npc.getName()),
				false,
				ConversationStates.ATTENDING,
				"Good! You wouldn't have fit in here anyway. Perhaps you want to #offer some of that armor instead...",
				null);
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("Blordrough","blordrough","blordroughs"),
				null,
				ConversationStates.ATTENDING,
				"Ados army has great losses in battles with Blordrough soldiers. They are coming from the side of Ados tunnels.",
				null);
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new GreetingMatchesNameCondition(npc.getName()),
				ConversationStates.ATTENDING,
				null,
				new QuestAction());

		// compatibility so players can say "done"
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				null,
				new QuestAction());
	}

	/**
	 * add quest to the Stendhal world.
	 */
	@Override
	public void addToWorld() {
		npc = npcs.get(QUEST_NPC);
		fillQuestInfo(
				"Kill Blordroughs",
				"Mrotho wants some Blordrough warriors killed.",
				true);
		step_1();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final int completedCount = getCompletedCount(player);

		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
				return res;
		}
		res.add("I have met Mrotho in Ados barracks.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.contains("done")) {
			res.add("I killed blordroughs and got a reward from " + QUEST_NPC);
		} else {
			res.add("I have killed " + Integer.toString(getKilledCreaturesNumber(player)) + " blordroughs (need " + Integer.toString(killsnumber) + ").");
		}

		if (completedCount > 0) {
			res.add("I have slain " + Integer.toString(completedCount) + " blordrough armies.");
		}

        return res;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(
				new QuestCompletedCondition(QUEST_SLOT),
				new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK)).fire(player, null, null);
	}

	/**
	 * return name of quest slot.
	 */
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/**
	 * return name of quest.
	 */
	@Override
	public String getName() {
		return "KillBlordroughs";
	}

	@Override
	public String getNPCName() {
		return "Mrotho";
	}
}
