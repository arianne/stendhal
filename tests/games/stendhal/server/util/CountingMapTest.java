package games.stendhal.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * Tests the CountingMap class.
 * 
 * @author Martin Fuchs
 */
public class CountingMapTest {

	@Test
	public void test() {
		final CountingMap<String> a = new CountingMap<String>("prefix");

		final String key1 = a.add("ABC 123");
		assertEquals("prefix0", key1);

		final String key2 = a.add("xyz");
		assertEquals("prefix1", key2);

		assertTrue(!key1.equals(key2));

		// count map entries
		int size = 0;
		for (final Map.Entry<String, String> it : a) {
			it.toString(); 
			++size;
		}
		assertEquals(2, size);
	}

}
