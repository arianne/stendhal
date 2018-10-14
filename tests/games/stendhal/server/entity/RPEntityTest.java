/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.CaptureTheFlagFlag;
import games.stendhal.server.entity.item.Container;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.PlayerSlot;
import games.stendhal.server.events.AttackEvent;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPSlot;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class RPEntityTest {
	private class MockRPEntity extends RPEntity {
		@Override
		protected void dropItemsOn(final Corpse corpse) {
			// do Nothing
		}

		@Override
		public void logic() {
			// do Nothing
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();

	}

	/**
	 * Tests for applydistanceattackModifiers.
	 */
	@Test
	public void testApplydistanceattackModifiers() {

		final int damage = 100;

		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 0, 7), is(80));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 1, 7), is(43));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 4, 7), is(75));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 9, 7), is(93));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 16, 7), is(100));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 25, 7), is(93));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 36, 7), is(75));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 49, 7), is(43));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 64, 7), is(0));

		// same with non standard distances.
		// a short range weapon first
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 0, 4), is(80));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 1, 4), is(64));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 4, 4), is(96));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 9, 4), is(96));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 16, 4), is(64));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 25, 4), is(0));

		// and a long range weapon (dunno if we actually have anything for
		// this long range)
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 0, 10), is(80));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 1, 10), is(33));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 4, 10), is(59));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 9, 10), is(79));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 16, 10), is(92));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 25, 10), is(99));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 36, 10), is(99));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 49, 10), is(92));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 64, 10), is(79));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 81, 10), is(59));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 100, 10), is(33));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 121, 10), is(0));
	}

	/**
	 * Tests for calculateRiskForCanHit.
	 */
	@Test
	public void testCalculateRiskForCanHit() {
		final RPEntity entity = new MockRPEntity();

		int defenderDEF = 1;
		int attackerATK = 1;
		assertThat(entity.calculateRiskForCanHit(1, defenderDEF, attackerATK), is(19));
		assertThat(entity.calculateRiskForCanHit(2, defenderDEF, attackerATK), is(18));
		assertThat(entity.calculateRiskForCanHit(3, defenderDEF, attackerATK), is(17));
		assertThat(entity.calculateRiskForCanHit(4, defenderDEF, attackerATK), is(16));
		assertThat(entity.calculateRiskForCanHit(5, defenderDEF, attackerATK), is(15));
		assertThat(entity.calculateRiskForCanHit(6, defenderDEF, attackerATK), is(14));
		assertThat(entity.calculateRiskForCanHit(7, defenderDEF, attackerATK), is(13));
		assertThat(entity.calculateRiskForCanHit(8, defenderDEF, attackerATK), is(12));
		assertThat(entity.calculateRiskForCanHit(9, defenderDEF, attackerATK), is(11));
		assertThat(entity.calculateRiskForCanHit(10, defenderDEF, attackerATK), is(10));
		assertThat(entity.calculateRiskForCanHit(11, defenderDEF, attackerATK), is(9));
		assertThat(entity.calculateRiskForCanHit(12, defenderDEF, attackerATK), is(8));
		assertThat(entity.calculateRiskForCanHit(13, defenderDEF, attackerATK), is(7));
		assertThat(entity.calculateRiskForCanHit(14, defenderDEF, attackerATK), is(6));
		assertThat(entity.calculateRiskForCanHit(15, defenderDEF, attackerATK), is(5));
		assertThat(entity.calculateRiskForCanHit(16, defenderDEF, attackerATK), is(4));
		assertThat(entity.calculateRiskForCanHit(17, defenderDEF, attackerATK), is(3));
		assertThat(entity.calculateRiskForCanHit(18, defenderDEF, attackerATK), is(2));
		assertThat(entity.calculateRiskForCanHit(19, defenderDEF, attackerATK), is(1));
		assertThat(entity.calculateRiskForCanHit(20, defenderDEF, attackerATK), is(0));

		defenderDEF = 10;
		attackerATK = 5;
		assertThat(entity.calculateRiskForCanHit(1, defenderDEF, attackerATK), is(90));
		assertThat(entity.calculateRiskForCanHit(2, defenderDEF, attackerATK), is(80));
		assertThat(entity.calculateRiskForCanHit(3, defenderDEF, attackerATK), is(70));
		assertThat(entity.calculateRiskForCanHit(4, defenderDEF, attackerATK), is(60));
		assertThat(entity.calculateRiskForCanHit(5, defenderDEF, attackerATK), is(50));
		assertThat(entity.calculateRiskForCanHit(6, defenderDEF, attackerATK), is(40));
		assertThat(entity.calculateRiskForCanHit(7, defenderDEF, attackerATK), is(30));
		assertThat(entity.calculateRiskForCanHit(8, defenderDEF, attackerATK), is(20));
		assertThat(entity.calculateRiskForCanHit(9, defenderDEF, attackerATK), is(10));
		assertThat(entity.calculateRiskForCanHit(10, defenderDEF, attackerATK), is(0));
		assertThat(entity.calculateRiskForCanHit(11, defenderDEF, attackerATK), is(-10));
		assertThat(entity.calculateRiskForCanHit(12, defenderDEF, attackerATK), is(-20));
		assertThat(entity.calculateRiskForCanHit(13, defenderDEF, attackerATK), is(-30));
		assertThat(entity.calculateRiskForCanHit(14, defenderDEF, attackerATK), is(-40));
		assertThat(entity.calculateRiskForCanHit(15, defenderDEF, attackerATK), is(-50));
		assertThat(entity.calculateRiskForCanHit(16, defenderDEF, attackerATK), is(-60));
		assertThat(entity.calculateRiskForCanHit(17, defenderDEF, attackerATK), is(-70));
		assertThat(entity.calculateRiskForCanHit(18, defenderDEF, attackerATK), is(-80));
		assertThat(entity.calculateRiskForCanHit(19, defenderDEF, attackerATK), is(-90));
		assertThat(entity.calculateRiskForCanHit(20, defenderDEF, attackerATK), is(-100));
	}

	/*
	 * Quest tests do a lot of simple drops. Testing some harder cases
	 * here.
	 */
	@Test
	public void testDrop() {
		final RPEntity entity = new MockRPEntity();
		entity.addSlot(new PlayerSlot("bag"));

		// More than one non stackable
		entity.equip("bag", ItemTestHelper.createItem("carrot"));
		entity.equip("bag", ItemTestHelper.createItem("wooden shield"));
		entity.equip("bag", ItemTestHelper.createItem("wooden shield"));
		entity.equip("bag", ItemTestHelper.createItem("money", 4));
		assertEquals(2, entity.getNumberOfEquipped("wooden shield"));
		entity.drop("wooden shield", 2);
		assertEquals(0, entity.getNumberOfEquipped("wooden shield"));

		// Stackable in more than one stack
		entity.addSlot(new PlayerSlot("rhand"));
		entity.equip("rhand", ItemTestHelper.createItem("money", 3));
		assertEquals(2, entity.getAllEquipped("money").size());
		assertEquals(7, entity.getNumberOfEquipped("money"));
		entity.drop("money", 5);
		assertEquals(2, entity.getNumberOfEquipped("money"));
		assertEquals(1, entity.getAllEquipped("money").size());

		// Stackable more than one stack, one of then nested
		Item bag = new Container("testbag", "container", "testbag", Collections.emptyMap());
		entity.equip("bag", bag);
		bag.getSlot("content").add(ItemTestHelper.createItem("money", 40));
		assertEquals(42, entity.getNumberOfEquipped("money"));
		entity.drop("money", 5);
		assertEquals(37, entity.getNumberOfEquipped("money"));

		assertEquals(1, entity.getNumberOfEquipped("carrot"));
	}

	/**
	 * Tests for getItemAtkforsimpleweapon.
	 */
	@Test
	public void testGetItemAtkforsimpleweapon() {
		final RPEntity entity = new MockRPEntity();
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));

		assertThat(entity.getItemAtk(), is(0f));
		final Item item = SingletonRepository.getEntityManager().getItem("dagger");
		entity.getSlot("lhand").add(item);
		assertThat(entity.getItemAtk(), is((float) item.getAttack()));
		entity.getSlot("rhand").add(item);
		assertThat(entity.getItemAtk(), is((float) item.getAttack()));
		entity.getSlot("lhand").remove(item.getID());
		assertThat(entity.getItemAtk(), is((float) item.getAttack()));

	}

	/**
	 * Tests for getItemAtkforcheese.
	 */
	@Test
	public void testGetItemAtkforcheese() {
		final RPEntity entity = new MockRPEntity();
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));

		assertThat(entity.getItemAtk(), is(0f));
		final Item item = SingletonRepository.getEntityManager().getItem("cheese");
		entity.getSlot("lhand").add(item);
		assertThat(entity.getItemAtk(), is(0f));
		entity.getSlot("rhand").add(item);
		assertThat(entity.getItemAtk(), is(0f));
		entity.getSlot("lhand").remove(item.getID());
		assertThat(entity.getItemAtk(), is(0f));
	}

	/**
	 * Tests for getItemAtkforLeftandRightweaponCorrectlyWorn.
	 */
	@Test
	public void testGetItemAtkforLeftandRightweaponCorrectlyWorn() {
		ItemTestHelper.generateRPClasses();
		RPEntity entity = new MockRPEntity();

		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));

		assertThat(entity.getItemAtk(), is(0f));
		final Item lefthanditem = SingletonRepository.getEntityManager().getItem("l hand sword");
		entity.getSlot("lhand").add(lefthanditem);
		assertThat(entity.getItemAtk(), is(0f));

		final Item righthanditem = SingletonRepository.getEntityManager().getItem("r hand sword");
		entity.getSlot("rhand").add(righthanditem);
		assertThat(entity.getItemAtk(),
				is((float) (lefthanditem.getAttack() + righthanditem.getAttack())));
	}

	/**
	 * Tests for getItemAtkforLeftandRightweaponIncorrectlyWorn.
	 */
	@Test
	public void testGetItemAtkforLeftandRightweaponIncorrectlyWorn() {

		ItemTestHelper.generateRPClasses();
		final RPEntity entity = new MockRPEntity();
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));

		assertThat(entity.getItemAtk(), is(0f));

		final Item lefthanditem = SingletonRepository.getEntityManager().getItem("l hand sword");
		entity.getSlot("rhand").add(lefthanditem);
		assertThat(entity.getItemAtk(), is(0f));

		final Item righthanditem = SingletonRepository.getEntityManager().getItem("r hand sword");
		entity.getSlot("lhand").add(righthanditem);
		assertThat(entity.getItemAtk(), is(0f));

	}

	/**
	 * Tests for attackCanHitreturnTruedamageZero.
	 */
	@Test
	public void testAttackCanHitreturnTruedamageZero() {
		MockStendlRPWorld.get();
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		final RPEntity attacker = new MockRPEntity() {
			@Override
			public boolean canHit(final RPEntity defender) {
				return true;
			}

			@Override
			public int damageDone(final RPEntity defender, double attackingWeaponsValue,
					Nature damageType) {
				return 0;
			}
		};

		final RPEntity defender = new MockRPEntity();
		zone.add(attacker);
		zone.add(defender);

		attacker.setTarget(defender);
		defender.setBaseHP(100);
		defender.setHP(100);

		assertTrue(zone.has(defender.getID()));
		assertThat(defender.getHP(), greaterThan(0));
		for (RPEvent ev : attacker.events()) {
			assertFalse(ev instanceof AttackEvent);
		}

		assertFalse(attacker.attack());

		assertNotNull(attacker.getAttackTarget());
		AttackEvent attack = null;
		for (RPEvent ev : attacker.events()) {
			if (ev instanceof AttackEvent) {
				attack = (AttackEvent) ev;
				continue;
			}
		}
		assertNotNull(attack);
		assertTrue(attack.has("hit"));
		assertTrue(attack.has("damage"));
		assertThat("no damage done ", attack.get("damage"), is("0"));
	}

	/**
	 * Tests for attackCanHitreturnTruedamage30.
	 */
	@Test
	public void testAttackCanHitreturnTruedamage30() {
		MockStendlRPWorld.get();
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		final RPEntity attacker = new MockRPEntity() {
			@Override
			public boolean canHit(final RPEntity defender) {
				return true;
			}

			@Override
			public int damageDone(final RPEntity defender, double attackingWeaponsValue,
					Nature damageType, boolean ranged, int maxRange) {
				return 30;
			}
		};
		attacker.updateModifiedAttributes();

		final RPEntity defender = new MockRPEntity() {
			@Override
			public void onDamaged(final Entity attacker, final int damage) {
				assertEquals(30, damage);
			}
		};
		defender.updateModifiedAttributes();

		zone.add(attacker);
		zone.add(defender);

		attacker.setTarget(defender);
		defender.setBaseHP(100);
		defender.setHP(100);

		assertTrue(zone.has(defender.getID()));
		assertThat(defender.getHP(), greaterThan(0));
		for (RPEvent ev : attacker.events()) {
			assertFalse(ev instanceof AttackEvent);
		}

		assertTrue(attacker.attack());

		assertNotNull(attacker.getAttackTarget());

		AttackEvent attack = null;
		for (RPEvent ev : attacker.events()) {
			if (ev instanceof AttackEvent) {
				attack = (AttackEvent) ev;
				continue;
			}
		}
		assertNotNull(attack);
		assertTrue(attack.has("hit"));
		assertTrue(attack.has("damage"));
		assertThat("no damage done ", attack.get("damage"), is("30"));
	}

	/**
	 * Tests for isAttacking.
	 */
	@Test
	public void testIsAttacking() {
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		final RPEntity attacker = new MockRPEntity();
		attacker.updateModifiedAttributes();
		assertFalse("attacktarget = null", attacker.isAttacking());
		final RPEntity defender = new MockRPEntity();
		defender.updateModifiedAttributes();
		zone.add(attacker);
		zone.add(defender);
		attacker.setTarget(defender);
		defender.setBaseHP(1);
		defender.setHP(1);
		assertTrue(attacker.isAttacking());
		defender.setHP(0);
		assertFalse(attacker.isAttacking());

	}

	/**
	 * Tests for setXP.
	 */
	@Test
	public void testSetXP() {

		final RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {
				// do nothing

			}

			@Override
			public void logic() {
				// do nothing

			}
		};
		entity.setXP(300);
		final int oldXP = entity.getXP();
		entity.setXP(oldXP + Integer.MAX_VALUE);
		assertThat(entity.getXP(), is(oldXP));
	}

	/**
	 * Tests for addXP.
	 */
	@Test
	public void testaddXP() {
		final RPEntity entity = new MockRPEntity();
		entity.setXP(300);
		final int oldXP = entity.getXP();
		entity.addXP(Integer.MAX_VALUE);
		assertThat(entity.getXP(), is(oldXP));
	}

	/**
	 * Tests for getSlotToEquip.
	 */
	@Test
	public void testSlotNameToEquip() {
		final RPEntity baglessentity = new MockRPEntity();
		final Item item = createMock(Item.class);
		final List<String> slotnames = Arrays.asList("bag");
		replay(item);
		assertEquals(null, baglessentity.getSlotToEquip(item));
		verify(item);

		reset(item);
		final RPEntity entityWithBag = new MockRPEntity() {
			{
				addSlot(new RPSlot("bag"));
			}
		};
		expect(item.getPossibleSlots()).andReturn(slotnames);

		replay(item);
		assertEquals("bag", entityWithBag.getSlotToEquip(item).getName());
		verify(item);

		reset(item);
		final RPEntity entityWithFullBag = new MockRPEntity() {
			{
				RPSlot slot = new RPSlot("bag");
				addSlot(slot);
				slot.setCapacity(0);
			}
		};
		expect(item.getPossibleSlots()).andReturn(slotnames);

		replay(item);
		assertEquals(null, entityWithFullBag.getSlotToEquip(item));
		verify(item);
	}

	@Test
	public void testgetDroppables() {
		RPEntity entity = new MockRPEntity();
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		Item flag = new CaptureTheFlagFlag();

		List<Item> droppables;

		droppables = entity.getDroppables();
		assertTrue(droppables.isEmpty());

		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));
		entity.addSlot(new PlayerSlot("bag"));

		// this is not actually legal in the game, due to the item definition.
		// but this test shows that the flag must be in a hand to be droppable.
		entity.getSlot("bag").add(flag);

		droppables = entity.getDroppables();
		assertTrue(droppables.isEmpty());

		// only droppable items (flag) are droppable right now
		entity.getSlot("lhand").add(cheese);

		droppables = entity.getDroppables();
		assertTrue(droppables.isEmpty());

		// flags are droppable
		entity.getSlot("rhand").add(flag);

		droppables = entity.getDroppables();
		assertEquals(1, droppables.size());
		assertEquals("flag", droppables.get(0).get("name"));

		// remove flag, and we have no droppables any more
		entity.getSlot("rhand").remove(flag.getID());

		droppables = entity.getDroppables();
		assertTrue(droppables.isEmpty());
	}

	@Test
	public void testdropDroppableItem() {

		// MockStendlRPWorld.get();

		Player player = PlayerTestHelper.createPlayer("player");
		Item flag = new CaptureTheFlagFlag();
		List<Item> droppables;

		MockStendhalRPRuleProcessor.get().addPlayer(player);

		StendhalRPZone zone = new StendhalRPZone("testzone", 100, 100);

		zone.add(player);

		player.getSlot("rhand").add(flag);

		droppables = player.getDroppables();
		assertEquals(1, droppables.size());
		assertEquals("flag", droppables.get(0).get("name"));

		player.dropDroppableItem(flag);

		droppables = player.getDroppables();
		assertTrue(droppables.isEmpty());
	}

	@Test
	public void testmaybeDropDroppables() {

		Player player = PlayerTestHelper.createPlayer("player");
		Player attacker = PlayerTestHelper.createPlayer("attacker");
		Item flag = new CaptureTheFlagFlag();
		List<Item> droppables;

		MockStendhalRPRuleProcessor.get().addPlayer(player);
		MockStendhalRPRuleProcessor.get().addPlayer(attacker);

		StendhalRPZone zone = new StendhalRPZone("testzone", 100, 100);

		zone.add(player);

		player.getSlot("rhand").add(flag);

		// XXX i don't really know a better way to test this.
		// in theory, this could be in a loop forever.
		// but that won't happen

		while (true) {
			player.maybeDropDroppables(attacker);
			droppables = player.getDroppables();

			if (droppables.isEmpty()) {
				break;
			}
		}
	}

	@Test
	public void testModifiedBaseHP() throws Exception {
		final RPEntity entity = new MockRPEntity();
		assertThat(entity.getBaseHP(), is(0));
		entity.initHP(100);
		assertThat(entity.getBaseHP(), is(100));
		long expireTimestampAsLong = System.currentTimeMillis() + 1000;
		assertThat(entity.getBaseHP(), is(100));
		while (expireTimestampAsLong >= System.currentTimeMillis()) {
			// just to wait till modifier is expired
			Thread.sleep(100);
		}
		assertThat(entity.getBaseHP(), is(100));
	}

	@Test
	public void testModifiedDef() throws Exception {
		final RPEntity entity = new MockRPEntity();
		assertThat(entity.getDef(), is(0));
		entity.setDef(100);
		assertThat(entity.getDef(), is(100));
		long expireTimestampAsLong = System.currentTimeMillis() + 1000;
		assertThat(entity.getDef(), is(100));
		while (expireTimestampAsLong >= System.currentTimeMillis()) {
			// just to wait till modifier is expired
			Thread.sleep(100);
		}
		assertThat(entity.getDef(), is(100));
	}
}
