package games.stendhal.client.soundreview;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SoundFileMapTest {

	/**
	 * Tests for isNull.
	 */
	@Test
	public void testIsNull() {
		final SoundFileMap sfm = new SoundFileMap();
		assertTrue(sfm.isNull());
		sfm.put("test", new byte[0]);
		assertFalse(sfm.isNull());
		assertNotNull(sfm.get("test"));
	}

}
