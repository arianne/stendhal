package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;


/**
 * QUEST: KillBlordroughs
 *
 * PARTICIPANTS: <ul>
 * <li> Despot Halb Errvl
 * <li> some creatures
 * </ul>
 *
 * STEPS:<ul>
 * <li> Despot asking you to kill 100 blordrough warriors.
 * <li> Kill them and go back to Despot for your reward.
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
	 
	private static final String QUEST_NPC = "Despot Halb Errvl";
	private static final String QUEST_SLOT = "kill_blordroughs";
	private final long questdelay = MathHelper.MILLISECONDS_IN_ONE_WEEK;	
	private final int killsnumber = 100;
	private SpeakerNPC npc;	
	
	/**
	 * function checking if quest is active for player or no.
	 * @param player - player for who we will check quest state.
	 * @return - true if player's quest is active.
	 */
	private boolean questInProgress(final Player player) {
		if(player.getQuest(QUEST_SLOT)!=null) {
			return(!player.getQuest(QUEST_SLOT,0).equals("done"));			
		}
		return(false);
	}
	
	/**
	 * function decides, if quest can be given to player
	 * @param player - player for which we will check quest slot
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
			if (time < questdelay) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * function will return NPC answer how much time remains.
	 * @param player - chatting player.
	 * @return - NPC's reply string
	 */
	private String getNPCTextReply(final Player player, final Long currenttime) {
		String reply = "";
		String questLast = player.getQuest(QUEST_SLOT, 1);
		if (questLast != null) {
			final long timeRemaining = (Long.parseLong(questLast) + 
					questdelay - currenttime);

			if (timeRemaining > 0) {
				reply = "Please check back in "
						+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
						+ ".";
			} else {
				// something wrong.
				reply = "I dont want to decide about you now.";
				Logger.getLogger("KillBlordroughs").error("wrong time count	for player "+player.getName()+": "+
						"current time is "+currenttime+
						", last quest time is "+questLast, 
						new Throwable());
			}
		}
		return(reply);
	}
	
	/**
	 * function returns list of blordrough creatures.
	 * @return - list of blordrough creatures
	 */
	private LinkedList<Creature> getBlordroughs() {
		LinkedList<Creature> blordroughs = new LinkedList<Creature>();
		final Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
		for (Creature creature : creatures) {
			if (!creature.isRare() && creature.getName().contains("blordrough")) {
				blordroughs.add(creature);
			}
		}	
		return(blordroughs);
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
			} else {
				recsolo = Integer.parseInt(temp);
			};
			temp = player.getQuest(QUEST_SLOT, 2+i*2);
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
			};
			
			temp = player.getKeyedSlot("!kills", "shared."+sortedcreatures.get(i).getName());
			if (temp==null) {
				shared = 0;
			} else {
				shared = Integer.parseInt(temp);
			};
				
			sb.append(";"+solo);
			sb.append(";"+shared);			
		}
		//player.sendPrivateText(sb.toString());
		player.setQuest(QUEST_SLOT, sb.toString());
	}
	
	/**
	 * function will complete quest and reward player.
	 * @param player - player to be rewarded.
	 * @param killed - number of killed creatures.
	 */
	private void rewardPlayer(final Player player, int killed) {
		int karmabonus = 5*(killed/(killsnumber)-1);
		final StackableItem money = (StackableItem) SingletonRepository.getEntityManager()
			.getItem("money");
		money.setQuantity(50000);
		player.setQuest(QUEST_SLOT, "done;"+System.currentTimeMillis());
		player.equipOrPutOnGround(money);
		player.addKarma(karmabonus);
		player.addXP(500000);
	}
	 
	/**
	 * class for quest talking.
	 */
	class QuestAction implements ChatAction {

		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			if(questInProgress(player)) {
				int killed = getKilledCreaturesNumber(player);
				// player killed no creatures but asked about quest again.
				if(killed==0) {
					npc.say("I already explained to you what i need. Are you idiot, as you cant remember this simple thing about #blordroughs?");
					return;
				}
				if(killed < killsnumber) {
					npc.say("You killed only "+killed+" blordrough soldiers.");
					return;
				}
				rewardPlayer(player, killed);
			} else {
				final Long currtime = System.currentTimeMillis();
				if (questCanBeGiven(player, currtime)) {
					// will give quest to player.
					npc.say("I need help in battles with #Blordrough warriors. They really annoying me. Kill at least 100 of any blordrough soldiers and i will reward you.");
					writeQuestRecord(player);					
				} else {
					npc.say(getNPCTextReply(player, currtime));
				}
				
			};
		}
	}
	
	/**
	 * add quest state to npc's fsm.
	 */
	private void step_1() {	
		npc.add(ConversationStates.ATTENDING, 
				Arrays.asList("Blordrough","blordrough"),
				null, 
				ConversationStates.ATTENDING, 
				"My Mithrilbourgh army have great losses in battles with Blordrough soldiers. They coming from side of Ados tunnels.",
				null);
		npc.add(ConversationStates.ATTENDING, 
				ConversationPhrases.QUEST_MESSAGES,
				null, 
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
		return("KillBlordroughs");
	}
}
 
