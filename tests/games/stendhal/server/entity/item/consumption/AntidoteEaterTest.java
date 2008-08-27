package games.stendhal.server.entity.item.consumption;

import static org.junit.Assert.*;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class AntidoteEaterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCode() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		AntidoteEater eater1 = new AntidoteEater(bob );
		AntidoteEater eater2 = new AntidoteEater(bob);
		assertTrue(eater1.equals(eater2));
		assertTrue(eater2.equals(eater1));
		assertEquals(eater1.hashCode(), eater2.hashCode());
	}

}
