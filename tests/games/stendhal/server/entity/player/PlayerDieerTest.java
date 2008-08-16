package games.stendhal.server.entity.player;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.CorpseTestHelper;

public class PlayerDieerTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		CorpseTestHelper.generateRPClasses();
	}

	@Test
	public void testPlayerDieer() {
		final Player hasRing = PlayerTestHelper.createPlayer("bob");
		hasRing.setXP(10000);
		
		final Player hasNoRing = PlayerTestHelper.createPlayer("bob");
		hasNoRing.setXP(10000);
		
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		zone.add(hasRing);
		zone.add(hasNoRing);
		
		final RingOfLife ring = new RingOfLife();
		hasRing.equip("bag", ring);
		
		assertFalse(ring.isBroken());
		final PlayerDieer dierWithRing = new PlayerDieer(hasRing);
		dierWithRing.onDead(new Entity() {
		});
		
		final PlayerDieer dierWithoutRing1 = new PlayerDieer(hasNoRing);
		dierWithoutRing1.onDead(new Entity() {
		});
		assertTrue(ring.isBroken());
		
		assertThat("ring wearer looses 1 percent", hasRing.getXP(), is(9900));
		assertThat("normal player loses 10 percent", hasNoRing.getXP(), is(9000));
		hasRing.setXP(10000);
		dierWithRing.onDead(new Entity() {
		});
		assertThat("ring wearer with broken ring loses 10 percent", hasRing.getXP(), is(9000));
		
	}
	
	

}
