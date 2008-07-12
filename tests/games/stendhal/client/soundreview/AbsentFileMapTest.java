package games.stendhal.client.soundreview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbsentFileMapTest {
	private AbsentFileMap afm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		afm = new AbsentFileMap();
	}

	@After
	public void tearDown() throws Exception {
		afm = null;
	}

	@Test
	public void testClear() {
		afm.clear();
	}

	@Test
	public void testContainsKey() {

		assertFalse(afm.containsKey(new Object()));
	}

	@Test
	public void testContainsValue() {
		assertFalse(afm.containsValue(new Object()));
	}

	@Test
	public void testEntrySet() {
		final Set<Entry<String, byte[]>> afmset = afm.entrySet();
		assertNull(afmset);
	}

	@Test
	public void testGet() {
		assertNull(afm.get(new Object()));
	}

	@Test
	public void testIsEmpty() {
		assertTrue(afm.isEmpty());
	}

	@Test
	public void testKeySet() {
		assertNull(afm.keySet());
	}

	@Test(expected = IllegalStateException.class)
	public void testPut() {
		afm.put("testkey", new byte[1]);
	}

	@Test(expected = IllegalStateException.class)
	public void testPutAll() {
		afm.putAll(new HashMap<String, byte[]>());
	}

	@Test
	public void testRemove() {
		assertNull(afm.remove(new Object()));
	}

	@Test
	public void testSize() {
		assertEquals(0, afm.size());
	}

	@Test
	public void testValues() {
		assertNull(afm.values());
	}

	@Test
	public void testIsNull() {
		assertTrue(afm.isNull());
	}

}
