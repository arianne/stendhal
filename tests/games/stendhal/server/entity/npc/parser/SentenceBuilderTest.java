package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the SentenceBuilder class.
 * 
 * @author Martin Fuchs
 */
public class SentenceBuilderTest {

	@Test
	public final void testSentenceBuilder() {
		final SentenceBuilder b = new SentenceBuilder();

		assertTrue(b.isEmpty());
		assertEquals("", b.toString());

		b.append("abc");
		assertFalse(b.isEmpty());
		assertEquals("abc", b.toString());

		b.append("def");
		assertFalse(b.isEmpty());
		assertEquals("abc def", b.toString());

		b.append('X');
		assertFalse(b.isEmpty());
		assertEquals("abc defX", b.toString());
	}

}
