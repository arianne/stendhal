package games.stendhal.server.entity.modifier;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;

import java.util.Collection;
import java.util.Collections;
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

	private TurnListener listener;
	
	/**
	 * Create a new AbstractModifierHandler
	 */
	protected AbstractModifierHandler(ModifiedAttributeUpdater affectedEntity) {
		this.modifiers = new TreeSet<AttributeModifier>();
		this.affectedEntity = affectedEntity;
	}

	public void addModifier(AttributeModifier am) {
		this.modifiers.add(am);
		if(this.listener == null) {
			this.listener = new ModifierCleanUpTurnListener(this);
			int seconds = Long.valueOf(this.getSecondsTillNextExpire()).intValue();
			SingletonRepository.getTurnNotifier().notifyInSeconds(seconds + 1, listener);
		}
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
	
	public long getSecondsTillNextExpire() {
		long next = 0;
		AttributeModifier min = Collections.min(this.modifiers);
		if(min != null) {
			next = min.getMillisecondsTillExpire();
		}
		return next / 1000;
	}

}
