package games.stendhal.server.entity;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.slot.PlayerSlot;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.ItemTestHelper;

public class RPEntityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		ItemTestHelper.generateRPClasses();
		
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
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 0), is(108));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 1), is(105));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 4), is(99));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 9), is(88));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 16), is(72));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 25), is(52));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 36), is(28));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 49), is(0));
		assertThat(RPEntity.applyDistanceAttackModifiers(damage, 64), is(-33));
	}
	@Test
	public void testCalculateRiskForCanHit() {
		RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
			}

			@Override
			public void logic() {
				// do nothing
				
			}
		};
		int defenderDEF = 0;
		int attackerATK = 0;
		assertThat(entity.calculateRiskForCanHit(1, defenderDEF, attackerATK), is(-9));
		assertThat(entity.calculateRiskForCanHit(2, defenderDEF, attackerATK), is(-8));
		assertThat(entity.calculateRiskForCanHit(3, defenderDEF, attackerATK), is(-7));
		assertThat(entity.calculateRiskForCanHit(4, defenderDEF, attackerATK), is(-6));
		assertThat(entity.calculateRiskForCanHit(5, defenderDEF, attackerATK), is(-5));
		assertThat(entity.calculateRiskForCanHit(6, defenderDEF, attackerATK), is(-4));
		assertThat(entity.calculateRiskForCanHit(7, defenderDEF, attackerATK), is(-3));
		assertThat(entity.calculateRiskForCanHit(8, defenderDEF, attackerATK), is(-2));
		assertThat(entity.calculateRiskForCanHit(9, defenderDEF, attackerATK), is(-1));
		assertThat(entity.calculateRiskForCanHit(10, defenderDEF, attackerATK), is(0));
		assertThat(entity.calculateRiskForCanHit(11, defenderDEF, attackerATK), is(1));
		assertThat(entity.calculateRiskForCanHit(12, defenderDEF, attackerATK), is(2));
		assertThat(entity.calculateRiskForCanHit(13, defenderDEF, attackerATK), is(3));
		assertThat(entity.calculateRiskForCanHit(14, defenderDEF, attackerATK), is(4));
		assertThat(entity.calculateRiskForCanHit(15, defenderDEF, attackerATK), is(5));
		assertThat(entity.calculateRiskForCanHit(16, defenderDEF, attackerATK), is(6));
		assertThat(entity.calculateRiskForCanHit(17, defenderDEF, attackerATK), is(7));
		assertThat(entity.calculateRiskForCanHit(18, defenderDEF, attackerATK), is(8));
		assertThat(entity.calculateRiskForCanHit(19, defenderDEF, attackerATK), is(9));
		assertThat(entity.calculateRiskForCanHit(20, defenderDEF, attackerATK), is(10));
		
		
		defenderDEF = 10;
		attackerATK = 5;
		assertThat(entity.calculateRiskForCanHit(1, defenderDEF, attackerATK), is(-9));
		assertThat(entity.calculateRiskForCanHit(2, defenderDEF, attackerATK), is(-8));
		assertThat(entity.calculateRiskForCanHit(3, defenderDEF, attackerATK), is(-7));
		assertThat(entity.calculateRiskForCanHit(4, defenderDEF, attackerATK), is(-6));
		assertThat(entity.calculateRiskForCanHit(5, defenderDEF, attackerATK), is(-5));
		assertThat(entity.calculateRiskForCanHit(6, defenderDEF, attackerATK), is(-4));
		assertThat(entity.calculateRiskForCanHit(7, defenderDEF, attackerATK), is(-3));
		assertThat(entity.calculateRiskForCanHit(8, defenderDEF, attackerATK), is(-2));
		assertThat(entity.calculateRiskForCanHit(9, defenderDEF, attackerATK), is(-1));
		assertThat(entity.calculateRiskForCanHit(10, defenderDEF, attackerATK), is(0));
		assertThat(entity.calculateRiskForCanHit(11, defenderDEF, attackerATK), is(1));
		assertThat(entity.calculateRiskForCanHit(12, defenderDEF, attackerATK), is(2));
		assertThat(entity.calculateRiskForCanHit(13, defenderDEF, attackerATK), is(3));
		assertThat(entity.calculateRiskForCanHit(14, defenderDEF, attackerATK), is(4));
		assertThat(entity.calculateRiskForCanHit(15, defenderDEF, attackerATK), is(5));
		assertThat(entity.calculateRiskForCanHit(16, defenderDEF, attackerATK), is(6));
		assertThat(entity.calculateRiskForCanHit(17, defenderDEF, attackerATK), is(7));
		assertThat(entity.calculateRiskForCanHit(18, defenderDEF, attackerATK), is(8));
		assertThat(entity.calculateRiskForCanHit(19, defenderDEF, attackerATK), is(9));
		assertThat(entity.calculateRiskForCanHit(20, defenderDEF, attackerATK), is(10));
	}
	@Test
	public void testGetItemAtkforsimpleweapon() {
		float magicFour = 4.0f;
		
		RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
				
			}

			@Override
			public void logic() {
				// do nothing
				
			}
		};
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));
		
		assertThat(entity.getItemAtk(), is(0f));
		Item item = SingletonRepository.getEntityManager().getItem("dagger");
		entity.getSlot("lhand").add(item);
		assertThat(entity.getItemAtk(), is(magicFour * item.getAttack()));		
		entity.getSlot("rhand").add(item);
		assertThat(entity.getItemAtk(), is(magicFour * item.getAttack()));
		entity.getSlot("lhand").remove(item.getID());
		assertThat(entity.getItemAtk(), is(magicFour * item.getAttack()));		
		
	}
	@Test
	public void testGetItemAtkforcheese() {
			
		RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
				
			}

			@Override
			public void logic() {
				// do nothing
				
			}
		};
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));
		
		assertThat(entity.getItemAtk(), is(0f));
		Item item = SingletonRepository.getEntityManager().getItem("cheese");
		entity.getSlot("lhand").add(item);
		assertThat(entity.getItemAtk(), is(0f));		
		entity.getSlot("rhand").add(item);
		assertThat(entity.getItemAtk(), is(0f));
		entity.getSlot("lhand").remove(item.getID());
		assertThat(entity.getItemAtk(), is(0f));
	}
	
	@Test
	public void testGetItemAtkforLeftandRightweaponCorrectlyWorn() {
		float magicFour = 4.0f;
		
		ItemTestHelper.generateRPClasses();
		RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
				
			}

			@Override
			public void logic() {
				// do nothing
				
			}
		};
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));
		
		assertThat(entity.getItemAtk(), is(0f));
		Item lefthanditem = SingletonRepository.getEntityManager().getItem("l hand sword");
		entity.getSlot("lhand").add(lefthanditem);
		assertThat(entity.getItemAtk(), is(0f));	

		Item righthanditem = SingletonRepository.getEntityManager().getItem("r hand sword");
		entity.getSlot("rhand").add(righthanditem);
		assertThat(entity.getItemAtk(), is(magicFour * (lefthanditem.getAttack() + righthanditem.getAttack())));
	}
	
	@Test
	public void testGetItemAtkforLeftandRightweaponIncorrectlyWorn() {
				
		ItemTestHelper.generateRPClasses();
		RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
				
			}

			@Override
			public void logic() {
				// do nothing
				
			}
		};
		entity.addSlot(new PlayerSlot("lhand"));
		entity.addSlot(new PlayerSlot("rhand"));
		
		assertThat(entity.getItemAtk(), is(0f));

		Item lefthanditem = SingletonRepository.getEntityManager().getItem("l hand sword");
		entity.getSlot("rhand").add(lefthanditem);
		assertThat(entity.getItemAtk(), is(0f));		

		Item righthanditem = SingletonRepository.getEntityManager().getItem("r hand sword");
		entity.getSlot("lhand").add(righthanditem);
		assertThat(entity.getItemAtk(), is(0f));
		
	}

	@Test
	public void testAttackCanHitreturnTruedamageZero() {
		MockStendlRPWorld.get();
		StendhalRPZone zone = new StendhalRPZone("testzone");
		RPEntity attacker = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing

			}

			@Override
			public boolean canHit(RPEntity defender) {
				return true;
			}
			@Override
			public int damageDone(RPEntity defender) {
				return 0;
			}

			@Override
			public void logic() {
				// do nothing

			}
		};
		RPEntity defender = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
				
			}

			@Override
			public void logic() {
				// do nothing
				
			}
		};
		
		zone.add(attacker);
		zone.add(defender);
		
		
		attacker.setTarget(defender);
		defender.setHP(100);
		
		assertTrue(zone.has(defender.getID()));
		assertThat(defender.getHP(), greaterThan(0));
		assertFalse(attacker.has("damage"));
		
		assertFalse(attacker.attack());
		
		assertNotNull(attacker.getAttackTarget());
		assertTrue(attacker.has("damage"));
		assertThat("no damage done " , attacker.get("damage"), is("0"));
		}

	@Test
	public void testAttackCanHitreturnTruedamage30() {
		MockStendlRPWorld.get();
		StendhalRPZone zone = new StendhalRPZone("testzone");
		RPEntity attacker = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing

			}

			@Override
			public boolean canHit(RPEntity defender) {
				return true;
			}
			@Override
			public int damageDone(RPEntity defender) {
				return 30;
			}

			@Override
			public void logic() {
				// do nothing

			}
		};
		RPEntity defender = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// do nothing
				
			}

			@Override
			public void onDamaged(Entity attacker, int damage) {
				assertEquals(30, damage);
			}
			@Override
			public void logic() {
				// do nothing
				
			}
		};
		
		zone.add(attacker);
		zone.add(defender);
		
		
		attacker.setTarget(defender);
		defender.setHP(100);
		
		assertTrue(zone.has(defender.getID()));
		assertThat(defender.getHP(), greaterThan(0));
		assertFalse(attacker.has("damage"));
		
		assertTrue(attacker.attack());
		
		assertNotNull(attacker.getAttackTarget());
		assertTrue(attacker.has("damage"));
		assertThat("no damge done " , attacker.get("damage"), is("30"));
		}

	
}
