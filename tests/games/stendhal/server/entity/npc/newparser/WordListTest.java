package games.stendhal.server.entity.npc.newparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * test WordList class
 *
 * @author Martin Fuchs
 */
public class WordListTest {

	@Test
	public final void testNouns() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("house");
		assertNotNull(w);
		assertEquals("NOU", w.type);
		assertEquals("houses", w.plural);

		w = wl.find("man");
		assertNotNull(w);
		assertEquals("NOU-PER", w.type);
		assertEquals("men", w.plural);

		w = wl.find("carrot");
		assertNotNull(w);
		assertEquals("NOU-FOO", w.type);
		assertEquals("carrots", w.plural);

		w = wl.find("carrots");
		assertNotNull(w);
		assertEquals("NOU-FOO-PLU", w.type);
		assertEquals("carrot", w.plural);

		w = wl.find("water");
		assertNotNull(w);
		assertEquals("NOU-FLU", w.type);
		assertEquals("waters", w.plural);

		w = wl.find("she");
		assertNotNull(w);
		assertEquals("NOU-PER", w.type);
		assertEquals("they", w.plural);
	}

	@Test
	public final void testVerbs() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("say");
		assertNotNull(w);
		assertEquals("VER", w.type);

		w = wl.find("open");
		assertNotNull(w);
		assertEquals("VER", w.type);

		w = wl.find("are");
		assertNotNull(w);
		assertEquals("VER-PLU", w.type);
	}

	@Test
	public final void testAdjectives() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("white");
		assertNotNull(w);
		assertEquals("ADJ-COL", w.type);

		w = wl.find("silvery");
		assertNotNull(w);
		assertEquals("ADJ-COL", w.type);
	}

	@Test
	public final void testPrepositions() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("with");
		assertNotNull(w);
		assertEquals("PRE", w.type);

		w = wl.find("on");
		assertNotNull(w);
		assertEquals("PRE", w.type);
	}

	@Test
	public final void testPlural() {
		WordList wl = WordList.getInstance();

		assertEquals("houses", wl.plural("house"));
		assertEquals("cookies", wl.plural("cookie"));
		assertEquals("cookies", wl.plural("cooky"));
	}

	@Test
	public final void testSingular() {
		WordList wl = WordList.getInstance();

		assertEquals("house", wl.singular("houses"));
		assertEquals("cookie", wl.singular("cookies"));
	}
}
