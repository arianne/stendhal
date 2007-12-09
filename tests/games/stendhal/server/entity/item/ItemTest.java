package games.stendhal.server.entity.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import utilities.PlayerTestHelper;

public class ItemTest {
	private static final String ZONE_NAME = "0_semos_village_w";

	private static final String ZONE_CONTENT = "Level 0/semos/village_w.tmx";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		StendhalRPWorld world = StendhalRPWorld.get();
		if (StendhalRPWorld.get().getRPZone(ZONE_NAME) == null) {
			world.addArea(ZONE_NAME, ZONE_CONTENT);
		}

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
	public void testGenerateRPClass() {
		Entity.generateRPClass();
		Item.generateRPClass();

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
		assertEquals(rect.getCenterX(), 0.0,0.001);
		assertEquals(rect.getCenterY(), 0.0,0.001);

		mo.getArea(rect, 0.0, 0.0);
		assertEquals(rect.getMinX(), 0.0,0.001);
		assertEquals(rect.getMinY(), 0.0,0.001);
		assertEquals(rect.getMaxX(), 1.0,0.001);
		assertEquals(rect.getMaxY(), 1.0,0.001);

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
		assertTrue(mo.toString().matches(	// ignore attribute listing because their sort order is not reliable
				"Item, RPObject with Attributes of Class\\(item\\): \\[.*\\] and RPSlots  and RPLink  and RPEvents "));
	}

	@Test
	public void testOnPutOnGround() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.onPutOnGround(PlayerTestHelper.createPlayer());

	}

	@Test
	public void testOnRemoveFromGround() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.onRemoveFromGround();

	}

	@Test
	public void testOnTurnReached() throws SAXException, IOException {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());

		mo.put("id", 1);
		mo.put("zoneid", ZONE_NAME);
		mo.onTurnReached(1);
	}

	@Test
	public void testRemoveOne() throws SAXException, IOException {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.put("id", 2);
		mo.put("zoneid", ZONE_NAME);
		mo.removeOne();
	}

	@Test
	public void testCanBeEquippedIn() {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertTrue(mo.canBeEquippedIn(null)); // ground is null
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
	public void testRemoveFromWorld() throws SAXException, IOException {
		Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.put("id", 3);
		mo.put("zoneid", ZONE_NAME);
		mo.removeFromWorld();
	}

}
