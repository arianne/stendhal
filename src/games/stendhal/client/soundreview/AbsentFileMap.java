package games.stendhal.client.soundreview;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * AbsentFilemap implements the NullObject Pattern for SoundFileMap
 * 
 */
class AbsentFileMap implements Map<String, byte[]>, Nullable {

	public void clear() {

	}

	public boolean containsKey(final Object key) {

		return false;
	}

	public boolean containsValue(final Object value) {

		return false;
	}

	public Set<java.util.Map.Entry<String, byte[]>> entrySet() {

		return null;
	}

	public byte[] get(final Object key) {

		return null;
	}

	public boolean isEmpty() {

		return true;
	}

	public Set<String> keySet() {

		return null;
	}

	public byte[] put(final String key, final byte[] value) {
		throw new IllegalStateException("not yet created");
	}

	public void putAll(final Map<? extends String, ? extends byte[]> t) {
		throw new IllegalStateException("not yet created");

	}

	public byte[] remove(final Object key) {

		return null;
	}

	public int size() {

		return 0;
	}

	public Collection<byte[]> values() {
		return null;
	}

	public boolean isNull() {
		return true;
	}

}
