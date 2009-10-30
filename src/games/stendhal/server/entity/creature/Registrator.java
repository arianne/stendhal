package games.stendhal.server.entity.creature;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
  * Wrapper for java Observable object.
  */
class Registrator extends Observable {
	
	/**
	 * mark Registrator object as changed.
	 * Function was moved from protected (in java.util.Observable) 
	 * to public zone. 
	 */
	public void setChanges() {
		setChanged();
	};
	
	/**
	 * registers observer for notifying
	 * @param observer
	 * 			- observer to add
	 */	
	public void setObserver(final Observer observer) {
		if(observer != null) {
			addObserver(observer); 		
		} else {
			// log it.
			final Logger logger = Logger.getLogger(Registrator.class);
			logger.error("null observer was not added.", new Throwable());
		};
	}

	/**
	 * remove observer from observers list.
	 * @param observer
	 * 			- observer to remove.
	 */
	public void removeObserver(final Observer observer) {
		deleteObserver(observer);
	}

}
