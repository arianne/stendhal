package games.stendhal.server.trade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class TradeTest {

	@BeforeClass
	public static void beforeClass() {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void afterClass() {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testSuccessfullTrade() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone shopZone = new StendhalRPZone("trade_center");
		Shop edeka = new Shop(shopZone);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, price);
		assertThat(offer.getItem(), is(item));
		assertThat(offer.getPrice(), is(price));
		assertThat(Boolean.valueOf(bob.isEquipped(item.getName())),
				is(Boolean.FALSE));
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.TRUE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(bob.isEquipped("money")), is(Boolean.FALSE));
		edeka.fetchEarnings(bob);
		assertThat(Boolean.valueOf(bob.isEquipped("money", price.intValue())),
				is(Boolean.TRUE));
	}

	@Test
	public void testNonExistingOffer() {
		StendhalRPZone shopZone = new StendhalRPZone("trade_center");
		Shop edeka = new Shop(shopZone);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		Offer offer = new Offer(item, price, "george");

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.FALSE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
	}
	
	
	@Test
	public void testPoorBuyer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone shopZone = new StendhalRPZone("trade_center");
		Shop edeka = new Shop(shopZone);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		Integer tooFewMoney = price - 1;
		erniesMoney.setQuantity(tooFewMoney);
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, price);

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.FALSE));
		assertThat(ernie.isEquipped("money", tooFewMoney), is(Boolean.TRUE));
		assertThat(Boolean.valueOf(bob.isEquipped("money")), is(Boolean.FALSE));
		edeka.fetchEarnings(bob);
		assertThat(Boolean.valueOf(bob.isEquipped("money", tooFewMoney.intValue())),
				is(Boolean.FALSE));
		assertThat(Boolean.valueOf(bob.isEquipped("money", price.intValue())),
				is(Boolean.FALSE));
	}

}
