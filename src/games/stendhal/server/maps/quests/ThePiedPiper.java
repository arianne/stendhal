package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.KillNotificationCreature;
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
	private LinkedList<KillNotificationCreature> rats = 
		new LinkedList<KillNotificationCreature>();
 
	private static final int MAX_QUEST_REPEAT_TIME = 60 * 60 * 24 * 14;
	private static final int MIN_QUEST_REPEAT_TIME = 60 * 60 * 24 * 7;	
//	private static final int MAX_QUEST_REPEAT_TIME = 60 * 6;
//	private static final int MIN_QUEST_REPEAT_TIME = 60 * 5;

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
    private TPP_Phase phase;
	/**
	 * List of game zones, where rats will appears.
	 * 
	 * TODO: add other ados buildings here, and improve summonRats() function
	 *       to avoid placing rats inside closed areas within houses.
	 */
	private static final List<String> RAT_ZONES = Arrays.asList(
//			"int_ados_haunted_house",
//			"int_ados_storage",
			"int_ados_barracks_0",
			"int_ados_barracks_1",
			"int_ados_bakery",
			"int_ados_goldsmith",
			"int_ados_bank",
			"int_ados_tavern_0",
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
			"int_ados_ross_house");
	
	/**
	 * List of creatures types to create.
	 */
	private static final List<String> RAT_TYPES = Arrays.asList(
			"rat",
			"caverat",
			"venomrat",
			"razorrat",
			"giantrat",
			"archrat");
	
	/**
	 * List of moneys quantities reward for each type of killed rats.
	 */
	private static final List<Integer> RAT_REWARDS = Arrays.asList(
			10,
			20,
			100,
			160,
			360,
			800);
	
	
	final private QuestTimer questTimer = new QuestTimer();
	/**
	 * Timings logic of quest.
	 */
	class QuestTimer implements TurnListener {
		public void onTurnReached(final int currentTurn) {
			switch(phase){
			case TPP_INACTIVE: 
					// summon rats, make phase 1 and
				    // start timer to phase 2.
					logger.info("ThePiedPiper quest started.");
					phase=TPP_Phase.TPP_INVASION;
					summonRats();
					tellAllAboutRatsProblem();
					step_0();
				    break;
			case TPP_INVASION: 
					// remove rats, make phase 2 and 
				    // start timer to phase 0.
					logger.info("ThePiedPiper quest timeout occured.");
					phase=TPP_Phase.TPP_AWAITING;
					removeAllRats();
					tellAllAboutRatsIsWinners();
					step_2();
				    break;
			default: // make phase 0 and start timer to phase 1.
					logger.info("ThePiedPiper quest going inactive.");
					tellAllAboutRatsIsGone();
					step_2();
					phase=TPP_Phase.TPP_INACTIVE;
					break;
			}
		}
	}

	
	/**
	 * 
	 * NPC's actions when player asks for rats problem.
	 */
	class AnswerOrOfferRewardAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC mayor) {
			switch (phase) {
			case TPP_INACTIVE: // quest is not active
					mayor.say("Ados isn't now under rats invasion. You still can "+
							  "get #reward for last time you helped. You can ask #details "+
							  "if you want");
					break;
			case TPP_INVASION: // rats invasion
					if(rats.size()!=0) {
						mayor.say("There is still about "+Integer.toString(rats.size())+" rats alive.");
					} else {
						mayor.say("All rats now killed, and you can take your #reward. If you want to know " +
						"#details, i can explain it to you.");
					};
					break;
			default:// pied piper here.
				    mayor.say("I called rats exterminator. "+
				    		"You can get #reward for your help now, ask about #details "+
							  "if you want to know it.");
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
					mayor.say("Ados is under rats invasion. "+
							  "I dont want reward you now, "+
			  				  " until all rats will die.");
				    break;
		    default:			
		    		final int quantity = calculateReward(player);
		    		// to avoid giving karma without job
		    		if(quantity==0) {
		    			mayor.say("You killed no rats, so you will get no moneys.");
		    			return;
		    		};
		    		player.addKarma(5);
		    		final StackableItem moneys = (StackableItem) SingletonRepository.getEntityManager()
		    						.getItem("money");
		    		moneys.setQuantity(quantity);
		    		player.equipOrPutOnGround(moneys);
		    		mayor.say("Here is your "+quantity+" moneys, thank you very much for your help.");
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
				mayor.say("You killed no rats during #rats invasion. "+
						  "For get #reward you have to kill at least "+
						  "one rat at that time.");
				return;
			};
			final StringBuilder sb = new StringBuilder("Well, from last reward, you killed ");
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
					sb.append(" and ");
				}; 				
				sb.append(kills);
				sb.append(" "+RAT_TYPES.get(i));
				sb.append(Grammar.quantityplnoun(kills, RAT_TYPES.get(i)));
				if(kills!=1){
					sb.append("s");
				};
				
				sb.append(", ");
				moneys = moneys + kills*RAT_REWARDS.get(i);
			}
			sb.append("so i have to give you ");
			sb.append(moneys);
			sb.append(" moneys as #reward for that job.");
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
				kills=Integer.decode(player.getQuest(QUEST_SLOT,i+1));				
			} catch (NumberFormatException nfe) {
				// player's quest slot don't contain valid number
				// so he didn't killed such creatures.
				kills=0;
			};
			
			if(i==RAT_TYPES.size()) {
			}
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
	        	notifyDead((RPEntity)circs.getVictim());
	        	};
	        };
	    }
	}
	
	/**
	 *  Red alert! Rats in the Ados city!
	 */
	private void tellAllAboutRatsProblem() {
		final String text = new String("Mayor Chalmers shouts: Ados city is under #rats invasion!"+
				                          " Anyone who will help to clean up city, will be rewarded!");
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}
	
	/**
	 *  Rats is dead :-)
	 */
	private void tellAllAboutNoRatsInCity() {
		final String text = new String("Mayor Chalmers shouts: No #rats in Ados now, "+
				                          "exclude those who always lived in storage and "+
				                          "haunted house. "+
				                          "Rats hunters are welcome to get their #reward.");
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}
	
	/**
	 *  Rats now living under all buildings. Need to call Pied Piper :-)
	 */
	private void tellAllAboutRatsIsWinners() {
		final String text = new String("Mayor Chalmers shouts: Saddanly, #rats captured city, "+
										  "they are living now under all Ados buildings. "+
				                          "I am now in need of call Piped Piper, rats exterminator. "+
				                          "Thank to all who tryed to clean up Ados, "+
				                          " you are welcome to get your #reward.");
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}
	
	/**
	 *  Pied Piper sent rats away:-)
	 */
	private void tellAllAboutRatsIsGone() {
		final String text = new String("Mayor Chalmers shouts: Thanx gods, #rats is gone now, Pied Piper " +
										"hypnotized them and lead away to dungeons. "+
				                        "Those of you, who helped to Ados city with rats problem, "+
										"can get your #reward now.");
		SingletonRepository.getRuleProcessor().tellAllPlayers(text);
	}
	
	/** 
	 * rats invasion starts :-)
	 */
	private void summonRats() {

		final int maxRats = 5;
		final int minRats = 1;
		final EntityManager manager = SingletonRepository.getEntityManager();
		final RatsObserver ratsObserver = new RatsObserver();
		
		// generating rats in zones
		for(int j=0; j<(RAT_ZONES.size()); j++) {
			final StendhalRPZone zone = (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(
					RAT_ZONES.get(j));
			for(int i=minRats ; i<maxRats; i++) {
				final int x=Rand.rand(zone.getWidth());
				final int y=Rand.rand(zone.getHeight());
				// equal distribution 
				// final int tc=Rand.rand(RAT_TYPES.size());
				// Gaussian distribution
				int tc=Rand.randGaussian(0,RAT_TYPES.size());
				if ((tc>(RAT_TYPES.size()-1)) || (tc<0)) {
					tc=0;
				};
				final KillNotificationCreature rat = 
					new KillNotificationCreature( (Creature) manager.getEntity(
							RAT_TYPES.get(tc)));

				// choosed place is occupied
				if (zone.collides(rat,x,y)) {
					// Could not place the creature here. 
					// Treat it like it was never exists.
				} else {
					// spawn creature
					rat.registerObjectsForNotification(ratsObserver);
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
		};
	}
	
	/**
	 * function to control amount of alive rats.
	 * @param dead
	 * 			- creature that was just died.
	 */
	public void notifyDead(final RPEntity dead) {
		rats.remove(rats.get(rats.indexOf(dead)));
		if (rats.size()==0) {
			// killed last rat
			tellAllAboutNoRatsInCity();	
			phase=TPP_Phase.TPP_INACTIVE;
			step_0();
		};
    }
	
	/**
	 * removing rats from the world
	 */
	public void removeAllRats() {
		final int sz=rats.size();
		int i=0;
		while(rats.size()!=0) {
			try {
			final KillNotificationCreature rat = rats.get(0);
			rat.stopAttack();
			rat.clearDropItemList();
			rat.getZone().remove(rat);
			rats.remove(0);
			i++;
			} catch (IndexOutOfBoundsException ioobe) {
				// index is greater then size???
				logger.error("removeAllRats IndexOutOfBoundException at "+
						Integer.toString(i)+" position. Totally "+
						Integer.toString(sz)+" elements.", ioobe);
			};
		}
	}
    
	/**
	 * function for calculating time of either next quest start or 
	 * timeout of current quest.
	 * 
	 * @return
	 * 			time in seconds to waiting.
	 */
	private int calculateNextQuestTime() {
		final int time = MIN_QUEST_REPEAT_TIME + 
			Rand.rand(MAX_QUEST_REPEAT_TIME - MIN_QUEST_REPEAT_TIME);
		// limit between MAX_ and MIN_
		return Math.min(time, MAX_QUEST_REPEAT_TIME);
	}
	

	/*
	private void step_test() {
		//
		// WARNING: Dont use in game! For testing only
		// Register TurnListener to have automatical quest start.
		TurnNotifier.get().dontNotify(questTimer);
		TurnNotifier.get().notifyInSeconds(60, questTimer);
	}
	*/
	
	/**
	 *  Register TurnListener to have automatical quest start/stop.
	 *  Quest will start or stop after time will over.
	 */
	private void step_0() {
		TurnNotifier.get().dontNotify(questTimer);
		TurnNotifier.get().notifyInSeconds(calculateNextQuestTime(), questTimer);
	}
	

	/**
	 *   add states to NPC's FSM
	 */
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("rats"), null, 
				ConversationStates.ATTENDING, null, new AnswerOrOfferRewardAction());
		npc.add(ConversationStates.ATTENDING, "reward", null,
				ConversationStates.ATTENDING, null, new RewardPlayerAction());
		npc.add(ConversationStates.ATTENDING, "details", null,
				ConversationStates.ATTENDING, null, new DetailsKillingsAction());
	}
	 
	/**
	 *  will start part II of this quest.
	 *  currently makes quest inactive.
	 */
	private void step_2() {
		if (phase==TPP_Phase.TPP_AWAITING){
			step_0();
		};
	}
	
 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

 	@Override
	public String getName() {
		return "ThePiedPiper";
	}
	
	@Override
	public void addToWorld() {
		step_0();
	    step_1();
	    step_2();
	    // function step_test() is for testing only
	    //step_test();
		super.addToWorld();
	}	
}
