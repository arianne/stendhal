package games.stendhal.server.entity.item;

import java.util.Map;

/**
 * A key that matches if the identifier and lock number are the right. 
 */
public class HouseKey extends Item {
	public HouseKey(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		
		setInfoString("nowhere;0;");
	}

	/**
	 * Copy constructor.
	 * 
	 * @param key the key to be copied.
	 */
	public HouseKey(final HouseKey key) {
		super(key);
	}
	
	@Override
	public String describe() {
		return "You see a key to " + getId() + ".";
	}
	
	/**
	 * Set the paramaters of the key.
	 * 
	 * @param id the portal identifier
	 * @param lockNumber the number of the lock
	 * @param owner the owner of the house
	 */
	public void setup(final String id, final int lockNumber, String owner) {
		if (owner == null) {
			owner = "";
		}
		setInfoString(id + ";" + lockNumber + ";" + owner);
		// TODO: decide the sprite name
	}
	
	/**
	 * Check if the key matches a portal.
	 * 
	 * @param houseId identifier of the portal
	 * @param number number of the lock
	 * @return true if the key fits the lock, false otherwise
	 */
	public boolean matches(final String houseId, final int number) {
		final String[] info = getInfoString().split(";");
		
		int keyNumber = Integer.parseInt(info[1]);
		return (info[0].equals(houseId) && keyNumber == number);
	}
	
	/**
	 * Get the portal identifier.
	 *  
	 * @return the identifier of the portal
	 */
	private String getId() {
		final String[] info = getInfoString().split(";");
		
		return info[0];
	} 
}
