package games.stendhal.server.entity.item;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class FoodMillTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testOnUsedSugarMill() throws Exception {
		String name = "sugar mill";
		String clazz = "";
		String subclass = "";
		Map<String, String> attributes = new HashMap<String, String>();
		FoodMill fm = new FoodMill(name, clazz, subclass, attributes);
		Player user  =  PlayerTestHelper.createPlayer("fmbob");
		fm.onUsed(user);
		assertEquals("You should be carrying the sugar mill in order to use it.", PlayerTestHelper.getPrivateReply(user));

		user.equip("bag", fm);
		fm.onUsed(user);
		assertEquals("You should hold the sugar mill in either hand in order to use it.", PlayerTestHelper.getPrivateReply(user));
		user.equip("lhand", fm);
		fm.onUsed(user);

		assertEquals("Your other hand looks empty.", PlayerTestHelper.getPrivateReply(user));
		PlayerTestHelper.equipWithItemToSlot(user, "cheese", "rhand");
		fm.onUsed(user);
		assertEquals("You need to have at least a sugar cane in your other hand", PlayerTestHelper.getPrivateReply(user));

		user.drop("cheese");

		PlayerTestHelper.equipWithItemToSlot(user, "sugar cane", "rhand");

		fm.onUsed(user);
		assertEquals("You don't have an empty sack with you", PlayerTestHelper.getPrivateReply(user));
		PlayerTestHelper.equipWithItem(user, "empty sack");
		fm.onUsed(user);
		assertTrue(user.isEquipped("sugar"));
	}
	@Test
	public void testOnUsedScrollEraser() throws Exception {
		String name = "scroll eraser";
		String clazz = "";
		String subclass = "";
		Map<String, String> attributes = new HashMap<String, String>();
		FoodMill fm = new FoodMill(name, clazz, subclass, attributes);
		Player user  =  PlayerTestHelper.createPlayer("fmbob");
		fm.onUsed(user);
		assertEquals("You should be carrying the scroll eraser in order to use it.", PlayerTestHelper.getPrivateReply(user));
		assertFalse(user.isEquipped("empty scroll"));

		user.equip("bag", fm);
		fm.onUsed(user);
		assertEquals("You should hold the scroll eraser in either hand in order to use it.", PlayerTestHelper.getPrivateReply(user));
		assertFalse(user.isEquipped("empty scroll"));

		user.equip("lhand", fm);
		fm.onUsed(user);
		assertEquals("Your other hand looks empty.", PlayerTestHelper.getPrivateReply(user));
		assertFalse(user.isEquipped("empty scroll"));

		PlayerTestHelper.equipWithItemToSlot(user, "cheese", "rhand");
		PlayerTestHelper.equipWithItemToSlot(user, "marked scroll","bag");
		fm.onUsed(user);
		assertEquals("You need to have at least a marked scroll in your other hand", PlayerTestHelper.getPrivateReply(user));
		assertFalse(user.isEquipped("empty scroll"));

		user.drop("cheese");
		user.drop("marked scroll");
		PlayerTestHelper.equipWithItemToSlot(user, "marked scroll","rhand");
		fm.onUsed(user);
		assertTrue(user.isEquipped("empty scroll"));
		assertFalse(user.isEquipped("marked scroll"));
	}

	@Test
	public void testFoodMill()
	 throws Exception {
		String name = "sugar mill";
		String clazz = "";
		String subclass = "";
		Map<String, String> attributes = new HashMap<String, String>();
		new FoodMill(name, clazz, subclass, attributes);

	}

}
