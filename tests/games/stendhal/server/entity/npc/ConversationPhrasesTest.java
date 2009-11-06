package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Tests for ConversationPhrases
 *
 * @author hendrik
 */
public class ConversationPhrasesTest {

	/**
	 * test for empty list
	 */
	@Test
	public void testEmpty() {
		assertTrue(ConversationPhrases.EMPTY.size() == 0);
	}
}
