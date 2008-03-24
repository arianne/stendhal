package games.stendhal.server.entity.creature.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.CreatureTestHelper;

public class HandToHandTest {

	@BeforeClass
	public static void setUpbeforeClass() throws Exception {
		MockStendlRPWorld.get();
		CreatureTestHelper.generateRPClasses();
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testCanAttackNow() {
		HandToHand hth = new HandToHand();
		Creature creature = new Creature();	
		assertFalse("no target yet", hth.canAttackNow(creature));
		RPEntity victim = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logic() {
				// TODO Auto-generated method stub
				
			}
		};
		victim.put("id", 1);
		creature.setTarget(victim);
		assertTrue("new ones stand on same positon", hth.canAttackNow(creature));
		victim.setPosition(10, 10);
		assertFalse("too far away", hth.canAttackNow(creature));
		
		
	}

	
	private static boolean mockinvisible;

	@Test
	public void testHasValidTarget() {
		StendhalRPZone zone = new StendhalRPZone("hthtest");
		
		HandToHand hth = new HandToHand();
		Creature creature = new Creature();	
		assertFalse("is not attacking", hth.hasValidTarget(creature));
		RPEntity victim = new RPEntity() {
		
			@Override
			public boolean isInvisible() {
				return mockinvisible;
			}
			@Override
			protected void dropItemsOn(Corpse corpse) {
				
			}

			@Override
			public void logic() {
				
			}
		};
		victim.put("id", 1);
		creature.setTarget(victim);
		mockinvisible = true;
		assertTrue(victim.isInvisible());
		assertFalse("victim is invisible", hth.hasValidTarget(creature));
		mockinvisible = false;
		assertFalse(victim.isInvisible());
		zone.add(victim);
		assertFalse("not in same zone", hth.hasValidTarget(creature));
		zone.add(creature);
		assertTrue("in same zone, on same spot", hth.hasValidTarget(creature));
		victim.setPosition(12, 0);
		assertTrue("in same zone, not too far away", hth.hasValidTarget(creature));
		victim.setPosition(13, 0);
		assertFalse("in same zone, too far away", hth.hasValidTarget(creature));
		

	}

}
