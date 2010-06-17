package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
 * QUEST: The Pied Piper
 *
 * PARTICIPANTS: <ul>
 * <li> Mayor Chalmers
 * <li> George
 * <li> Anna
 * <li> Jens
 * <li> Susi
 * <li> Pied Piper
 * </ul>
 *
 * STEPS:<ul>
 * <li> PART I.
 * <li> Mayor will activate the quest by telling to all about Ados city rats problem.
 * <li> Kill (all) rats in city and go back to mayor for your reward.
 * </ul>
 *
 * REWARD:<ul>
 * <li> PART I.
 * <li> 10 moneys for each rat
 * <li> 20 moneys for each caverat
 * <li> 100 moneys for each venomrat
 * <li> 160 moneys for each razorrat
 * <li> 360 moneys for each giantrat
 * <li> 800 moneys for each archrat
 * <li> 5 karma in total
 * </ul>
 *
 * REPETITIONS: <ul><li> once between a week and two weeks.</ul>
 */
 public class ThePiedPiper extends AbstractQuest {

	private static final String QUEST_SLOT = "the_pied_piper";
	private static Logger logger = Logger.getLogger(ThePiedPiper.class);
	protected LinkedList<Creature> rats =
		new LinkedList<Creature>();


    // timings unit is second.
	private static int QUEST_INACTIVE_TIME_MAX = 1;
	private static int QUEST_INACTIVE_TIME_MIN = 1;
	private static int QUEST_INVASION_TIME = 1;
	private static int QUEST_AWAITING_TIME = 1;
	private static int QUEST_SHOUT_TIME = 1;

	/**
	 * function will set timings to either test server or game server.
	 */
	private static void adjustTimings() {
		if (System.getProperty("stendhal.testserver") == null) {		
			// game timings
			QUEST_INACTIVE_TIME_MAX = 60 * 60 * 24 * 14;
			QUEST_INACTIVE_TIME_MIN = 60 * 60 * 24 * 7;
			QUEST_INVASION_TIME = 60 * 60 * 2;
			QUEST_AWAITING_TIME = 60 * 1;
			QUEST_SHOUT_TIME = 60 * 10;
			} 
		else {	
			// test timings
			QUEST_INACTIVE_TIME_MAX = 60 * 11;
			QUEST_INACTIVE_TIME_MIN = 60 * 10;
			QUEST_INVASION_TIME = 60 * 20;
			QUEST_AWAITING_TIME = 60 * 10;
			QUEST_SHOUT_TIME = 60 * 2;
			}
	}

	/**
	 * related to quest part.
	 * <ul>
	 * <li> INACTIVE - quest isn't active
	 * <li> INVASION - part I (rats invasion)
	 * <li> AWAITING - part II (pied piper called)
	 * <li> OUTGOING - part III (pied piper killing rats)
	 * <li> CHILDRENS - part IV (pied piper takes childrens away)
	 * <li> FINAL - part V (return childrens back to Ados)
	 * </ul>
	 */

	private enum TPP_Phase {
		TPP_INACTIVE,
		TPP_INVASION,
		TPP_AWAITING ,
		TPP_OUTGOING,
		TPP_CHILDRENS,
		TPP_FINAL
	}
	// initializing to prevent null pointer exception
    private TPP_Phase phase=TPP_Phase.TPP_INACTIVE;

	/**
	 * List of game zones, where rats will appears.
	 *
	 * TODO: add other Ados buildings here, and improve summonRats() function
	 *       to avoid placing rats inside closed areas within houses.
	 */
	protected static final List<String> RAT_ZONES = Arrays.asList(
//			"int_ados_haunted_house",
//			"int_ados_storage",
			"int_ados_barracks_0",
			"int_ados_barracks_1",
			"int_ados_bakery",
			"int_ados_goldsmith",
			"int_ados_bank",
// can't be used because NPC can block creature
//			"int_ados_tavern_0",
			"int_ados_library",
			"int_ados_bar",
			"int_ados_bar_1",
			"int_ados_sewing_room",
			"int_ados_meat_market",
			"int_ados_fishermans_hut_north",
			"int_ados_town_hall",
			"int_ados_town_hall_1",
			"int_ados_town_hall_2",
			"int_ados_town_hall_3",
			"int_ados_ross_house",
			"0_ados_city_n",
			"0_ados_city",
			"0_ados_city_s");

	/**
	 * List of creatures types to create.
	 */
	protected static final List<String> RAT_TYPES = Arrays.asList(
			"rat",
			"caverat",
			"venomrat",
			"razorrat",
			"giantrat",
			"archrat");

	/**
	 * List of reward moneys quantities for each type of killed rats.
	 */
	protected static final List<Integer> RAT_REWARDS = Arrays.asList(
			10,
			20,
			100,
			160,
			360,
			800);

	/**
	 * constructor
	 */
	public ThePiedPiper() {
		adjustTimings();
	}

	final private ShouterTimer shouterTimer = new ShouterTimer();
	/**
	 * timer for npc's shouts to player.
	 */
	class ShouterTimer implements TurnListener {
		public void start() {
			tellAllAboutRatsProblem();
			TurnNotifier.get().dontNotify(this);
			TurnNotifier.get().notifyInSeconds(QUEST_SHOUT_TIME, this);
		}
		public void stop() {
			TurnNotifier.get().dontNotify(this);
		}
		public void onTurnReached(int currentTurn) {
			start();
		}
	}

	/**
	 * summon rats, make phase INVASION and
	 * start timer to phase AWAITING.
	 */
	protected void phaseInactiveToInvasion() {
		logger.info("ThePiedPiper quest started (phase INVASION).");
		phase=TPP_Phase.TPP_INVASION;
		summonRats();
		shouterTimer.start();
		step_2();
	}

	/**
	 * last rat killed.
	 */
	protected void phaseInvasionToInactive() {
		tellAllAboutNoRatsInCity();
		logger.info("ThePiedPiper quest: last rat was killed (phase INACTIVE).");
		phase=TPP_Phase.TPP_INACTIVE;
		shouterTimer.stop();
		step_1();
	}

	/**
	 * remove rats, make phase AWAITING and
	 * start timer to phase INACTIVE.
	 */
	protected void phaseInvasionToAwaiting() {
		logger.info("ThePiedPiper quest timeout (phase AWAITING).");
		phase=TPP_Phase.TPP_AWAITING;
		removeAllRats();
		shouterTimer.stop();
		tellAllAboutRatsIsWinners();
		step_3();
	}

	/**
	 * make phase INACTIVE and
	 * start timer to phase INVASION.
	 */
	protected void phaseAwaitingToInactive() {
		logger.info("ThePiedPiper quest is over (phase INACTIVE).");
		phase=TPP_Phase.TPP_INACTIVE;
		shouterTimer.stop();
	//	tellAllAboutRatsIsGone();
		step_1();
	}

	final private QuestTimer questTimer = new QuestTimer();
	/**
	 * Timings logic of quest.
	 */
	class QuestTimer implements TurnListener {
		public void onTurnReached(final int currentTurn) {
			switch(phase){
			case TPP_INACTIVE:
					phaseInactiveToInvasion();
				    break;
			case TPP_INVASION:
					phaseInvasionToAwaiting();
				    break;
			default:
					phaseAwaitingToInactive();
					break;
			}
		}
	}
	
	protected int getRatsCount() {
		return(rats.size());
	}

	/**
	 *
	 * NPC's actions when player asks for rats problem.
	 */
	class AnswerOrOfferRewardAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC mayor) {
			switch (phase) {
			case TPP_INACTIVE: // quest is not active
					mayor.say("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.");
					break;
			case TPP_INVASION: // rats invasion
					if(rats.size()!=0) {
						mayor.say("There " + Grammar.isare(rats.size()) +  " still about "+Integer.toString(rats.size())+" rats alive.");
					} else {
						mayor.say("All the rats are now killed, and you can take your #reward. If you want to know " +
						"#details, I can explain it to you.");
					};
					break;
			default: // pied piper here.
				    mayor.say("The rats are gone. "+
				    		"You can get #reward for your help now, ask about #details "+
							  "if you want to know more.");
					break;
			};
		}
	}

	/**
	 *  NPC's actions when player asks for his reward.
	 */
	class RewardPlayerAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC mayor) {
			switch (phase) {
			case TPP_INVASION: // invasion time
					mayor.say("Ados is being invaded by rats! "+
							  "I dont want to reward you now, "+
			  				  " until all rats are dead.");
				    break;
		    default:
		    		final int quantity = calculateReward(player);
		    		// to avoid giving karma without job
		    		if(quantity==0) {
		    			mayor.say("You didn't kill any rats which invaded the city, so you don't deserve a reward.");
		    			return;
		    		};
		    		player.addKarma(5);
		    		final StackableItem moneys = (StackableItem) SingletonRepository.getEntityManager()
		    						.getItem("money");
		    		moneys.setQuantity(quantity);
		    		player.equipOrPutOnGround(moneys);
		    		mayor.say("Please take "+quantity+" money, thank you very much for your help.");
		    		player.setQuest(QUEST_SLOT, "done");
		    	    break;
			}
		}
	}

	/**
	 * NPC's answers when player ask for details.
	 */
	class DetailsKillingsAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC mayor) {
			if (calculateReward(player)==0) {
				mayor.say("You killed no rats during the #rats invasion. "+
						  "To get a #reward you have to kill at least "+
						  "one rat at that time.");
				return;
			};
			final StringBuilder sb = new StringBuilder("Well, from the last reward, you killed ");
			long moneys = 0;
			int kills = 0;
			for(int i=0; i<RAT_TYPES.size(); i++) {
				try {
					kills=Integer.parseInt(player.getQuest(QUEST_SLOT,i+1));
				} catch (NumberFormatException nfe) {
					// Have no records about this creature in player's slot.
					// Treat it as he never killed this creature.
					kills=0;
				};
				// must add 'and' word before last creature in list
				if(i==(RAT_TYPES.size()-1)) {
					sb.append("and ");
				};

				sb.append(Grammar.quantityplnoun(kills, RAT_TYPES.get(i)));
				sb.append(", ");
				moneys = moneys + kills*RAT_REWARDS.get(i);
			}
			sb.append("so I will give you ");
			sb.append(moneys);
			sb.append(" money as a #reward for that job.");
			mayor.say(sb.toString());
		}
	}

	/**
	 *  method for making records about killing rats
	 *  in player's quest slot.
	 *
	 *  @param player
	 *  			- player which killed rat.
	 *  @param victim
	 *  			- rat object
	 */
	private void killsRecorder(Player player, final RPEntity victim) {

		final String str = victim.getName();
		final int i = RAT_TYPES.indexOf(str);
		if(i==-1) {
			//no such creature in reward table, will not count it
			logger.warn("Unknown creature killed: "+
					    victim.getName());
			return;
		};

		if((player.getQuest(QUEST_SLOT)==null)||
		   (player.getQuest(QUEST_SLOT).equals("done")||
		   (player.getQuest(QUEST_SLOT).equals("")))){
			// player just killed his first creature.
		    player.setQuest(QUEST_SLOT, "rats;0;0;0;0;0;0");
		};

		// we using here and after "i+1" because player's quest index 0
		// is occupied by quest stage description.
		if(player.getQuest(QUEST_SLOT,i+1)==""){
			// something really wrong, will correct this...
			player.setQuest(QUEST_SLOT,"rats;0;0;0;0;0;0");
		};
		int kills;
		try {
			kills = Integer.parseInt(player.getQuest(QUEST_SLOT, i+1))+1;
		} catch (NumberFormatException nfe) {
			// have no records about this creature in player's slot.
			// treat it as he never killed this creature before.
			kills=1;
		};
		player.setQuest(QUEST_SLOT, i+1, Integer.toString(kills));
	}

	/**
	 * function for calculating reward's moneys for player
	 *
	 * @param player
	 * 			- player which must be rewarded
	 * @return
	 * 			gold amount for hunting rats.
	 */
	private int calculateReward(Player player) {
		int moneys = 0;
		int kills = 0;
		for(int i=0; i<RAT_TYPES.size(); i++) {
			try {
				final String killed = player.getQuest(QUEST_SLOT,i+1);
				// have player quest slot or not yet?
				if (killed != null) {
					kills=Integer.decode(killed);
				}
			} catch (NumberFormatException nfe) {
				// player's quest slot don't contain valid number
				// so he didn't killed such creatures.
			};
			moneys = moneys + kills*RAT_REWARDS.get(i);
		};
		return(moneys);
	}


    /**
     *  Implementation of Observer interface.
     *  Update function will record the fact of rat's killing
     *  in player's quest slot.
     */
	class RatsObserver implements Observer {
		public void update (Observable obj, Object arg) {
	        if (arg instanceof CircumstancesOfDeath) {
	    		final CircumstancesOfDeath circs=(CircumstancesOfDeath)arg;
	        	if(RAT_ZONES.contains(circs.getZone().getName())) {
	        	if(circs.getKiller() instanceof Player) {
	        		final Player player = (Player) circs.getKiller();
	        		killsRecorder(player, circs.getVictim());
	        	}
	        	notifyDead(circs.getVictim());
	        	};
	        };
	    }
	}

	/**
	 *  Red alert! Rats in the Ados city!
	 */
	private void tellAllAboutRatsProblem() {
		final String text = "Mayor Chalmers shouts: Ados City is being invaded by #rats!"+
			              " Anyone who will help to clean up city, will be rewarded!";
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}

	/**
	 *  Rats are dead :-)
	 */
	private void tellAllAboutNoRatsInCity() {
		final String text = "Mayor Chalmers shouts: No #rats in Ados survived, "+
				            "only those who always lived in the "+
				            "haunted house. "+
				            "Rat hunters are welcome to get their #reward.";
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}

	/**
	 *  Rats now living under all buildings. Need to call Pied Piper :-)
	 */
	private void tellAllAboutRatsIsWinners() {
		final String text = // "Mayor Chalmers shouts: Suddenly, #rats have captured city, "+
							"Mayor Chalmers shouts: The #rats left as suddenly as they arrived. "+
							"Perhaps they have returned to the sewers. "+
				   //         "I now need to call the Pied Piper, a rat exterminator. "+
							"Anyway, " +
				            "Thanks to all who tried to clean up Ados, "+
				            " you are welcome to get your #reward.";
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}

	/**
	 *  Pied Piper sent rats away:-)
	 
	private void tellAllAboutRatsIsGone() {
		final String text = "Mayor Chalmers shouts: Thankfully, all the #rats are gone now, " +
							"the Pied Piper " +
							"hypnotized them and led them away to dungeons. "+
				            "Those of you, who helped Ados City with the rats problem, "+
							"can get your #reward now.";
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}*/

	/**
	 * rats invasion starts :-)
	 * Iterate through each zone and select the min and max rat count based on zone size
	 * Places rat if possible, if not skip this rat (so if 6 rats chosen perhaps only 3 are placed)
	 */
	private void summonRats() {

		final EntityManager manager = SingletonRepository.getEntityManager();
		final RatsObserver ratsObserver = new RatsObserver();

		// generating rats in zones
		for(int j=0; j<(RAT_ZONES.size()); j++) {
			final StendhalRPZone zone = (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(
					RAT_ZONES.get(j));
			final int maxRats = (int) Math.round(Math.sqrt(zone.getWidth()*zone.getHeight())/4);
			final int minRats = (int) Math.round(Math.sqrt(zone.getWidth()*zone.getHeight())/12);
			final int ratsCount = Rand.rand(maxRats-minRats)+minRats;
			logger.debug(ratsCount+ " rats selected at " + zone.getName());
			for(int i=0 ; i<ratsCount; i++) {
				final int x=Rand.rand(zone.getWidth());
				final int y=Rand.rand(zone.getHeight());
				// Gaussian distribution
				int tc=Rand.randGaussian(0,RAT_TYPES.size());
				if ((tc>(RAT_TYPES.size()-1)) || (tc<0)) {
					tc=0;
				};
				// checking if EntityManager knows about this creature type.
				final Creature tempCreature = new Creature((Creature) manager.getEntity(RAT_TYPES.get(tc)));
				if (tempCreature == null) {
					continue;
				};
				final Creature rat = new Creature(tempCreature.getNewInstance());

				// chosen place is occupied
				if (zone.collides(rat,x,y)) {
					// Could not place the creature here.
					// Treat it like it was never exists.
					logger.debug("RATS " + zone.getName() + " " + x + " " + y + " collided.");
					continue;
				} else if (zone.getName().startsWith("0")) {
					// If we can't make it here, we can't make it anywhere ...
					// just checking the 0 level zones atm	
					// the rat is not in the zone yet so we can't call the smaller version of the searchPath method
					final List<Node> path = Path.searchPath(zone, x, y, zone.getWidth()/2,
							zone.getHeight()/2, (64+64)*2);
					if (path == null || path.size() == 0){
						logger.debug("RATS " + zone.getName() + " " + x + " " + y + " no path to " + zone.getWidth()/2 + " " + zone.getHeight()/2);
						continue;
					}
				} 
				// spawn creature
				rat.registerObjectsForNotification(ratsObserver);
				/* -- commented because of these noises reflects on all archrats in game -- */
				// add unique noises to humanoids
				if (tc==RAT_TYPES.indexOf("archrat")) {
					final LinkedList<String> ll = new LinkedList<String>(
							Arrays.asList("We will capture Ados!",
							"Our revenge will awesome!"));
					LinkedHashMap<String, LinkedList<String>> lhm =
						new LinkedHashMap<String, LinkedList<String>>();
					// add to all states except death.
					lhm.put("idle", ll);
					lhm.put("fight", ll);
					lhm.put("follow", ll);
					rat.setNoises(lhm);
				};
				
				StendhalRPAction.placeat(zone, rat, x, y);
				rats.add(rat);
			};
		};
	}

	/**
	 * function to control amount of alive rats.
	 * @param dead
	 * 			- creature that was just died.
	 */
	private void notifyDead(final RPEntity dead) {
		if (!rats.remove(dead)) {
			logger.warn("killed creature isn't in control list ("+dead.toString()+").");
		}
		if (rats.size()==0) {
			phaseInvasionToInactive();
		};
    }

	/**
	 * removing rats from the world
	 */
	private void removeAllRats() {
		final int sz=rats.size();
		int i=0;
		while(rats.size()!=0) {
			try {
			final Creature rat = rats.get(0);
			rat.stopAttack();
			rat.clearDropItemList();
			rat.getZone().remove(rat);
			rats.remove(0);
			i++;
			} catch (IndexOutOfBoundsException ioobe) {
				// index is greater then size???
				logger.error("removeAllRats IndexOutOfBoundException at "+
						Integer.toString(i)+" position. Total "+
						Integer.toString(sz)+" elements.", ioobe);
			};
		}
	}


	/**
	 * Set new time period for quest timer (time to next quest phase).
	 * @param max - maximal time in seconds
	 * @param min - minimal time in seconds
	 */
	private void newNotificationTime(int max, int min) {
		TurnNotifier.get().dontNotify(questTimer);
		TurnNotifier.get().notifyInSeconds(
				Rand.randUniform(max, min),	questTimer);
	}

	/**
	 *   add states to NPC's FSM
	 */
	private void step_0() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("rats", "rats!"), null,
				ConversationStates.ATTENDING, null, new AnswerOrOfferRewardAction());
		npc.add(ConversationStates.ATTENDING, "reward", null,
				ConversationStates.ATTENDING, null, new RewardPlayerAction());
		npc.add(ConversationStates.ATTENDING, "details", null,
				ConversationStates.ATTENDING, null, new DetailsKillingsAction());
	}

	/**
	 * Quest will start after quest inactive time period will over.
	 */
	private void step_1() {
		newNotificationTime(QUEST_INACTIVE_TIME_MAX, QUEST_INACTIVE_TIME_MIN);
	}

	/**
	 * Quest will go to AWAITING state after invasion time period will over.
	 */
	private void step_2() {
		newNotificationTime(QUEST_INVASION_TIME, QUEST_INVASION_TIME);
	}

	/**
	 *  will start part II of this quest.
	 *  currently makes quest inactive.
	 */
	private void step_3() {
		if (phase==TPP_Phase.TPP_AWAITING) {
			newNotificationTime(QUEST_AWAITING_TIME, QUEST_AWAITING_TIME);
		}
	}

 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
 	
 	@Override
 	public List<String> getHistory(final Player player) {
 		LinkedList<String> history = new LinkedList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return history;
		}	
		final String questState = player.getQuest(QUEST_SLOT, 0);
		if ("rats".equals(questState)) {
			history.add("I have killed some rats in Ados city already, and trying to kill more.");
		}
		if ("done".equals(questState)) {
			history.add("I have killed some rats in Ados city and got reward from Mayor Chalmers!");
		}
		return history; 		
 	}
 	


 	@Override
	public String getName() {
		return "ThePiedPiper";
	}

	@Override
	public void addToWorld() {
		step_0();
	    step_1();
	    step_3();
		super.addToWorld();
		fillQuestInfo(
				"The Pied Piper",
				"Ados city have a rats problem periodically.",
				true);
	}
}
