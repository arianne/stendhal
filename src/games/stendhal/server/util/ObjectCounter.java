package games.stendhal.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Counts objects.
 * 
 * @author hendrik
 * @param <K>
 *            Type of objects to count
 */
public class ObjectCounter<K> {
	private final Map<K, Integer> counter = new HashMap<K, Integer>();

	/**
	 * Clears the counter.
	 */
	public void clear() {
		counter.clear();
	}

	/**
	 * Gets the counter map.
	 * 
	 * @return Map
	 */
	public Map<K, Integer> getMap() {
		return counter;
	}

	/**
	 * Adds one to the appropriate entry.
	 * 
	 * @param o
	 *            object
	 */
	public void add(final K o) {
		Integer in = counter.get(o);
		if (in == null) {
			in = Integer.valueOf(1);
		} else {
			in += Integer.valueOf(1);
		}
		counter.put(o, in);
	}
}
