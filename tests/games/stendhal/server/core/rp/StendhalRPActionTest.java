package games.stendhal.server.core.rp;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StendhalRPActionTest {

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
	public void testApplydistanceattackModifiers() {
		
		int damage = 100;
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 0), is(108));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 1), is(105));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 4), is(99));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 9), is(88));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 16), is(72));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 25), is(52));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 36), is(28));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 49), is(0));
		assertThat(StendhalRPAction.applyDistanceAttackModifiers(damage, 64), is(-33));
	}

}
