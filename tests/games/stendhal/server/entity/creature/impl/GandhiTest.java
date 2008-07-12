package games.stendhal.server.entity.creature.impl;

import static org.junit.Assert.assertFalse;
import games.stendhal.server.entity.creature.Creature;

import org.junit.Test;

public class GandhiTest {

	
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
