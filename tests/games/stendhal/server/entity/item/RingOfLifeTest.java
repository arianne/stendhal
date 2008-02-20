package games.stendhal.server.entity.item;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.ItemTestHelper;

public class RingOfLifeTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();
		
	}
	@Test
	public void testDescribe() {
		RingOfLife ring = new RingOfLife();
		assertThat(ring.isBroken(), is(false));
		assertThat(ring.describe(), is("You see the ring of life. Wear it, and you risk less from death."));
		
		ring.damage();
		assertThat(ring.isBroken(), is(true));
		assertThat(ring.describe(), is("You see the ring of life. The gleam is lost from the stone and it has no powers."));
		
	}

	@Test
	public void testOnUsed() {
		RingOfLife ring = new RingOfLife();
		assertThat(ring.isBroken(), is(false));
		assertThat(ring.describe(), is("You see the ring of life. Wear it, and you risk less from death."));
		
		ring.onUsed(null);
		assertThat(ring.isBroken(), is(true));
		assertThat(ring.describe(), is("You see the ring of life. The gleam is lost from the stone and it has no powers."));
		

	}

	

	@Test
	public void testRepair() {
		RingOfLife ring = new RingOfLife();
		assertThat(ring.isBroken(), is(false));
		assertThat(ring.describe(), is("You see the ring of life. Wear it, and you risk less from death."));
		
		ring.damage();
		assertThat(ring.isBroken(), is(true));
		assertThat(ring.describe(), is("You see the ring of life. The gleam is lost from the stone and it has no powers."));
	
		ring.repair();
		assertThat(ring.isBroken(), is(false));
		assertThat(ring.describe(), is("You see the ring of life. Wear it, and you risk less from death."));
	}

}
