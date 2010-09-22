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

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.piedpiper.AwaitingPhase;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuest;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuestConstants;
import games.stendhal.server.maps.quests.piedpiper.InactivePhase;
import games.stendhal.server.maps.quests.piedpiper.InvasionPhase;
import games.stendhal.server.maps.quests.piedpiper.TPPQuestInPhaseCondition;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
 public class ThePiedPiper extends AbstractQuest implements ITPPQuestConstants {

	protected static final Logger logger = Logger.getLogger(ThePiedPiper.class);
	protected LinkedList<Creature> rats = new LinkedList<Creature>();
	
	private static LinkedList<ITPPQuest> phases = new LinkedList<ITPPQuest>();
    private static TPP_Phase phase = INACTIVE;
    
	protected LinkedHashMap<String, Integer> timings = new LinkedHashMap<String, Integer>();
	
	public static SpeakerNPC getMainNPC() {
		return SingletonRepository.getNPCList().get("Mayor Chalmers");
	}
	
	/**
	 * function will set timings to either test server or game server.
	 */
	private void setTimings() {
		if (System.getProperty("stendhal.testserver") == null) {		
			// game timings */
			timings.put(INACTIVE_TIME_MAX, 60 * 60 * 24 * 14);
			timings.put(INACTIVE_TIME_MIN, 60 * 60 * 24 * 7);
			timings.put(INVASION_TIME_MIN, 60 * 60 * 2);
			timings.put(INVASION_TIME_MAX, 60 * 60 * 2);
			timings.put(AWAITING_TIME_MIN, 60 * 1);
			timings.put(AWAITING_TIME_MAX, 60 * 1);
			timings.put(SHOUT_TIME, 60 * 10);
			} 
		else {	
			// test timings
			timings.put(INACTIVE_TIME_MAX, 60 * 2);
			timings.put(INACTIVE_TIME_MIN, 60 * 1);
			timings.put(INVASION_TIME_MIN, 60 * 20);
			timings.put(INVASION_TIME_MAX, 60 * 20);
			timings.put(AWAITING_TIME_MIN, 60 * 20);
			timings.put(AWAITING_TIME_MAX, 60 * 20);
			timings.put(SHOUT_TIME, 60 * 2);
			} 
	}
	   
    /**
     * 
     * @param ph
     * @return phase index
     */
    public static int getPhaseIndex(TPP_Phase ph) {
    	for (int i=0; i<getPhases().size(); i++) {
    		if(getPhases().get(i).getPhase().compareTo(ph)==0) {
    			return(i);
    		}
    	}
    	// didnt found it! 
    	logger.warn("Using improper phase for quest ("+ph.name()+"). size: "+getPhases().size());
		return (-1);
    }
    
    /**
     * return next available quest phase
     * @param ph - 
     * @return next phase
     */
    public static TPP_Phase getNextPhase(TPP_Phase ph) {
    	int pos=getPhaseIndex(ph);
    	if(pos!=(getPhases().size()-1)) {
		   return (getPhases().get(pos+1).getPhase());
    	};
    	return(getDefaultPhaseClass().getPhase());
    }
    
    /**
     * return next instance of quest phases classes from list
     * @param ph
     * @return next phase class
     */
    public static ITPPQuest getNextPhaseClass(TPP_Phase ph) {
		return getPhases().get(getPhaseIndex(getNextPhase(ph)));    	
    }
    
    /**
     * return instance of quest phases classes
     * @param ph
     * @return phase class
     */
    public static ITPPQuest getPhaseClass(TPP_Phase ph) {
    	/*
    	if(getPhaseIndex(ph)==-1) {
    		return getDefaultPhaseClass();
    	}
    	*/
    	return getPhases().get(getPhaseIndex(ph));
    }
    
    /**
     * function return default phase class
     * @return default phase class
     */
    public static ITPPQuest getDefaultPhaseClass() {
    	return getPhases().get(getPhaseIndex(INACTIVE));
    }


	private static QuestTimer questTimer;
	/**
	 * Timings logic of quest.
	 */
	class QuestTimer implements TurnListener {
		public void onTurnReached(final int currentTurn) {
			final ITPPQuest i = getPhaseClass(getPhase());
			i.phaseToNextPhase(getNextPhaseClass(getPhase()), 
					new LinkedList<String>(Arrays.asList("pied piper")));
		}
	}
	
	protected int getRatsCount() {
		return(rats.size());
	}

	/**
	 *  NPC's actions when player asks for his reward.
	 */
	class RewardPlayerAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final EventRaiser mayor) {
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
			}
	}

	/**
	 * NPC's answers when player ask for details.
	 */
	class DetailsKillingsAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final EventRaiser mayor) {
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

				sb.append(Grammar.quantityplnoun(kills, RAT_TYPES.get(i), "a"));
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
	 * Set new time period for quest timer (time to next quest phase).
	 * @param max - maximal time in seconds
	 * @param min - minimal time in seconds
	 */
	public static void setNewNotificationTime(int max, int min) {
		TurnNotifier.get().dontNotify(questTimer);
		TurnNotifier.get().notifyInSeconds(
				Rand.randUniform(max, min),	questTimer);
	}

	/**
	 *   add states to NPC's FSM
	 */
	private void prepareNPC() {

		
		// Player asking about rats when quest is inactive
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				Arrays.asList("rats", "rats!"), 
				new TPPQuestInPhaseCondition(INACTIVE),
				ConversationStates.ATTENDING, 
				"Ados isn't being invaded by rats right now. You can still "+
				  "get a #reward for the last time you helped. You can ask for #details "+
				  "if you want.", 
				null);
		
		// Player asking about rats at invasion time.
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				Arrays.asList("rats", "rats!"), 
				new TPPQuestInPhaseCondition(INVASION),
				ConversationStates.ATTENDING, 
				null, 
				new ChatAction() {
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						npc.say("There " + Grammar.isare(rats.size()) + 
								" still about "+Integer.toString(rats.size())+
								" rats alive.");
					}	
				});
		
		// Player asking about rats when quest is neither inactive nor invasion phase
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				Arrays.asList("rats", "rats!"), 
				new AndCondition(
					new NotCondition(new TPPQuestInPhaseCondition(INACTIVE)),
					new NotCondition(new TPPQuestInPhaseCondition(INVASION))),
				ConversationStates.ATTENDING, 
				"The rats are gone. "+
	    		"You can get #reward for your help now, ask about #details "+
				  "if you want to know more.", 
				null);		
		
		// Player asked about reward at invasion time
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				"reward", 
				new TPPQuestInPhaseCondition(INVASION),
				ConversationStates.ATTENDING, 
				"Ados is being invaded by rats! "+
				  "I dont want to reward you now, "+
				  " until all rats are dead.", 
				null);
		
		// Player asked about reward not at invasion time
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				"reward", 
				new NotCondition(new TPPQuestInPhaseCondition(INVASION)),
				ConversationStates.ATTENDING, 
				null, 
				new RewardPlayerAction());
		
		//Player asked about details at invasion time
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				"details", 
				new TPPQuestInPhaseCondition(INVASION),
				ConversationStates.ATTENDING, 
				"Ados is being invaded by rats! "+
				  "I dont want to either reward you or "+
				  "explain details to you now,"+
				  " until all rats are dead.", 
				null);
		
		getMainNPC().add(
				ConversationStates.ATTENDING, 
				"details", 
				new NotCondition(new TPPQuestInPhaseCondition(INVASION)),
				ConversationStates.ATTENDING, 
				null, 
				new DetailsKillingsAction());
	}
	
	/**
	 * first start
	 */
	private void startQuest() {	
		setTimings();
		getPhases().add(new InactivePhase(timings));
		getPhases().add(new InvasionPhase(timings, rats));
		getPhases().add(new AwaitingPhase(timings));
		setNewNotificationTime(
				getDefaultPhaseClass().getMinTimeOut(),
				getDefaultPhaseClass().getMaxTimeOut());
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
		questTimer = new QuestTimer();
		fillQuestInfo(
				"The Pied Piper",
				"Ados city has a rats problem periodically.",
				true);
				
		prepareNPC();
		super.addToWorld();
		startQuest();
	}

	public static void setPhase(TPP_Phase phase) {
		ThePiedPiper.phase = phase;
	}

	public static TPP_Phase getPhase() {
		return phase;
	}

	public static void setPhases(LinkedList<ITPPQuest> phases) {
		ThePiedPiper.phases = phases;
	}

	public static LinkedList<ITPPQuest> getPhases() {
		return phases;
	}

}
