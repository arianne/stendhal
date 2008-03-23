package games.stendhal.server.entity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import games.stendhal.server.entity.item.Corpse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RPEntityTest {

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
	public void testCalculateRiskForCanHit() {
		RPEntity entity = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {
				// TODO Auto-generated method stub
			}

			@Override
			public void logic() {
				// TODO Auto-generated method stub
				
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

}
