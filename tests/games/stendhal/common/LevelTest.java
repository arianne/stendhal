package games.stendhal.common;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LevelTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testMaxLevel() {
		assertEquals(597, Level.maxLevel());

	}

	@Test
	public final void testGetLevel() {
		// assertELevel.getLevel()
	}

	@Test
	public final void testGetXP() {

		assertEquals(0, Level.getXP(0));
		assertEquals(50, Level.getXP(1));
		assertEquals(9753800, Level.getXP(100));

		assertEquals(2118873200, Level.getXP(Level.maxLevel()));
	}

	@Test
	public final void testGetNegativeXP() {

		assertEquals(-1, Level.getXP(-1));
		assertEquals(-1, Level.getXP(-10));
	}

	@Test
	public final void testGetMoreThanMaxXP() {
		assertEquals(2129553600, Level.getXP(Level.maxLevel() + 1));
		assertEquals(-1, Level.getXP(Level.maxLevel() + 2));
	}

	@Test
	public final void testChangeLevel() {
		assertEquals(0, Level.changeLevel(0, 49));

		assertEquals(1, Level.changeLevel(0, 50));
		assertEquals(1, Level.changeLevel(50, 100));
		assertEquals(2, Level.changeLevel(0, 100));
		assertEquals(Level.maxLevel() - 1, Level.changeLevel(0,
				Level.getXP(Level.maxLevel() - 1)));
		assertEquals(Level.maxLevel(), Level.changeLevel(0,
				Level.getXP(Level.maxLevel())));

	}

	@Test
	public final void testGetWisdom() {
		assertEquals(0.0, Level.getWisdom(0), 0.001);
		assertEquals(0.9973688848712813, Level.getWisdom(Level.maxLevel()), 0.001);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public final void testGetWisdomOverMaxlevel() {
		assertEquals(1.0, Level.getWisdom(Level.maxLevel() + 1), 0.001);
	}

}
