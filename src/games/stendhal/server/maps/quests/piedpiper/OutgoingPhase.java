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
package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.MultiZonesFixedPath;
import games.stendhal.server.core.pathfinder.RPZonePath;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class OutgoingPhase extends TPPQuest {
	private final SpeakerNPC piedpiper = new SpeakerNPC("Pied Piper");	
	private final SpeakerNPC mainNPC = TPPQuestHelperFunctions.getMainNPC();	
	private final int minPhaseChangeTime;
	private int maxPhaseChangeTime;
	private List<List<RPZonePath>> fullpath = 
		new LinkedList<List<RPZonePath>>();
	
	private void addConversations() {
		TPP_Phase myphase = OUTGOING;
				
		// Player asking about rats
		mainNPC.add(
				ConversationStates.ATTENDING, 
				Arrays.asList("rats", "rats!"), 
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING, 
				"The rats are gone. "+
	    		"You can get a #reward for your help now, ask about #details "+
				  "if you want to know more.", 
				null);	
		
		// Player asking about details.
		mainNPC.add(
				ConversationStates.ATTENDING, 
				"details", 
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING, 
				null, 
				new DetailsKillingsAction());
		
		// Player asked about reward
		mainNPC.add(
				ConversationStates.ATTENDING, 
				"reward", 
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING, 
				null, 
				new RewardPlayerAction());		
		

	}

	/**
	 * constructor
	 * @param timings 
	 * - a pair of time parameters for phase timeout
	 */
	public OutgoingPhase(final Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime = timings.get(AWAITING_TIME_MIN);
		maxPhaseChangeTime = timings.get(AWAITING_TIME_MAX);
		addConversations();
	}

	public void prepare() {
		createPiedPiper();
	}
	
	/**
	 * class for creating complete route of npc
	 * across his world's path
	 */
	class MultiZonesFixedPathList implements Observer {

		/**
		 * 
		 */
		public void update(Observable arg0, Object arg1) {
			
		}		
		
		/**
		 * constructor
		 * @param pathes - list of all routes of npc across the world
		 * @param o - observer for notifying about each route's over.
		 */
		public MultiZonesFixedPathList(final List<MultiZonesFixedPath> pathes, Observer o) {
			
		}
		
	}
	
	/**
	 * class for adding a random rat to a chain 
	 * when piper staying near house's door
	 */
	class AttractRat implements Observer {

		public void update(Observable arg0, Object arg1) {
          
			
		}
		
	}

	/**
	 * prepare NPC to walk through his multizone path.
	 */
	private void leadNPC() {
		final StendhalRPZone zone = fullpath.get(0).get(0).getZone();
		final int x=fullpath.get(0).get(0).getPath().get(0).getX();
		final int y=fullpath.get(0).get(0).getPath().get(0).getY();
		piedpiper.setPosition(x, y);
		piedpiper.pathnotifier.setObserver(
				new MultiZonesFixedPath(piedpiper, fullpath.get(0), 
						new AttractRat()));
		piedpiper.setPath(new FixedPath(fullpath.get(0).get(0).getPath(), false));
		zone.add(piedpiper);
	}
	
	public int getMinTimeOut() {
		return minPhaseChangeTime;
	}
	

	public int getMaxTimeOut() {
		return maxPhaseChangeTime;
	}


	public void phaseToDefaultPhase(List<String> comments) {
		destroyPiedPiper();
		super.phaseToDefaultPhase(comments);		
	}


	public void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments) {
		destroyPiedPiper();
		super.phaseToNextPhase(nextPhase, comments);
	}
	
	
	/*
	 *  Pied Piper sent rats away:-)
	 */
	public String getSwitchingToNextPhaseMessage() {
		final String text = 
			"Mayor Chalmers shouts: Thankfully, all the #rats are gone now, " +
			"the Pied Piper hypnotized them and led them away to the dungeons. "+
			"Those of you who helped Ados City with the rats problem "+
			"can get your #reward now.";		
		return text;
	}

	public TPP_Phase getPhase() {
		return TPP_Phase.TPP_OUTGOING;
	}
	
	/**
	 * function for creating pied piper npc
	 */
	private void createPiedPiper() {
		TPPQuestHelperFunctions.setupPiper(piedpiper);
		fullpath = PathsBuildHelper.getAdosCollectingRatsPaths();
		leadNPC();
	}
	
	/**
	 * function will remove piped piper npc object
	 */
	private void destroyPiedPiper() {
		piedpiper.getZone().remove(piedpiper);
	}		
	
}

