package games.stendhal.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Counts objects
 * 
 * @author hendrik
 * @param <K>
 *            Type of objects to count
 */
public class ObjectCounter<K> {
	private Map<K, Integer> counter = new HashMap<K, Integer>();

	/**
	 * clears the counter
	 */
	public void clear() {
		counter.clear();
	}

	/**
	 * gets the counter map
	 * 
	 * @return Map
	 */
	public Map<K, Integer> getMap() {
		return counter;
	}

	/**
	 * adds one to the appropriate entry
	 * 
	 * @param o
	 *            object
	 */
	public void add(K o) {
		Integer in = counter.get(o);
		if (in == null) {
			in = Integer.valueOf(1);
		} else {
			in += Integer.valueOf(1);
		}
		counter.put(o, in);
	}
}
