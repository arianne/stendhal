package games.stendhal.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * Tests the CountingMap class
 * 
 * @author Martin Fuchs
 */
public class CountingMapTest {

	@Test
	public void test() {
		CountingMap<String> a = new CountingMap<String>("prefix");

		String key1 = a.add("ABC 123");
		assertEquals("prefix0", key1);

		String key2 = a.add("xyz");
		assertEquals("prefix1", key2);

		assertTrue(!key1.equals(key2));

		// count map entries
		int size = 0;
		for (Map.Entry<String, String> it : a) {
			it.toString(); // System.out.println(it.toString());
			++size;
		}
		assertEquals(2, size);
	}

}
