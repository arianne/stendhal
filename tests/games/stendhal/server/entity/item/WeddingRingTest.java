package games.stendhal.server.entity.item;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class WeddingRingTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();
		
	}
	
	@Test
	public void testDescribe() {
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		assertThat(ring.describe(), is("You see a wedding ring."));
		ring.setInfoString("juliet");
		assertThat(ring.describe(), is("You see a wedding ring. Its engraving says: \"In eternal love to juliet\"."));
	}
	
	@Test
	public void testOnUsedNotMarried() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		
		ring.onUsed(romeo);
		assertEquals("This wedding ring hasn't been engraved yet.", romeo.events().get(0).get("text"));
	}
}
