package games.stendhal.server.entity.creature;

import static org.junit.Assert.*;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.RPObject;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class SheepTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		StendhalRPWorld world = MockStendlRPWorld.get();

		PlayerTestHelper.generateCreatureRPClasses();
		Sheep.generateRPClass();
	}

	@Test
	public void testSearchForFoodNotThere() {
		Sheep meh = new Sheep();
		StendhalRPZone zone = new StendhalRPZone("testzone", 10, 10);
		zone.add(meh);
		assertFalse(meh.searchForFood());
		
	}

	@Test
	public void testSearchForFoodNextTo() {
		Sheep meh = new Sheep();
		StendhalRPZone zone = new StendhalRPZone("testzone", 10, 10);
		zone.add(meh);
		RPObject foodobject = new RPObject();
		foodobject.put("amount", 1);

		SheepFood food = new SheepFood(foodobject);
		assertTrue(food.getAmount() > 0);
		zone.add(food);

		assertTrue(meh.searchForFood());
		assertEquals("eat", meh.getIdea());
	}

	@Test
	public void testSearchForFoodNotNextTo() {
		Sheep meh = new Sheep();
		StendhalRPZone zone = new StendhalRPZone("testzone", 10, 10);
		assertTrue(zone.getSheepFoodList().isEmpty());
		zone.add(meh);
		RPObject foodobject = new RPObject();
		foodobject.put("amount", 1);
		foodobject.put("x", 3);
		foodobject.put("y", 3);
		SheepFood food = new SheepFood(foodobject);
		assertTrue(food.getAmount() > 0);
		assertFalse(food.nextTo(meh));

		zone.add(food);
		assertFalse(zone.getSheepFoodList().isEmpty());
		assertTrue(meh.searchForFood());
		assertEquals("found food and thinks of it", "food", meh.getIdea());

	}

	@Test
	public void testSearchForBlockedFood() {
		Sheep meh = new Sheep();
		StendhalRPZone zone = new StendhalRPZone("testzone", 10, 10);
		zone.add(meh);
		RPObject foodobject = new RPObject();
		foodobject.put("amount", 1);
		foodobject.put("x", 2);
		foodobject.put("y", 2);
		SheepFood food = new SheepFood(foodobject);
		assertTrue(food.getAmount() > 0);
		assertFalse(food.nextTo(meh));

		zone.add(food);

		for (int x = 0; x <= zone.getWidth(); x++) {
			for (int y = 0; y <= zone.getHeight(); y++) {
				zone.collisionMap.setCollide(x, y);
			}
		}
		assertFalse("no path found", meh.searchForFood());
		assertEquals(null, meh.getIdea());

	}

	@Test
	public void testGetFarerNotBlockedFood() {

		boolean[][] collisions = { { false, true, false, true, false, false, false, false, false, false },
				{ false, true, false, true, false, false, false, false, false, false },
				{ false, true, false, true, false, false, false, false, false, false },
				{ false, true, false, true, false, false, false, false, false, false },
				{ false, true, true, true, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false } };
		StendhalRPZone zone = new StendhalRPZone("testzone", 9, 9);
		for (int x = 0; x <= zone.getWidth(); x++) {
			for (int y = 0; y <= zone.getHeight(); y++) {
				if (collisions[x][y]) {
					zone.collisionMap.setCollide(x, y);
				}
			}
		}

		Sheep meh = new Sheep();

		zone.add(meh);
		RPObject foodobject = new RPObject();
		foodobject.put("amount", 1);
		foodobject.put("x", 2);
		foodobject.put("y", 2);
		SheepFood food = new SheepFood(foodobject);
		assertTrue(food.getAmount() > 0);
		assertFalse(food.nextTo(meh));
		
		
		RPObject foodobject2 = new RPObject();
		foodobject2.put("amount", 1);
		foodobject2.put("x", 0);
		foodobject2.put("y", 3);
		SheepFood food2 = new SheepFood(foodobject2);
		assertTrue(food2.getAmount() > 0);
		assertFalse(food2.nextTo(meh));

		zone.add(food2);

		assertTrue("no path found", meh.searchForFood());
		assertEquals("food", meh.getIdea());

	}

	@Test
	public void testDescribe() {
		Sheep meh = new Sheep();
		assertEquals("", meh.getDescription());

	}

	@Ignore
	@Test
	public void testOnDeadEntity() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testOnDeadString() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testLogic() {
		fail("Not yet implemented");
	}

	@Test
	public void testSheep() {
		Sheep meh = new Sheep();

	}

	@Ignore
	@Test
	public void testSheepPlayer() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testSheepRPObjectPlayer() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testOnHungry() {
		Sheep meh = new Sheep();
		StendhalRPZone zone = new StendhalRPZone("testzone", 10, 10);
		zone.add(meh);
		assertFalse("there is no food in the zone yet, so what hunt", meh.onHungry());
		meh.setIdea("food");
		assertFalse("there is no food in the zone yet, so what hunt", meh.onHungry());
		meh.setIdea("food");
		meh.setSpeed(1.0);
		assertTrue("pretend hunting", meh.onHungry());

	}

	@Ignore
	@Test
	public void testOnIdle() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testOnStarve() {
		fail("Not yet implemented");
	}

}
