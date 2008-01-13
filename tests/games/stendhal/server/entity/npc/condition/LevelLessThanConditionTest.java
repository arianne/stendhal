package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class LevelLessThanConditionTest {

	private Player level100Player;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		level100Player = PlayerTestHelper.createPlayer("player");
		level100Player.setLevel(100);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public final void testHashCode() {
		assertEquals(new LevelLessThanCondition(101).hashCode(),
				new LevelLessThanCondition(101).hashCode());

	}

	@Test
	public final void testFire() {
		assertFalse(new LevelLessThanCondition(99).fire(level100Player,
				ConversationParser.parse("lessthan"), null));
		assertFalse(new LevelLessThanCondition(100).fire(level100Player,
				ConversationParser.parse("lessthan"), null));
		assertTrue(new LevelLessThanCondition(101).fire(level100Player,
				ConversationParser.parse("lessthan"), null));
	}

	@Test
	public final void testLevelLessThanCondition() {
		new LevelLessThanCondition(0);

	}

	@Test
	public final void testToString() {
		assertEquals("level < 0 ", new LevelLessThanCondition(0).toString());
	}

	@Test
	public final void testEqualsObject() {
		assertEquals(new LevelLessThanCondition(101),
				new LevelLessThanCondition(101));
		assertFalse((new LevelLessThanCondition(101)).equals(new LevelLessThanCondition(
				102)));
		assertFalse((new LevelLessThanCondition(102)).equals(new LevelLessThanCondition(
				101)));

	}

}
