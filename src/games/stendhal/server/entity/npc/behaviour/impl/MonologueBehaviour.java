// $Id$
package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * causes the speaker npc to loop a repeated monologue while he is not attending a player.
 * the text for repeating can have more than one option, in which case he says each in turn.
 *
 * @author kymara
 */
public final class MonologueBehaviour implements TurnListener {


	private final SpeakerNPC speakerNPC;
	private final String[] repeatedText;
	private int i = 0;
	private int minutes;

	/**
	 * Creates a new MonologueBehaviour.
	 *
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 * @param minutes
	 * 			  after how many minutes to repeat text
	 */
	public MonologueBehaviour(final SpeakerNPC speakerNPC,
			final String[] repeatedText, final int minutes) {
		this.speakerNPC = speakerNPC;
		this.repeatedText = repeatedText;
		this.minutes = minutes;
		SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		if (speakerNPC.getEngine().getCurrentState() == ConversationStates.IDLE) {
			speakerNPC.say(repeatedText[i % repeatedText.length]);
			speakerNPC.setCurrentState(ConversationStates.IDLE);
			if (i == Integer.MAX_VALUE) {
				// deal with overflow (only takes 9 hours :P)
				// probably means there is a better way to do it, but this should work...
				i = 0;
			} else {
				i = i + 1;
			}
		}
		// Schedule so we are notified again in
		SingletonRepository.getTurnNotifier().notifyInSeconds(minutes*60, this);
	}
}
