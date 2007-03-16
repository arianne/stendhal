package games.stendhal.server.events;

/**
 * Equipable Entities implement this interface EquipListener.
 *
 * @author hendrik
 */
public interface EquipListener {

	/**
	 * Checks whether this object can be equipped in the
	 * given slot.
	 *
	 * @return true, if it can be equipped; false otherwise
	 */
	public boolean canBeEquippedIn(String slot);

}
