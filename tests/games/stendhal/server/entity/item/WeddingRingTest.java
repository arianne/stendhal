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
	
	@Test
	public void testOnUsedNotOnline() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		
		ring.setInfoString("juliet");
		ring.onUsed(romeo);
		assertEquals("juliet is not online.", romeo.events().get(0).get("text"));
	}
	
	@Test
	public void testOnUsedOnlineButNotWearingTheRing() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		
		PlayerTestHelper.registerPlayer(juliet);
		
		ring.setInfoString("juliet");
		ring.onUsed(romeo);
		assertEquals("juliet is not wearing the wedding ring.", romeo.events().get(0).get("text"));
	}
	
	@Test
	public void testOnUsedOnlineButEngaged() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(juliet);
		
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		
		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		juliet.equipToInventoryOnly(ring2);
		
		ring.onUsed(romeo);
		
		assertEquals("Sorry, juliet has divorced you and is now engaged to someone else.", romeo.events().get(0).get("text"));
	}
	
	@Test
	public void testOnUsedOnlineButRemarried() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(juliet);
		
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		
		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("paris");
		juliet.equipToInventoryOnly(ring2);
		
		ring.onUsed(romeo);
		
		assertEquals("Sorry, juliet has divorced you and is now remarried.", romeo.events().get(0).get("text"));
	}
}
