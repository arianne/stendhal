package games.stendhal.server.entity.portal;

import games.stendhal.server.entity.RPEntity;

import javax.management.AttributeNotFoundException;

/**
 * A KeyDestroyingDoor works just like a normal locked door, except that it
 * destroys the key item when used. By using the requiredAmount parameter,
 * it can also be programmed to consume more than one key item each time.
 * 
 * Note that the item needn't be a real key; one could, for instance, also
 * create a door that costs 5 pieces of gold to use.
 * 
 * If you add such a door to the world, don't forget to add a sign or an
 * NPC which tells the player how the door works.
 * 
 * @author daniel
 */
public class KeyDestroyingDoor extends LockedDoor {

	/**
	 * The number of key items that are destroyed while
	 * passing through the door
	 */
	private int requiredAmount;

	/**
	 * Creates a new KeyDestroying door which destroys 1 key when the player
	 * uses it.
	 * @param key The name of the item that is required to use the door
	 * @param clazz The class. Responsible for how this door looks like.
	 * @param dir The direction in which one has to walk in order to pass
	 *            through this door
	 * @throws AttributeNotFoundException
	 */
	public KeyDestroyingDoor(String key, String clazz) {
		this(key, clazz, 1);
	}

	/**
	 * Creates a new KeyDestroying door.
	 * @param key The name of the item that is required to use the door
	 * @param clazz The class. Responsible for how this door looks like.
	 * @param dir The direction in which one has to walk in order to pass
	 *            through this door
	 * @param requiredAmount The number of key items that are destroyed while
	 *                       passing through the door
	 * @throws AttributeNotFoundException
	 */
	public KeyDestroyingDoor(String key, String clazz, int requiredAmount) {
		super(key, clazz);

		this.requiredAmount = requiredAmount;
	}

	@Override
	public void onUsed(RPEntity user) {
		if (has("locked") && user.isEquipped(get("locked"), requiredAmount)) {
			super.onUsed(user);
			// destroy key(s)
			user.drop(get("locked"), requiredAmount);
		}
	}
}
