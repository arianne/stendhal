package games.stendhal.server.entity.modifier;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Abstract handler for modifiers
 * 
 * @author madmetzger
 */
public abstract class AbstractModifierHandler {
	
	private final ModifiedAttributeUpdater affectedEntity;
	
	private Collection<AttributeModifier> modifiers;
	
	/**
	 * Create a new AbstractModifierHandler
	 */
	protected AbstractModifierHandler(ModifiedAttributeUpdater affectedEntity) {
		this.modifiers = new TreeSet<AttributeModifier>();
		this.affectedEntity = affectedEntity;
		TurnListener listener = new TurnListenerDecorator(new ModifierCleanUpTurnListener(this));
		SingletonRepository.getTurnNotifier().notifyInSeconds(1, listener);
	}

	public void addModifier(AttributeModifier am) {
		this.modifiers.add(am);
		this.affectedEntity.updateModifiedAttributes();
	}

	/**
	 * Get all managed AttributeModifiers
	 * 
	 * @return the collection of modifiers
	 */
	protected Collection<AttributeModifier> getModifiers() {
		return modifiers;
	}
	
	/**
	 * Clean up the managed modifiers and remove modifiers that are expired
	 */
	public void removeExpiredModifiers() {
		Iterator<AttributeModifier> iterator = modifiers.iterator();
		boolean updateNeeded = false;
		while(iterator.hasNext()) {
			AttributeModifier am = iterator.next();
			if(am.isExpired()) {
				iterator.remove();
				updateNeeded = true;
			}
		}
		if(updateNeeded) {
			this.affectedEntity.updateModifiedAttributes();
		}
	}
	
}
