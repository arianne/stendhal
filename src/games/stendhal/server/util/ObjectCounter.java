package games.stendhal.server.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Counts objects
 *
 * @author hendrik
 * @param <K> Type of objects to count
 */
public class ObjectCounter<K> {
	private Map<K, Integer> counter = new HashMap<K, Integer>();

	public void clear() {
		counter.clear();
	}

	public Map<K, Integer> getMap() {
		return counter;
	}
	
	public void add(K o) {
		Integer in = counter.get(o);
		if (in == null) {
			in = new Integer(1);
		} else {
			in = new Integer(in.intValue() + 1);
		}
		counter.put(o, in);
	}
}
