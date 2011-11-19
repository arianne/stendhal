/**
 * 
 */
package games.stendhal.server.entity.modifier;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;

/**
 * This TurnListener regularly cleans up expired modifiers handled by
 * an AbstractModifierHandler and its subclasses
 * 
 * @author madmetzger
 */
public class ModifierCleanUpTurnListener implements TurnListener {
	
	private final AbstractModifierHandler modifierHandler;
	
	public ModifierCleanUpTurnListener(AbstractModifierHandler handler) {
		this.modifierHandler = handler;
	}
	
	public void onTurnReached(int currentTurn) {
		this.modifierHandler.removeExpiredModifiers();
		SingletonRepository.getTurnNotifier().notifyInSeconds(1, new TurnListenerDecorator(this));
	}

}
