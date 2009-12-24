package games.stendhal.client.soundreview;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;
import games.stendhal.client.entity.User;
import games.stendhal.client.sound.SoundSystem;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SoundTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		User.setNull();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for soundStringIntInt.
	 */
	@Test
	public void testSoundStringIntInt() {
		new Sound("bla", 0, 0);
	}

	/**
	 * Tests for soundStringIntIntBoolean.
	 */
	@Test
	public void testSoundStringIntIntBoolean() {
		new Sound("bla", 0, 0, true);

	}

	/**
	 * Tests for play.
	 */
	@Ignore // needs a way to actually play sound, fails on hudson even with virtual X
	@Test
	public void testPlay() {
		final SoundMaster sm = new SoundMaster();
		sm.init();
		assumeTrue(SoundSystem.get().isOperative());
		
		Sound valid = new Sound("chicken-mix", 0, 0);
		assertNull(valid.play());
		valid = new Sound("chicken-mix", 1, 1);
		assertNotNull("this sound exists", valid);
		new User();
		assertNotNull(valid.play());
		final Sound invalid = new Sound("bla", 1, 1);
		assertNull(invalid.play());
	}

}
