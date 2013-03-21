package games.stendhal.server.entity.item;

import java.util.Map;
/**
 * A ring that protects the player from poisoning by a certain percentage
 * depending on karma level.
 * 
 * @author AntumDeluge
 */
public class AntivenomRing extends Ring {
	
	/*
	 * Minimum and maximum protection that ring provides.
	 */
	private static float MIN_PROTECT = 0.25f;
	private static float MAX_PROTECT = 0.50f;

	/**
	 * Creates a new antivenom ring.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public AntivenomRing(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		setPersistent(true);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public AntivenomRing(final AntivenomRing item) {
		super(item);
	}
}