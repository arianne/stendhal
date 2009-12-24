package games.stendhal.client.entity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import games.stendhal.client.entity.RPEntity.Resolution;

import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RPEntityTest {

	private RPEntity defender;
	private RPEntity attacker;
	private RPObject object;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		defender = new RPEntity() {
		};
		attacker = new RPEntity() {
		};
		object = new RPObject();

	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for evaluateAttackEmptyObject.
	 */
	@Test
	public void testEvaluateAttackEmptyObject() {
		defender.evaluateAttack(object, attacker);
		assertThat(attacker.getResolution(), is(Resolution.MISSED));
	}

	/**
	 * Tests for evaluateAttackrisk0.
	 */
	@Test
	public void testEvaluateAttackrisk0() {
		object.put("risk", 0);
		defender.evaluateAttack(object, attacker);
		assertThat(attacker.getResolution(), is(Resolution.MISSED));
	}

	/**
	 * Tests for evaluateAttackriskMinus1.
	 */
	@Test
	public void testEvaluateAttackriskMinus1() {
		object.put("risk", -1);
		defender.evaluateAttack(object, attacker);
		assertNull(attacker.getResolution());
	}

	/**
	 * Tests for evaluateAttackNoRiskdamage0.
	 */
	@Test
	public void testEvaluateAttackNoRiskdamage0() {
		object.put("damage", 0);
		defender.evaluateAttack(object, attacker);
		assertThat(attacker.getResolution(), is(Resolution.MISSED));
	}

	/**
	 * Tests for evaluateAttackRisk0Damage0.
	 */
	@Test
	public void testEvaluateAttackRisk0Damage0() {
		object.put("risk", 0);
		object.put("damage", 0);
		defender.evaluateAttack(object, attacker);
		assertThat(attacker.getResolution(), is(Resolution.MISSED));
	}

	/**
	 * Tests for evaluateAttackRisk1Damage0.
	 */
	@Test
	public void testEvaluateAttackRisk1Damage0() {
		object.put("risk", 1);
		object.put("damage", 0);
		defender.evaluateAttack(object, attacker);
		assertThat(attacker.getResolution(), is(Resolution.BLOCKED));
	}

	/**
	 * Tests for evaluateAttackRisk1Damage1.
	 */
	@Test
	public void testEvaluateAttackRisk1Damage1() {
		object.put("risk", 1);
		object.put("damage", 1);
		defender.evaluateAttack(object, attacker);
		assertThat(attacker.getResolution(), is(Resolution.HIT));
	}
}
