/*
 * RuleManager.java
 *
 * Created on 20. August 2005, 12:23
 *
 */

package games.stendhal.server.core.rule;

/**
 * Combines the different managers...
 * 
 * @author Matthias Totz
 */
public interface RuleManager {

	/**
	 * returns the EntityManager for this ruleset.
	 * 
	 * @return the EntityManager for this ruleset
	 */
	EntityManager getEntityManager();

	/**
	 * returns the ActionManager for this ruleset.
	 * 
	 * @return the ActionManager for this ruleset
	 */
	ActionManager getActionManager();
}
