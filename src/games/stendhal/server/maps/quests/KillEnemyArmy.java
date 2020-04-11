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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledInSumForQuestCondition;
import games.stendhal.server.entity.npc.condition.KillsQuestSlotNeedUpdateCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import marauroa.common.Pair;


/**
 * QUEST: KillEnemyArmy
 *
 * PARTICIPANTS: <ul>
 * <li> Despot Halb Errvl
 * <li> some creatures
 * </ul>
 *
 * STEPS:<ul>
 * <li> Despot asking you to kill some of enemy forces.
 * <li> Kill them and go back to Despot for your reward.
 * </ul>
 *
 *
 * REWARD:<ul>
 * <li> 100k of XP, or 300 karma.
 * <li> random moneys - from 10k to 60k, step 10k.
 * <li> 5 karma for killing 100% creatures
 * <li> 5 karma for killing every 50% next creatures
 * </ul>
 *
 * REPETITIONS: <ul><li> once a week.</ul>
 */

 public class KillEnemyArmy extends AbstractQuest {

	private static final String QUEST_NPC = "Despot Halb Errvl";
	private static final String QUEST_SLOT = "kill_enemy_army";
	private static final int delay = MathHelper.MINUTES_IN_ONE_WEEK;

	protected HashMap<String, Pair<Integer, String>> enemyForces = new HashMap<String, Pair<Integer,String>>();
	protected HashMap<String, List<String>> enemys = new HashMap<String, List<String>>();




	public KillEnemyArmy() {
		super();
		// fill monster types map
		enemyForces.put("blordrough",
				new Pair<Integer, String>(50,"Blordrough warriors now live in the Ados tunnels. They are extremely strong in battle, that is why Blordrough captured part of Deniran's territory."));
		enemyForces.put("madaram",
				new Pair<Integer, String>(100,"Their forces are somewhere under Fado. They are hideous."));
		enemyForces.put("dark elf",
				new Pair<Integer, String>(100,"Drows, or dark elves as they are commonly called, can be found under Nalwor. They use poison in battles, gathering it from different poisonous creatures."));
		enemyForces.put("chaos",
				new Pair<Integer, String>(150,"They are strong and crazy. Only my elite archers hold them from expanding more."));
		enemyForces.put("mountain dwarf",
				new Pair<Integer, String>(150,"They are my historical neighbors, living in Semos mines."));
		enemyForces.put("mountain orc",
				new Pair<Integer, String>(150,"Stupid creatures, but very strong. Can be found in an abandoned underground keep somewhere near Ados."));
		enemyForces.put("imperial",
				new Pair<Integer, String>(200,"They come from their castle in the underground Sedah city, ruled by their Emperor Dalmung."));
		enemyForces.put("barbarian",
				new Pair<Integer, String>(200,"Different barbarian tribes live on the surface in the North West area of Ados Mountains. Not dangerous but noisy."));
		enemyForces.put("oni",
				new Pair<Integer, String>(200,"Very strange race, living in their castle in Fado forest. There are rumors that they have agreed an alliance with the Magic city wizards."));

		/*
		 * those are not interesting
		enemyForces.put("dwarf",
				new Pair<Integer, String>(275,""));
		enemyForces.put("elf",
				new Pair<Integer, String>(300,""));
		enemyForces.put("skeleton",
				new Pair<Integer, String>(500,""));
		enemyForces.put("gnome",
				new Pair<Integer, String>(1000,""));
		*/

		/*
		 *  fill creatures map
		 */

		enemys.put("blordrough",
				Arrays.asList("blordrough quartermaster",
							  "blordrough corporal",
							  "blordrough storm trooper",
							  "blordrough soldier",
							  "blordrough elite",
							  "blordrough infantry",
							  "blordrough captain",
							  "blordrough general"));
		enemys.put("dark elf",
				Arrays.asList("child dark elf",
							  "dark elf archer",
							  "dark elf",
							  "dark elf elite archer",
							  "dark elf captain",
							  "dark elf knight",
							  "dark elf general",
							  "dark elf wizard",
							  "dark elf viceroy",
							  "dark elf sacerdotist",
							  "dark elf admiral",
							  "dark elf master",
							  "dark elf matronmother"));
		enemys.put("chaos",
				Arrays.asList("chaos soldier",
							  "chaos warrior",
							  "chaos commander",
							  "chaos sorcerer",
							  "chaos dragonrider",
							  "chaos lord",
							  "chaos green dragonrider",
							  "chaos overlord",
							  "chaos red dragonrider"));
		enemys.put("mountain dwarf",
				Arrays.asList("mountain dwarf",
							  "mountain elder dwarf",
							  "mountain dwarf guardian",
							  "mountain hero dwarf",
							  "mountain leader dwarf",
							  "Dhohr Nuggetcutter",
							  "giant dwarf",
							  "dwarf golem"));
		enemys.put("mountain orc",
				Arrays.asList("mountain orc",
							  "mountain orc warrior",
							  "mountain orc hunter",
							  "mountain orc chief"));
		enemys.put("imperial",
				Arrays.asList("imperial defender",
							  "imperial veteran",
							  "imperial archer",
							  "imperial priest",
							  "imperial elite guardian",
							  "imperial scientist",
							  "imperial high priest",
							  "imperial archer leader",
							  "imperial elite archer",
							  "imperial leader",
							  "imperial chief",
							  "imperial knight",
							  "imperial commander",
							  "imperial experiment",
							  "imperial demon servant",
							  "imperial mutant",
							  "imperial general",
							  "imperial demon lord",
							  "emperor dalmung",
							  "imperial general giant"));
		enemys.put("madaram",
				Arrays.asList("madaram peasant",
							  "madaram trooper",
							  "madaram soldier",
							  "madaram healer",
							  "madaram axeman",
							  "madaram queen",
							  "madaram hero",
							  "madaram cavalry",
							  "madaram stalker",
							  "madaram buster blader",
							  "madaram archer",
							  "madaram windwalker",
							  "kasarkutominubat"));
		/*
		 * exclude amazoness ( because they dont want to leave their island? )
		enemys.put("amazoness",
				Arrays.asList("amazoness archer",
						      "amazoness hunter",
						      "amazoness coastguard",
						      "amazoness archer commander",
						      "amazoness elite coastguard",
						      "amazoness bodyguard",
						      "amazoness coastguard mistress",
						      "amazoness commander",
						      "amazoness vigilance",
						      "amazoness imperator",
						      "amazoness giant"));
		 */
		enemys.put("oni",
				Arrays.asList("oni warrior",
							  "oni archer",
							  "oni priest",
							  "oni king",
							  "oni queen"));
		enemys.put("barbarian",
				Arrays.asList("barbarian",
						      "barbarian wolf",
						      "barbarian elite",
						      "barbarian priest",
						      "barbarian chaman",
						      "barbarian leader",
						      "barbarian king"));
	}

	/**
	 * function for choosing random enemy from map
	 * @return - enemy forces caption
	 */
	protected String chooseRandomEnemys() {
		final List<String> enemyList = new LinkedList<String>(enemyForces.keySet());
		final int enemySize = enemyList.size();
		final int position  = Rand.rand(enemySize);
		return enemyList.get(position);
	}

	/**
	 * function returns difference between recorded number of enemy creatures
	 *     and currently killed creatures numbers.
	 * @param player - player for who we counting this
	 * @return - number of killed enemy creatures
	 */
	private int getKilledCreaturesNumber(final Player player) {
		int count = 0;
		String temp;
		int solo;
		int shared;
		int recsolo;
		int recshared;
		final String enemyType = player.getQuest(QUEST_SLOT,1);
		final List<String> monsters = Arrays.asList(player.getQuest(QUEST_SLOT,2).split(","));
		final List<String> creatures = enemys.get(enemyType);
		for(int i=0; i<creatures.size(); i++) {
			String tempName = creatures.get(i);
			temp = monsters.get(i*5+3);
			if (temp == null) {
				recsolo = 0;
			} else {
				recsolo = Integer.parseInt(temp);
			}
			temp = monsters.get(i*5+4);
			if (temp == null) {
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


	class GiveQuestAction implements ChatAction {
		/**
		 * function will update player quest slot.
		 * @param player - player for which we will record quest.
		 */
		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser speakerNPC) {
			final String monstersType = chooseRandomEnemys();
			speakerNPC.say("I need help to defeat #enemy " + monstersType +
					" armies. They are a grave concern. Kill at least " + enemyForces.get(monstersType).first()+
					" of any "+ monstersType +
					" soldiers and I will reward you.");
			final HashMap<String, Pair<Integer, Integer>> toKill = new HashMap<String, Pair<Integer, Integer>>();
			List<String> sortedcreatures = enemys.get(monstersType);
			player.setQuest(QUEST_SLOT, 0, "start");
			player.setQuest(QUEST_SLOT, 1, monstersType);
			for(int i=0; i<sortedcreatures.size(); i++) {
				toKill.put(sortedcreatures.get(i), new Pair<Integer, Integer>(0,0));
			}
			new StartRecordingKillsAction(QUEST_SLOT, 2, toKill).fire(player, sentence, speakerNPC);
		}
	}

	class RewardPlayerAction implements ChatAction {
		/**
		 * function will complete quest and reward player.
		 * @param player - player to be rewarded.
		 */
		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser speakerNPC) {
			final String monsters = player.getQuest(QUEST_SLOT, 1);
			int killed=getKilledCreaturesNumber(player);
			int killsnumber = enemyForces.get(monsters).first();
			int moneyreward = 10000*Rand.roll1D6();
			if(killed == killsnumber) {
				// player killed no more no less then needed soldiers
				speakerNPC.say("Good work! Take these " + moneyreward + " coins. And if you need an assassin job again, ask me in one week. My advisors tell me they may try to fight me again.");
			} else {
				// player killed more then needed soldiers
				speakerNPC.say("Pretty good! You killed "+(killed-killsnumber)+" extra "+
						Grammar.plnoun(killed-killsnumber, "soldier")+"! Take these " + moneyreward + " coins, and remember, I may wish you to do this job again in one week!");
			}
			int karmabonus = 5*(2*killed/killsnumber-1);
			final StackableItem money = (StackableItem)
					SingletonRepository.getEntityManager().getItem("money");
			money.setQuantity(moneyreward);

			player.equipOrPutOnGround(money);
			player.addKarma(karmabonus);

		}
	}



	/**
	 * class for quest talking.
	 */
	class ExplainAction implements ChatAction {

		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
				final String monsters = player.getQuest(QUEST_SLOT, 1);
				int killed=getKilledCreaturesNumber(player);
				int killsnumber = enemyForces.get(monsters).first();

				if(killed==0) {
					// player killed no creatures but asked about quest again.
					npc.say("I already explained to you what I need. Are you an idiot, as you can't remember this simple thing about the #enemy " + monsters + " armies?");
					return;
				}
				if(killed < killsnumber) {
					// player killed less then needed soldiers.
					npc.say("You killed only "+killed+" "+Grammar.plnoun(killed, player.getQuest(QUEST_SLOT, 1))+
							". You have to kill at least "+killsnumber+" "+Grammar.plnoun(killed, player.getQuest(QUEST_SLOT, 1)));
					return;
				}

		}
	}

	/**
	 * class for quest talking.
	 */
	class FixAction implements ChatAction {

		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
				//final String monsters = player.getQuest(QUEST_SLOT, 1);
			    Logger.getLogger(KillEnemyArmy.class).warn("Fixing malformed quest string of player <"+
				                                            player.getName()+
				                                            ">: ("+
				                                            player.getQuest(QUEST_SLOT)+
				                                            ")");
				npc.say("I am sorry, I did not pay attention. " +
						"What I need now:");
				new GiveQuestAction().fire(player, sentence, npc);
		}
	}


	/**
	 * add quest state to npc's fsm.
	 */
	private void step_1() {

		SpeakerNPC npc = npcs.get(QUEST_NPC);

		// quest can be given
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(
					new QuestNotStartedCondition(QUEST_SLOT),
					new AndCondition(
						new QuestCompletedCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT, 1, delay))),
				ConversationStates.ATTENDING,
				null,
				new GiveQuestAction());

		// time is not over
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestCompletedCondition(QUEST_SLOT),
						new NotCondition(
								new TimePassedCondition(QUEST_SLOT, 1, delay))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, delay, "You have to check again in"));

		// explanations
		npc.add(ConversationStates.ATTENDING,
				"enemy",
				new QuestInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							npc.say(enemyForces.get(player.getQuest(QUEST_SLOT, 1)).second());
						}
				});

		// explanations
		npc.add(ConversationStates.ATTENDING,
				"enemy",
				new QuestNotInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				"Yes, my enemies are everywhere, they want to kill me! I guess you are one of them. Stay away from me!",
				null);

		// update player's quest slot or blank it if failed...
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new KillsQuestSlotNeedUpdateCondition(QUEST_SLOT, 1, enemys, true)),
				ConversationStates.ATTENDING,
				null,
				new FixAction());

		// checking for kills
		final List<String> creatures = new LinkedList<String>(enemyForces.keySet());
		for(int i=0; i<enemyForces.size(); i++) {
			final String enemy = creatures.get(i);

			  // player killed enough enemies.
		      npc.add(ConversationStates.ATTENDING,
		    		  ConversationPhrases.QUEST_FINISH_MESSAGES,
		    		  new AndCondition(
		    				  new QuestInStateCondition(QUEST_SLOT, 1, enemy),
		    				  new KilledInSumForQuestCondition(QUEST_SLOT, 2, enemyForces.get(enemy).first())),
		    		  ConversationStates.ATTENDING,
		    		  null,
		    		  new MultipleActions(
		    				  new RewardPlayerAction(),
		    				  new IncreaseXPAction(100000),
		    				  new IncrementQuestAction(QUEST_SLOT,3,1),
		    				  // empty the 2nd index as we use it later
		    				  new SetQuestAction(QUEST_SLOT,2,""),
		    				  new SetQuestToTimeStampAction(QUEST_SLOT,1),
		    				  new SetQuestAction(QUEST_SLOT,0,"done")));

		      // player killed not enough enemies.
		      npc.add(ConversationStates.ATTENDING,
		    		  ConversationPhrases.QUEST_FINISH_MESSAGES,
		    		  new AndCondition(
		    				  new QuestInStateCondition(QUEST_SLOT, 1, enemy),
		    				  new NotCondition(
		    						  new KilledInSumForQuestCondition(QUEST_SLOT, 2, enemyForces.get(enemy).first()))),
		    		  ConversationStates.ATTENDING,
		    		  null,
		    		  new ExplainAction());

		}
	}

	/**
	 * add quest to the Stendhal world.
	 */
	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Kill Enemy Army",
				"Despot Halb Errvl has a vendetta against any army who opposes him.",
				true);
		step_1();
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
		return "KillEnemyArmy";
	}

	@Override
	public int getMinLevel() {
		return 80;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,delay)).fire(player, null, null);
	}

 	@Override
 	public List<String> getHistory(final Player player) {
 		LinkedList<String> history = new LinkedList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return history;
		}

		if(player.getQuest(QUEST_SLOT, 0).equals("start")) {
	        final String givenEnemies = player.getQuest(QUEST_SLOT, 1);
	        final int givenNumber = enemyForces.get(givenEnemies).first();
	        // updating firstly
			if(new KillsQuestSlotNeedUpdateCondition(QUEST_SLOT, 2, enemys.get(givenEnemies), true).fire(player, null, null)) {
				// still need update??
			}
	        final int killedNumber = getKilledCreaturesNumber(player);

			history.add("Despot Halb Errvl asked me to kill "+
					givenNumber+" "+
					Grammar.plnoun(givenNumber, givenEnemies));
			String kn = Integer.valueOf(killedNumber).toString();
			if(killedNumber == 0) {
				kn="no";
			}
			history.add("Currently I have killed "+
					kn+" "+
					Grammar.plnoun(killedNumber, givenEnemies));
			if(new KilledInSumForQuestCondition(QUEST_SLOT, 2, givenNumber).fire(player, null, null)) {
				history.add("I have killed enough creatures to get my reward now.");
			} else {
				history.add(givenNumber-killedNumber+" "+
						Grammar.plnoun(givenNumber-killedNumber, givenEnemies)+" left to kill.");
			}
		}

		if(isCompleted(player)) {
			history.add("I completed Despot's Halb Errvl task and got my reward!");
		}
		if (isRepeatable(player)) {
			history.add("Despot Halb Errvl is getting paranoid again about his safety, I can offer my services now.");
		}
		int repetitions = player.getNumberOfRepetitions(getSlotName(), 3);
		if (repetitions > 0) {
			history.add("I've bloodthirstily slain "
					+ Grammar.quantityplnoun(repetitions, "whole army") + " for Despot Halb Errvl.");
		}
		return history;
 	}

	@Override
	public String getNPCName() {
		return "Despot Halb Errvl";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}
}
