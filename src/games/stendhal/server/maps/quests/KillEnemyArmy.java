package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledInSumForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;

import org.apache.log4j.Logger;


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
 * <li> 500k XP
 * <li> 50k moneys
 * <li> 5 karma for killing 100% creatures
 * <li> 5 karma for killing every 50% next creatures
 * </ul>
 *
 * REPETITIONS: <ul><li> once a week.</ul>
 */

 public class KillEnemyArmy extends AbstractQuest {

	private static final String QUEST_NPC = "Despot Halb Errvl";
	private static final String QUEST_SLOT = "kill_enemy_army";
	private final long questdelay = MathHelper.MILLISECONDS_IN_ONE_WEEK;
	private SpeakerNPC npc;
	private static Logger logger = Logger.getLogger(KillEnemyArmy.class);

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
				new Pair<Integer, String>(150,"Stupid creatures, but very strong. Can be found in Semos mines somewhere."));
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
							  "blordrough storm trooper"));
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
		return(enemyList.get(position));
	}

	/**
	 * function will return NPC answer how much time remains.
	 * @param player - chatting player.
	 * @param currenttime - current system time stamp.
	 * @return - NPC's reply string.
	 */
	private String getNPCTextReply(final Player player, final Long currenttime) {
		String reply = "";
		String questLast = player.getQuest(QUEST_SLOT, 1);
		if (questLast != null) {
			final long timeRemaining = (Long.parseLong(questLast) +
					questdelay - currenttime);

			if (timeRemaining > 0) {
				reply = "You have to check again in "
						+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
						+ ".";
			} else {
				// something wrong.
				reply = "I don't want to decide about you now.";
				logger.error("wrong time count for player "+player.getName()+": "+
						"current time is "+currenttime+
						", last quest time is "+questLast,
						new Throwable());
			}
		}
		return(reply);
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
			};
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
			};

			temp = player.getKeyedSlot("!kills", "shared."+tempName);
			if (temp==null) {
				shared = 0;
			} else {
				shared = Integer.parseInt(temp);
			};

			count = count + solo - recsolo + shared - recshared;
		}
		return(count);
	}

	/**
	 * function will update player quest slot.
	 * @param player - player for which we will record quest.
	 */

	class GiveQuestAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC speakerNPC) {
			final String monstersType = chooseRandomEnemys();
			player.setQuest(QUEST_SLOT, 1, monstersType);
			npc.say("I need help to defeat #enemy " + monstersType +
					" armies. They are a grave concern. Kill at least " + enemyForces.get(monstersType).first()+
					" of any "+ monstersType +
					" soldiers and I will reward you.");
			final HashMap<String, Pair<Integer, Integer>> toKill = new HashMap<String, Pair<Integer, Integer>>();
			List<String> sortedcreatures = enemys.get(monstersType);
			player.setQuest(QUEST_SLOT, "start");
			player.setQuest(QUEST_SLOT, 1, monstersType);
			for(int i=0; i<sortedcreatures.size(); i++) {
				toKill.put(sortedcreatures.get(i), new Pair<Integer, Integer>(0,0));
			}
			new StartRecordingKillsAction(QUEST_SLOT, 2, toKill).fire(player, sentence, speakerNPC);
		}
	}

	/**
	 * function will complete quest and reward player.
	 * @param player - player to be rewarded.
	 * @param killed - number of killed creatures.
	 */
	class RewardPlayerAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC speakerNPC) {
			final String monsters = player.getQuest(QUEST_SLOT, 1);
			int killed=getKilledCreaturesNumber(player);
			int killsnumber = enemyForces.get(monsters).first();
			if(killed == killsnumber) {
				// player killed no more no less then needed soldiers
				npc.say("Good work! Take these coins. And if you need an assassin job again, ask me in one week. My advisors tell me they may try to fight me again.");
			} else {
				// player killed more then needed soldiers
				npc.say("Pretty good! You killed "+(killed-killsnumber)+" extra "+
						Grammar.plnoun(killed-killsnumber, "soldier")+"! Take these coins, and remember, I may wish you to do this job again in one week!");
			}
			int karmabonus = 5*(2*killed/(killsnumber)-1);
			final StackableItem money = (StackableItem)
					SingletonRepository.getEntityManager().getItem("money");
			money.setQuantity(50000);
			player.setQuest(QUEST_SLOT, "done;"+System.currentTimeMillis());
			player.equipOrPutOnGround(money);
			player.addKarma(karmabonus);
			player.addXP(500000);
		};
	}



	/**
	 * class for quest talking.
	 */
	class ExplainAction implements ChatAction {

		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
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
	 * add quest state to npc's fsm.
	 */
	private void step_1() {

		// quest can be given
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(
					new QuestNotStartedCondition(QUEST_SLOT),
					new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "done"),
						new TimePassedCondition(QUEST_SLOT, MathHelper.MINUTES_IN_ONE_WEEK, 1))),
				ConversationStates.ATTENDING,
				null,
				new GiveQuestAction());

		// time is not over
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "done"),
						new NotCondition(
								new TimePassedCondition(QUEST_SLOT, MathHelper.MINUTES_IN_ONE_WEEK, 1))),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
							npc.say(getNPCTextReply(player, System.currentTimeMillis()));
					}
		});

		// explanations
		npc.add(ConversationStates.ATTENDING,
				"enemy",
				new QuestInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
						public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
							npc.say(enemyForces.get(player.getQuest(QUEST_SLOT, 1)).second());
						}
				});

		// explanations
		npc.add(ConversationStates.ATTENDING,
				"enemy",
				new QuestNotInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				"Yes, my enemies are everywhere, they want to kill me! I guess about you are one of them. Stay away from me!",
				null);

		// checking for kills
		final List<String> creatures = new LinkedList<String>(enemyForces.keySet());
		for(int i=0; i<enemyForces.size(); i++) {
			final String enemy = creatures.get(i);

			  // player killed enough enemies.
		      npc.add(ConversationStates.ATTENDING,
		    		  ConversationPhrases.QUEST_MESSAGES,
		    		  new AndCondition(
		    				  new QuestInStateCondition(QUEST_SLOT, 1, enemy),
		    				  new KilledInSumForQuestCondition(QUEST_SLOT, 2, enemyForces.get(enemy).first())),
		    		  ConversationStates.ATTENDING,
		    		  null,
		    		  new RewardPlayerAction());

		      // player killed not enough enemies.
		      npc.add(ConversationStates.ATTENDING,
		    		  ConversationPhrases.QUEST_MESSAGES,
		    		  new AndCondition(
		    				  new QuestInStateCondition(QUEST_SLOT, 1, enemy),
		    				  new NotCondition(
		    						  new KilledInSumForQuestCondition(QUEST_SLOT, 2, enemyForces.get(enemy).first()))),
		    		  ConversationStates.ATTENDING,
		    		  null,
		    		  new ExplainAction());

		};
	}

	/**
	 * add quest to the Stendhal world.
	 */
	@Override
	public void addToWorld() {
		npc = npcs.get(QUEST_NPC);
		super.addToWorld();
		step_1();
	}

	/**
	 * return name of quest slot.
	 */
	public String getSlotName() {
		return(QUEST_SLOT);
	}

	/**
	 * return name of quest.
	 */
	public String getName() {
		return("KillEnemyArmy");
	}
}

