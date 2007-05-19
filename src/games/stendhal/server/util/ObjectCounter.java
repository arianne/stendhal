package games.stendhal.server.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Counts objects
 *
 * @author hendrik
 */
public class ObjectCounter {
	private Map<Object, Integer> counter = new HashMap<Object, Integer>();

	public void clear() {
		counter.clear();
	}

	public Map<Object, Integer> getMap() {
		return counter;
	}
	
	public void add(Object o) {
		Integer in = counter.get(o);
		if (in == null) {
			in = new Integer(1);
		} else {
			in = new Integer(in.intValue() + 1);
		}
		counter.put(o, in);
	}
}
