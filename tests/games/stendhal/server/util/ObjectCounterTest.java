package games.stendhal.server.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectCounterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testAdd() {
		ObjectCounter<String> ocString = new ObjectCounter<String>();
		String bla = "bla";
		String blub = "blub";
		ocString.add(bla);
		Map<String, Integer> resmap = ocString.getMap();
		assertEquals(Integer.valueOf(1), resmap.get(bla));
		ocString.add(bla);
		resmap = ocString.getMap();
		assertEquals(Integer.valueOf(2), resmap.get(bla));
		ocString.add(bla);
		resmap = ocString.getMap();
		assertEquals(Integer.valueOf(3), resmap.get(bla));
		assertEquals(null, resmap.get(blub));
		ocString.add(blub);
		resmap = ocString.getMap();
		assertEquals(Integer.valueOf(3), resmap.get(bla));
		assertEquals(Integer.valueOf(1), resmap.get(blub));
		ocString.clear();
		resmap = ocString.getMap();
		assertEquals(null, resmap.get(bla));
		assertEquals(null, resmap.get(blub));

	}

}
