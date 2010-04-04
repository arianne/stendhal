package games.stendhal.server.maps.semos.tavern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.tavern.market.TradeCenterZoneConfigurator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test the trade center npc
 * 
 * @author madmetzger
 */
public class TradeMangerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_semos_tavern_0";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new TradeCenterZoneConfigurator());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public TradeMangerNPCTest() {
		super(ZONE_NAME, "Harold");
	}

	/**
	 * Tests for successful placement of an offer.
	 */
	@Test
	public void testSuccessfullOfferPlacement() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem playersMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(1500);
		playersMoney.setQuantity(price);
		player.equipToInventoryOnly(item);
		player.equipToInventoryOnly(playersMoney);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell axe 150000"));
		assertEquals("Do you want to sell an axe for 150000 money? It would cost you 1500 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 1500.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals(
				"Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}

	
	/**
	 * Check that creating offers for zero price cost.
	 * (Harold needs his provision; we need to charge for those to
	 * prevent cheating the trade score)
	 */
	@Test
	public void testCreateOfferForFree() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		PlayerTestHelper.equipWithItem(player, "axe");
		PlayerTestHelper.equipWithStackableItem(player, "money", 42);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell axe 0"));
		assertEquals("Do you want to sell an axe for 0 money? It would cost you 1 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 1.", getReply(npc));
		
		assertEquals("Making a free offer should cost", 41, ((StackableItem) player.getFirstEquipped("money")).getQuantity());

		assertTrue(en.step(player, "bye"));
		assertEquals(
				"Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}
}
