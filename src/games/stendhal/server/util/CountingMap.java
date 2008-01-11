package games.stendhal.server.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * A map which assigns a unique id to every entry.
 * 
 * @author hendrik
 * @param <V>
 *            value type
 */
public class CountingMap<V> implements Serializable,
		Iterable<Map.Entry<String, V>> {

	private static final long serialVersionUID = -4142274943695729582L;
	private Map<V, String> map = new HashMap<V, String>();
	private Map<String, V> mapKeys = new TreeMap<String, V>();
	private String prefix;
	private int counter = 0;

	/**
	 * Creates a new counting map.
	 * 
	 * @param prefix
	 *            prefix
	 */
	public CountingMap(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Adds a new entry to the map unless it is already part of it.
	 * 
	 * @param value
	 *            entry
	 * @return key
	 */
	public String add(V value) {
		String key = map.get(value);
		if (key == null) {
			key = prefix + counter;
			map.put(value, key);
			mapKeys.put(key, value);
			counter++;
		}
		return key;
	}

	public Iterator<Map.Entry<String, V>> iterator() {
		return mapKeys.entrySet().iterator();
	}
}
