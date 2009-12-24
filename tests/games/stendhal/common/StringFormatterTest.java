package games.stendhal.common;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringFormatterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for stringFormatter.
	 */
	@Test
	public final void testStringFormatter() {
		StringFormatter formatter = new StringFormatter("test");
		assertEquals("test", formatter.toString());
		formatter = new StringFormatter("<test>${test}</test>");
		assertEquals("<test></test>", formatter.toString());

		formatter.set("test", "hello");
		assertEquals("<test>hello</test>", formatter.toString());

	}

	/**
	 * Tests for setStringString.
	 */
	@Test
	public final void testSetStringString() {
		final StringFormatter formatter = new StringFormatter(
				"<first>${first}</first><2nd>${2nd}</2nd><3rd>${3rd}</3rd>");
		assertEquals("<first></first><2nd></2nd><3rd></3rd>",
				formatter.toString());
		formatter.set("3rd", "last");
		formatter.set("first", "winner");
		formatter.set("2nd", "another one");
		assertEquals(
				"<first>winner</first><2nd>another one</2nd><3rd>last</3rd>",
				formatter.toString());

	}

	/**
	 * Tests for setStringInt.
	 */
	@Test
	public final void testSetStringInt() {
		final StringFormatter formatter = new StringFormatter(
				"<first>${first}</first><2nd>${2nd}</2nd><3rd>${3rd}</3rd>");
		assertEquals("<first></first><2nd></2nd><3rd></3rd>",
				formatter.toString());
		formatter.set("3rd", 3);
		formatter.set("first", 1);
		formatter.set("2nd", 2);
		assertEquals("<first>1</first><2nd>2</2nd><3rd>3</3rd>",
				formatter.toString());

	}

}
