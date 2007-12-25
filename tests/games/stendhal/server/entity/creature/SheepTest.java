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
		assertFalse(meh.searchForFood(true));
		assertFalse(meh.searchForFood(false));
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

		assertTrue(meh.searchForFood(false));
		assertEquals("eat", meh.getIdea());
	}

	@Test
	public void testSearchForFoodNotNextTo() {
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

		assertTrue(meh.searchForFood(false));
		assertEquals("found food and thinks of it", "food", meh.getIdea());

		for (int x = 0; x <= zone.getWidth(); x++) {
			for (int y = 0; y <= zone.getHeight(); y++) {
				zone.collisionMap.setCollide(x, y);
			}
		}
		assertTrue(meh.searchForFood(false));
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
		assertTrue(meh.searchForFood(false));
		assertEquals("found food and thinks of it", "food", meh.getIdea());

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

	@Ignore
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
