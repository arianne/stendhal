package games.stendhal.client.soundreview;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class SoundFileMap implements Map<String, byte[]>, Nullable {

	private Map<String, byte[]> fileMap = new AbsentFileMap();

	SoundFileReader sfr;

	public SoundFileMap() {
		sfr = new SoundFileReader();
		sfr.init();
	}

	public void clear() {
		fileMap.clear();

	}

	public boolean containsKey(final Object key) {
		return fileMap.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return fileMap.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, byte[]>> entrySet() {
		return fileMap.entrySet();

	}

	public byte[] get(final Object key) {
		byte[] byteArray = fileMap.get(key);
		if (byteArray == null) {

			byteArray = sfr.getData((String) key);
			if (byteArray != null) {
				put((String) key, byteArray);
				// else
				// System.out.println(key+ "not found in filesystem");
			}
		}
		return byteArray;
	}

	public boolean isEmpty() {
		return fileMap.isEmpty();
	}

	public Set<String> keySet() {
		return fileMap.keySet();
	}

	public byte[] put(final String key, final byte[] value) {
		if (value == null) {
			throw new NullPointerException();
		}
		try {
			return fileMap.put(key, value);
		} catch (final IllegalStateException e) {
			fileMap = Collections.synchronizedMap(new HashMap<String, byte[]>());
			return fileMap.put(key, value);
		}
	}

	public void putAll(final Map<? extends String, ? extends byte[]> t) {
		try {
			fileMap.putAll(t);
		} catch (final IllegalStateException e) {
			fileMap = Collections.synchronizedMap(new HashMap<String, byte[]>());
			fileMap.putAll(t);
		}

	}

	public byte[] remove(final Object key) {

		return fileMap.remove(key);
	}

	public int size() {

		return fileMap.size();
	}

	public Collection<byte[]> values() {
		return fileMap.values();
	}

	public boolean isNull() {

		return (fileMap instanceof AbsentFileMap);
	}

}
