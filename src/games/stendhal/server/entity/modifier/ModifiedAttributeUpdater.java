/**
 * 
 */
package games.stendhal.server.entity.modifier;

/**
 * Interface for Entities, that have modified attributes needing an update
 * 
 * @author madmetzger
 */
public interface ModifiedAttributeUpdater {
	
	/**
	 * Update the modified attributes values
	 */
	public void updateModifiedAttributes();

}
