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
package games.stendhal.server.entity.npc.interaction;

import java.util.List;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
 * chatting between 2 NPCs
 * @author yoriy
 */
public final class NPCChatting implements Observer, TurnListener {
	private final SpeakerNPC first;
	private final SpeakerNPC second;
	private int count=0;
	private final List<String> conversations;
	final private Observer next;
	final String explainations;


	/**
	 * constructor
	 * @param first - first npc (who strarting conversation)
	 * @param second - second npc
	 * @param conversations
	 * @param explainations
	 * @param n - observer n
	 */
	public NPCChatting(
			SpeakerNPC first,
			SpeakerNPC second,
			List<String> conversations,
			String explainations,
			Observer n) {
		this.first=first;
		this.second=second;
		this.conversations=conversations;
		this.explainations=explainations;
		this.next=n;

	}

	private void setupDialog() {
		if(second.isTalking()) {
			second.say("Sorry, "+second.getAttending().getName()+
					" but "+explainations);
			second.setCurrentState(ConversationStates.IDLE);
		}
		second.setCurrentState(ConversationStates.ATTENDING);
		second.setAttending(first);
		second.stop();
		onTurnReached(0);
	}

	@Override
	public void update(Observable o, Object arg) {
		first.clearPath();
		first.pathnotifier.deleteObservers();
		count=0;
		setupDialog();
	}

	@Override
	public void onTurnReached(int currentTurn) {
		first.faceToward(second);
		second.faceToward(first);
		if((count%2)==0) {
			first.say(conversations.get(count));
		} else {
			second.say(conversations.get(count));
		}
		count++;
		if(count==conversations.size()) {
			TurnNotifier.get().dontNotify(this);
			second.setCurrentState(ConversationStates.IDLE);
			second.followPath();
            // going ahead
			next.update(null, null);
			return;
		}
		TurnNotifier.get().dontNotify(this);
		TurnNotifier.get().notifyInSeconds(8, this);
	}
}
