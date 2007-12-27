package games.stendhal.server.entity.npc.newparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
		assertTrue(w.type.isObject());
		assertEquals("OBJ", w.type.typeString);
		assertEquals("houses", w.plurSing);

		w = wl.find("man");
		assertNotNull(w);
		assertTrue(w.type.isSubject());
		assertEquals("SUB", w.type.typeString);
		assertEquals("men", w.plurSing);

		w = wl.find("carrot");
		assertNotNull(w);
		assertTrue(w.type.isObject());
		assertEquals("OBJ-FOO", w.type.typeString);
		assertEquals("carrots", w.plurSing);

		w = wl.find("carrots");
		assertNotNull(w);
		assertTrue(w.type.isObject());
		assertEquals("OBJ-FOO-PLU", w.type.typeString);
		assertEquals("carrot", w.plurSing);

		w = wl.find("water");
		assertNotNull(w);
		assertTrue(w.type.isObject());
		assertEquals("OBJ-FLU", w.type.typeString);
		assertEquals("waters", w.plurSing);

		w = wl.find("she");
		assertNotNull(w);
		assertTrue(w.type.isSubject());
		assertEquals("SUB", w.type.typeString);
		assertEquals("they", w.plurSing);
	}

	@Test
	public final void testVerbs() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("say");
		assertNotNull(w);
		assertTrue(w.type.isVerb());
		assertEquals("VER", w.type.typeString);

		w = wl.find("open");
		assertNotNull(w);
		assertTrue(w.type.isVerb());
		assertEquals("VER", w.type.typeString);

		w = wl.find("are");
		assertNotNull(w);
		assertTrue(w.type.isVerb());
		assertEquals("VER-PLU", w.type.typeString);
	}

	@Test
	public final void testAdjectives() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("white");
		assertNotNull(w);
		assertTrue(w.type.isAdjective());
		assertEquals("ADJ-COL", w.type.typeString);

		w = wl.find("silvery");
		assertNotNull(w);
		assertTrue(w.type.isAdjective());
		assertEquals("ADJ-COL", w.type.typeString);
	}

	@Test
	public final void testPrepositions() {
		WordList wl = WordList.getInstance();

		WordEntry w = wl.find("with");
		assertNotNull(w);
		assertTrue(w.type.isPreposition());
		assertEquals("PRE", w.type.typeString);

		w = wl.find("on");
		assertNotNull(w);
		assertTrue(w.type.isPreposition());
		assertEquals("PRE", w.type.typeString);
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
