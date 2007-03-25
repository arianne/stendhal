package games.stendhal.client.soundreview;

import static org.junit.Assert.*;

import org.junit.Test;


public class SoundFileMapTest {

	@Test
	public void testIsNull() {
		SoundFileMap sfm = new SoundFileMap();
		assertTrue(sfm.isNull());
		sfm.put("test",new byte[0]);
		assertFalse(sfm.isNull());
		assertNotNull(sfm.get("test"));
	}

}
