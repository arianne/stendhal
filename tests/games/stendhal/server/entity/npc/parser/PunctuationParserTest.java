package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test the NPC conversation PunctuationParser class.
 * 
 * @author Martin Fuchs
 */
public class PunctuationParserTest {

	@Test
	public final void testWordEntry() {
		PunctuationParser p = new PunctuationParser("... hello?");
		assertEquals(" hello", p.getText());
		assertEquals("...", p.getPrecedingPunctuation());
		assertEquals("?", p.getTrailingPunctuation());

		p = new PunctuationParser("hello world !");
		assertEquals("hello world ", p.getText());
		assertEquals("", p.getPrecedingPunctuation());
		assertEquals("!", p.getTrailingPunctuation());
	}

}
