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
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.MultiZonesFixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.quests.ThePiedPiper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import marauroa.common.Pair;

public class AwaitingPhase extends TPPQuest {
	private SpeakerNPC piedpiper = new SpeakerNPC("Pied Piper");	
	private int minPhaseChangeTime;
	private int maxPhaseChangeTime;
	private LinkedList<Pair<StendhalRPZone, LinkedList<Node>>> fullpath = 
		new LinkedList<Pair<StendhalRPZone, LinkedList<Node>>>();

	/**
	 * constructor
	 * @param timings 
	 * - a pair of time parameters for phase timeout
	 */
	public AwaitingPhase(Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime = timings.get(AWAITING_TIME_MIN);
		maxPhaseChangeTime = timings.get(AWAITING_TIME_MAX);
	}

	public void prepare() {
		createPiedPiper();
	}
	
	/**
	 * class for make one npc follower of other.
	 * @author yoriy
	 */
	class NPCFollowing implements Observer {
		SpeakerNPC follower;
		SpeakerNPC leader;
		Observer chatting;
		
		/**
		 * constructor
		 * @param leader - NPC for follow him.
		 * @param follower - follower of leader.
		 */
		public NPCFollowing(SpeakerNPC leader, SpeakerNPC follower, Observer chatting) {
			this.leader=leader;
			this.follower=follower;
			this.chatting=chatting;
		}
		
		public void update(Observable o, Object arg) {
			follower.clearPath();
			follower.pathnotifier.deleteObservers();
			moveToProperDistance();
		}
		
		/**
		 * return 1/3 of follower's path
		 * @param path
		 * @return - a part of path
		 */
		public FixedPath getOneThirdOfPath(FixedPath path) {
			LinkedList<Node> templ = new LinkedList<Node>();
			for(int i=0; i<path.getNodeList().size()/2; i++) {
				templ.add(path.getNodeList().get(i));
			};
			FixedPath temp= new FixedPath(templ, false);
			return(temp);
		}
		
		/**
		 * move follower close to leader.
		 */
		private void moveToProperDistance() {
			double dist=leader.squaredDistance(follower); 
			if (dist>6) {			
				follower.setMovement(leader, 0, 5, dist*1.5);
				follower.setPath(getOneThirdOfPath(follower.getPath()));
				follower.pathnotifier.addObserver(this);
			} else {
				follower.stop();
				follower.pathnotifier.deleteObservers();
				chatting.update(null, null);
			}
		}		
	}
	
	/**
	 * chatting between 2 npcs
	 * @author yoriy
	 */
	class NPCChatting implements Observer, TurnListener {
		protected SpeakerNPC mayor;
		protected SpeakerNPC piper;
		protected ITPPQuest phase;
		protected int count=0;
		protected final LinkedList<String> conversations = new LinkedList<String>(); 


		
		private void fillConversations() {
			//piper
			conversations.add("Good day, Mayor Chalmers. What did you call me here for?");
			//mayor
			conversations.add("Hello, very glad to see our respectable hero here. Who hasn't heard about you, there is almost...");
			//piper
			conversations.add("Please talk about your business to me, my time is precious.");
			//mayor
			conversations.add("... ok, what was I saying? Ah yes, our city has a little problem with #rats.");
			//piper
			conversations.add("Again?");
			//mayor
			conversations.add("Yes, these animals are too stupid to remember a lesson they learnt only recently.");
			//piper
			conversations.add("I can help, if you are ready to pay.");
			//mayor
			conversations.add("Ados City has no other way to eliminate this nuisance. We will pay you.");
			//piper
			conversations.add("Do you know my usual price?");
			//mayor
			conversations.add("Yes, I have it written somewhere in my papers.");
			//piper
			conversations.add("Good. I will return for my reward soon, please prepare it.");
			//mayor
			conversations.add("Don't worry, how can I break your trust in me and my city?");
		}
		
		/**
		 * constructor
		 * @param mayor - first npc
		 * @param piper - second npc
		 * @param phase - phase class object
		 */
		public NPCChatting(SpeakerNPC mayor, SpeakerNPC piper, ITPPQuest phase) {
			this.mayor=mayor;
			this.piper=piper;
			this.phase=phase;
			fillConversations();
		}
		
		private void setupDialog() {
			if(mayor.isTalking()) {
				mayor.say("Sorry, "+mayor.getAttending().getName()+
						" but I see that our city's savoiur is here. I have to speak with him quickly."+
						" Please speak with me again after we finish talking.");
				mayor.setCurrentState(ConversationStates.IDLE);
			};
			mayor.setCurrentState(ConversationStates.ATTENDING);
			mayor.setAttending(piper);
			mayor.stop();
			//fp = new FixedPath(new LinkedList<Node>(mayor.getPath().getNodeList()),	mayor.getPath().isLoop());
			//mayor.clearPath();
			onTurnReached(0);
		}
		
		public void update(Observable o, Object arg) {
			piper.clearPath();
			piper.pathnotifier.deleteObservers();
			count=0;
			setupDialog();
		}

		public void onTurnReached(int currentTurn) {
			piper.faceToward(mayor);
			mayor.faceToward(piper);
			if((count%2)==0) {
				piper.say(conversations.get(count));
			} else {
				mayor.say(conversations.get(count));
			};
			count++;
			if(count==conversations.size()) {
				TurnNotifier.get().dontNotify(this);
				mayor.setCurrentState(ConversationStates.IDLE);
				mayor.followPath();
				phase.phaseToNextPhase(
						ThePiedPiper.getNextPhaseClass(ThePiedPiper.getPhase()), 
						Arrays.asList("normal switching"));
				return;
			};
			TurnNotifier.get().dontNotify(this);
			TurnNotifier.get().notifyInSeconds(8, this);
		}
	}	
	
	/**
	 * prepare NPC to walk through his multizone path.
	 */
	private void leadNPC() {
		final StendhalRPZone zone = fullpath.get(0).first();
		int x=fullpath.get(0).second().get(0).getX();
		int y=fullpath.get(0).second().get(0).getY();
		piedpiper.setPosition(x, y);
		piedpiper.pathnotifier.setObserver(
				new MultiZonesFixedPath(piedpiper, fullpath, 
						new NPCFollowing(ThePiedPiper.getMainNPC(), piedpiper,
								new NPCChatting(ThePiedPiper.getMainNPC(), piedpiper, this))));
		piedpiper.setPath(new FixedPath(fullpath.get(0).second(), false));
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
		return TPP_Phase.TPP_AWAITING;
	}
	
	/**
	 * function for creating pied piper npc
	 */
	private void createPiedPiper() {
		piedpiper.setEntityClass("holidaymakernpc");
		piedpiper.initHP(1000);
		piedpiper.setResistance(0);
		piedpiper.setVisibility(100);
		piedpiper.add(ConversationStates.IDLE, 
							ConversationPhrases.GREETING_MESSAGES, 
							null, 
							ConversationStates.IDLE, 
							"hello", 
							null);
		fullpath=PathesBuildHelper.getAwaitingPhasePath();
		leadNPC();
		}
	
	/**
	 * function will remove piped piper npc object
	 */
	private void destroyPiedPiper() {
		piedpiper.getZone().remove(piedpiper);
	}		
	
}

