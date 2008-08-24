package games.stendhal.client;

import marauroa.common.game.RPObject;

/**
 * is used by {@link PerceptionToObject}. 
 * 
 * Any Class implementing this can be used to listen to changes in RPObjects.
 * 
 * @author astrid
 *
 */
public interface ObjectChangeListener {

	void deleted();
	void modifiedAdded(RPObject changes);
	void modifiedDeleted(RPObject changes);

}
