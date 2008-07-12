package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test the NPC conversation WordEntry class.
 * 
 * @author Martin Fuchs
 */
public class WordEntryTest {

	@Test
	public final void testWordEntry() {
		final WordEntry w = new WordEntry();
		assertEquals("/", w.getNormalizedWithTypeString());

		w.setNormalized("norm");
		w.setPlurSing("plur");
		assertEquals("norm/", w.getNormalizedWithTypeString());

		w.setType(new ExpressionType("TYP"));
		w.setValue(4711);
		assertEquals("norm/TYP", w.getNormalizedWithTypeString());
	}

}
