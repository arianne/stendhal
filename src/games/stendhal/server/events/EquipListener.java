package games.stendhal.server.events;

/**
 * Equipable Entities implement this interface EquipListener.
 *
 * @author hendrik
 */
public interface EquipListener {

	/**
	 * checks whether this object can be equiped
	 *
	 * @return true, if it can be equiped; false otherwise
	 */
	public boolean canBeEquiped();
}
