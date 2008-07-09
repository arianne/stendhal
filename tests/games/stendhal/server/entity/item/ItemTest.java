package games.stendhal.server.entity.item;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class ItemTest {
	
	private static final String ZONE_NAME = "ITEMTESTZONE";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
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
	public void testGetName() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals("name1", mo.getName());
	}

	@Test
	public void testGetAreaRectangle2DDoubleDouble() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		Rectangle2D rect = new Rectangle2D.Double();
		assertEquals(rect.getCenterX(), 0.0, 0.001);
		assertEquals(rect.getCenterY(), 0.0, 0.001);

		mo.getArea(rect, 0.0, 0.0);
		assertEquals(rect.getMinX(), 0.0, 0.001);
		assertEquals(rect.getMinY(), 0.0, 0.001);
		assertEquals(rect.getMaxX(), 1.0, 0.001);
		assertEquals(rect.getMaxY(), 1.0, 0.001);

	}

	@Test
	public void testDescribe() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals("", mo.getDescription());
	}

	@Test
	public void testItemStringStringStringMapOfStringString() {
		Map<String, String> attribs = new HashMap<String, String>();
		attribs.put("att_1", "val_1");
		attribs.put("att_2", "val_2");
		Item mo = new Item("name1", "class", "subclass", attribs);
		assertEquals("val_1", mo.get("att_1"));
		assertEquals("val_2", mo.get("att_2"));
		assertNull(mo.get("Noexistant"));
	}

	@Test
	public void testItemItemwithAttributes() {
		Map<String, String> attribs = new HashMap<String, String>();

		attribs.put("att_1", "val_1");
		attribs.put("att_2", "val_2");
		Item mo = new Item("name1", "class", "subclass", attribs);
		assertEquals("val_1", mo.get("att_1"));
		assertEquals("val_2", mo.get("att_2"));
		Item itemcopy = new Item(mo);
		assertEquals("val_1", itemcopy.get("att_1"));
		assertEquals("val_2", itemcopy.get("att_2"));
	}

	@Test
	// slots are copied by copy constructor
	public void testItemItem() {
		LinkedList<String> slots = new LinkedList<String>();
		slots.add("slot_1");
		slots.add("slot_2");
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		mo.setEquipableSlots(slots);
		assertEquals(slots, mo.getPossibleSlots());
	}

	@Test
	public void testSetEquipableSlots() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		LinkedList<String> slots = new LinkedList<String>();
		slots.add("one");
		slots.add("two");
		slots.add("three");
		mo.setEquipableSlots(slots);
		assertEquals(slots, mo.getPossibleSlots());
		slots.add("one");
		slots.add("one");
		slots.add("one");
		mo.setEquipableSlots(slots);
		assertEquals(slots, mo.getPossibleSlots());
		// TODO: should there be several slots of the same name?
	}

	@Test
	public void testGetAttack() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals(0, mo.getAttack());
		mo.put("atk", 3);
		assertEquals(3, mo.getAttack());
		mo.put("atk", 2);
		assertEquals(2, mo.getAttack());
	}

	@Test
	public void testGetDefense() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals(0, mo.getDefense());
		mo.put("def", 3);
		assertEquals(3, mo.getDefense());
		mo.put("def", 2);
		assertEquals(2, mo.getDefense());
	}

	@Test
	public void testIsPersistent() {
		Item mo = new Item("name1", "myClass", "subclass",
				new HashMap<String, String>());
		assertFalse(mo.isPersistent());
		mo.put("persistent", 1);
		assertTrue(mo.isPersistent());
		mo.put("persistent", 2);
		assertFalse(mo.isPersistent());
	}

	@Test
	public void testIsOfClass() {
		Item mo = new Item("name1", "myClass", "subclass",
				new HashMap<String, String>());
		assertTrue(mo.isOfClass("myClass"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetItemClass() {
		Item mo = new Item("name1", "myClass", "subclass",
				new HashMap<String, String>());
		assertEquals("myClass", mo.getItemClass());
		new Item("name1", null, "subclass", new HashMap<String, String>());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetItemSubclass() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertEquals("mySubclass", mo.getItemSubclass());
		new Item("name1", "myClass", null, new HashMap<String, String>());
	}

	@Test
	public void testGetQuantity() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertEquals("defaultquantity", 1, mo.getQuantity());
	}

	@Test
	public void testGetPossibleSlots() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertTrue(mo.getPossibleSlots().isEmpty());
	}

	@Test
	public void testToString() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		// ignore attribute listing because their sort order is not reliable
		assertTrue(mo.toString().matches(
				"Item, RPObject with Attributes of Class\\(item\\): \\[.*\\] and RPSlots  and RPLink  and RPEvents "));
	}

	@Test
	public void testOnPutOnGround() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.onPutOnGround(PlayerTestHelper.createPrivateTextMockingTestPlayer("player"));
	}

	@Test
	public void testOnRemoveFromGround() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.onRemoveFromGround();
	}

	@Test
	public void testOnTurnReached() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		zone.add(mo);
		assertTrue(zone.has(mo.getID()));
		mo.onTurnReached(1);
		assertNotNull(mo.getZone());
		assertFalse(zone.has(mo.getID()));
	}

	@Test
	public void testRemoveOne() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		MockStendlRPWorld.get().addRPZone(zone);
		zone.add(mo);
		assertTrue(zone.has(mo.getID()));
		mo.removeOne();
		assertNotNull(mo.getZone());
		assertFalse(zone.has(mo.getID()));
		
		
	}

	@Test
	public void testCanBeEquippedIn() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertTrue("ground is null", mo.canBeEquippedIn(null));  
		LinkedList<String> slots = new LinkedList<String>();
		slots.add("one");
		slots.add("two");
		slots.add("three");

		mo.setEquipableSlots(slots);
		assertTrue(mo.canBeEquippedIn("one"));
		assertTrue(mo.canBeEquippedIn("two"));
		assertTrue(mo.canBeEquippedIn("three"));
		assertFalse(mo.canBeEquippedIn("four"));
	}

	@Test
	public void testRemoveFromWorld() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		MockStendlRPWorld.get().addRPZone(zone);
		zone.add(mo);
		assertTrue(zone.has(mo.getID()));
		
		mo.removeFromWorld();
		assertNotNull(mo.getZone());
		assertFalse(zone.has(mo.getID()));
	}

	@Test
	public void testGetBoundTo() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertNull(mo.getBoundTo());
		mo.setBoundTo("bob");
		assertTrue(mo.isBound());
		assertTrue(mo.isBoundTo(PlayerTestHelper.createPlayer("bob")));
		assertEquals("bob", mo.getBoundTo());
		
		mo.setBoundTo(null);
		assertFalse(mo.isBound());
		assertFalse(mo.isBoundTo(PlayerTestHelper.createPlayer("bob")));
		assertThat(mo.getBoundTo(), not(is("bob")));
	}

	

}
