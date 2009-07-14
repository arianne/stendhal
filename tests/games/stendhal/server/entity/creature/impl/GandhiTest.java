package games.stendhal.server.entity.creature.impl;

import static org.junit.Assert.assertFalse;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

public class GandhiTest {
	@BeforeClass
	public static void beforeClass() {
		MockStendlRPWorld.get();
	}
	
	@Test
	public void testAttack() {
		final Gandhi g = new Gandhi();
		final Creature c = new Creature();
		g.attack(null);
		g.attack(c);
		assertFalse(c.isAttacking());
	}

	@Test
	public void testCanAttackNow() {
		final Gandhi g = new Gandhi();
		assertFalse(g.canAttackNow(null));
	}

	
	
	@Test
	public void testHasValidTarget() {
		final Gandhi g = new Gandhi();
		assertFalse(g.hasValidTarget(null));
	}

}
