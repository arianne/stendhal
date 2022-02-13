/* $Id$ */
/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.piedpiper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


public class InvasionPhase extends TPPQuest {

	private final int minPhaseChangeTime;
	private final int maxPhaseChangeTime;
	protected LinkedList<Creature> rats = new LinkedList<Creature>();

	private void addConversations(final SpeakerNPC mainNPC) {
		TPP_Phase myphase = INVASION;

		// Player asking about rats at invasion time.
		mainNPC.add(
				ConversationStates.ATTENDING,
				Arrays.asList("rats", "rats!"),
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						npc.say("There " + Grammar.isare(TPPQuestHelperFunctions.getRats().size()) +
								" still about "+Integer.toString(TPPQuestHelperFunctions.getRats().size())+
								" rats alive.");
					}
				});

		//Player asked about details at invasion time.
		mainNPC.add(
				ConversationStates.ATTENDING,
				"details",
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING,
				"Ados is being invaded by rats! "+
				  "I dont want to either reward you or "+
				  "explain details to you now,"+
				  " until all rats are dead.",
				null);

		// Player asked about reward at invasion time.
		mainNPC.add(
				ConversationStates.ATTENDING,
				"reward",
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING,
				"Ados is being invaded by rats! "+
				  "I dont want to reward you now, "+
				  " until all rats are dead.",
				null);
	}

	/**
	 * Create InvasionPhase.
	 *
	 * @param timings
	 */
	public InvasionPhase(Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime = timings.get(INVASION_TIME_MIN);
		maxPhaseChangeTime = timings.get(INVASION_TIME_MAX);
		this.rats=TPPQuestHelperFunctions.getRats();
		addConversations(TPPQuestHelperFunctions.getMainNPC());
	}


	@Override
	public int getMinTimeOut() {
		return minPhaseChangeTime;
	}


	@Override
	public int getMaxTimeOut() {
		return maxPhaseChangeTime;
	}



	/**
	 * rats invasion starts :-)
	 * Iterate through each zone and select the min and max rat count based on zone size
	 * Places rat if possible, if not skip this rat (so if 6 rats chosen perhaps only 3 are placed)
	 */
	private void summonRats() {

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
				final Creature tempCreature = TPPQuestHelperFunctions.getRandomRat();
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
				if (tempCreature.getName().equals("archrat")) {
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
				}

				StendhalRPAction.placeat(zone, rat, x, y);
				rats.add(rat);
			}
		}
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
			phaseToDefaultPhase(
					new LinkedList<String>(Arrays.asList("pied piper")));
		}
    }

	/**
	 *  Rats are dead :-)
	 */

	@Override
	public String getSwitchingToDefPhaseMessage() {
		final String text = "Mayor Chalmers shouts: No #rats in Ados survived, "+
				            "only those who always lived in the "+
				            "haunted house. "+
				            "Rat hunters are welcome to get their #reward.";
		return(text);
	}

	/**
	 *  Rats now living under all buildings. Need to call Pied Piper :-)
	 */
	@Override
	public String getSwitchingToNextPhaseMessage() {
		final String text =
			"Mayor Chalmers shouts: Suddenly, #rats have captured the city, "+
		  //"Mayor Chalmers shouts: The #rats left as suddenly as they arrived. "+
		  //"Perhaps they have returned to the sewers. "+
			"I now need to call the Pied Piper, a rat exterminator. "+
			"Anyway, thanks to all who tried to clean up Ados, "+
			" you are welcome to get your #reward.";
		return(text);
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
			}
		}
	}

	/**
	 * Red alert! Rats in the Ados city!
	 *
	 * @return Ados mayor's call for help message
	 */
	protected String ratsProblem() {
		final String text = "Mayor Chalmers shouts: Ados City is being invaded by #rats!"+
			              " Anyone who will help to clean up the city, will be rewarded!";
		return(text);
	}

	@Override
	public void prepare() {
		summonRats();
		super.startShouts(timings.get(SHOUT_TIME), ratsProblem());
	}

	@Override
	public void phaseToDefaultPhase(List<String> comments) {
		comments.add("last rat killed");
		super.phaseToDefaultPhase(comments);
	}


	@Override
	public void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments) {
		comments.add("switch phase, "+rats.size()+" rats still alive.");
		removeAllRats();
		super.phaseToNextPhase(nextPhase, comments);
	}

    /**
     *  Implementation of Observer interface.
     *  Update function will record the fact of rat's killing
     *  in player's quest slot.
     */
	class RatsObserver implements Observer {
		@Override
		public void update (Observable obj, Object arg) {
	        if (arg instanceof CircumstancesOfDeath) {
	    		final CircumstancesOfDeath circs=(CircumstancesOfDeath)arg;
	        	if(RAT_ZONES.contains(circs.getZone().getName())) {
	        	if(circs.getKiller() instanceof Player) {
	        		final Player player = (Player) circs.getKiller();
	        		killsRecorder(player, circs.getVictim());
	        	}
	        	notifyDead(circs.getVictim());
	        	}
	        }
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
		}

		if((player.getQuest(QUEST_SLOT)==null)||
		   (player.getQuest(QUEST_SLOT).equals("done")||
		   (player.getQuest(QUEST_SLOT).equals("")))){
			// player just killed his first creature.
		    player.setQuest(QUEST_SLOT, "rats;0;0;0;0;0;0");
		}

		// we using here and after "i+1" because player's quest index 0
		// is occupied by quest stage description.
		if ("".equals(player.getQuest(QUEST_SLOT,i+1))){
			// something really wrong, will correct this...
			player.setQuest(QUEST_SLOT,"rats;0;0;0;0;0;0");
		}
		int kills;
		try {
			kills = Integer.parseInt(player.getQuest(QUEST_SLOT, i+1))+1;
		} catch (NumberFormatException nfe) {
			// have no records about this creature in player's slot.
			// treat it as he never killed this creature before.
			kills=1;
		}
		player.setQuest(QUEST_SLOT, i+1, Integer.toString(kills));
	}


	@Override
	public TPP_Phase getPhase() {
		return TPP_Phase.TPP_INVASION;
	}



}
