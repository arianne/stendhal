package games.stendhal.bot.postman;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

/**
 * Tests for CiaMessage
 *
 * @author hendrik
 */
public class CiaMessageTest {

	/**
	 * tests for calcualteCommonPrefix
	 */
	@Test
	public void testCalculateCommonPrefix() {
		assertEquals(new CiaMessage().calculateCommonPrefix(new LinkedList<String>()), 0);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("Hello")), 5);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("Hello", "Hel")), 3);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("Hello", "B")), 0);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("Hello", "")), 0);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("", "Hello")), 0);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("Hello", "HelBzz")), 3);
		assertEquals(new CiaMessage().calculateCommonPrefix(Arrays.asList("Hello", "HelloDu")), 5);
	}

	/**
	 * tests for getFiles
	 */
	@Test
	public void testGetFiles() {
		CiaMessage msg = new CiaMessage();
		assertThat(msg.getFiles(), equalTo(""));

		msg.addFile("src/marauroa/server/net/INetworkServerManager.java");
		assertThat(msg.getFiles(), equalTo("src/marauroa/server/net/INetworkServerManager.java"));

		msg.addFile("src/marauroa/server/net/validator/ConnectionValidator.java");
		assertThat(msg.getFiles(), equalTo("src/marauroa/server/net/ (2 files)"));

		msg = new CiaMessage();
		msg.addFile("tiled/tileset/refactoring");
		msg.addFile("tiled/tileset/building/decoration/fireplace.xcf.bz2");
		assertThat(msg.getFiles(), equalTo("tiled/tileset/ (refactoring, building/decoration/fireplace.xcf.bz2)"));
	
		msg = new CiaMessage();
		msg.addFile("tests/games/stendhal/bot/postman/cia.txt");
		msg.addFile("tests/games/stendhal/bot/postman/cia2.txt");
		assertThat(msg.getFiles(), equalTo("tests/games/stendhal/bot/postman/ (cia.txt, cia2.txt)"));
	}
}
