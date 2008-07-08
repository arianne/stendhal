package games.stendhal.server.entity.mapstuff.spawner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.area.Allotment;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class FlowerGrowerTest {

		@BeforeClass
		public static void setUpBeforeClass() throws Exception {
			MockStendlRPWorld.get();
			AreaEntity.generateRPClass();
			GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
		}

	@Test
	public void testOnFruitPicked() {
		FlowerGrower fl = new FlowerGrower();
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(fl);
		assertFalse(zone.getPlantGrowers().isEmpty());
		fl.onFruitPicked(null);
		assertTrue(zone.getPlantGrowers().isEmpty());
	}

	@Test
	public void testGetRandomTurnsForRegrow() {
		FlowerGrower fl = new FlowerGrower();
		assertThat(fl.getRandomTurnsForRegrow(), is(3));
	}

	
	@Test
	public void testFlowerGrower() {
		FlowerGrower fl = new FlowerGrower();
		assertThat(fl.getMaxRipeness(), is(4));
		
	}
	@Test
	public void testGetDescription() {
		FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		assertThat(fl.describe(), is("0"));
		fl.setRipeness(1);
		assertThat(fl.describe(), is("1"));
		fl.setRipeness(2);
		assertThat(fl.describe(), is("2"));
		fl.setRipeness(3);
		assertThat(fl.describe(), is("3"));
		fl.setRipeness(4);
		assertThat(fl.describe(), is("4"));
		fl.setRipeness(5);
		assertThat(fl.describe(), is("You see an unripe lilia."));
	}
	@Test
	public void testGrowOnFertileGround() throws Exception {
		FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		StendhalRPZone zone = new StendhalRPZone("zone");
		Entity entity = new Allotment();
		zone.add(entity);
		zone.add(fl);
		
		assertTrue(fl.isOnFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(),is(1));
	}
	@Test
	public void testGrowOnFertileGround2() throws Exception {
		FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		StendhalRPZone zone = new StendhalRPZone("zone");
		Entity entity = new Allotment();
		
		zone.add(fl);
		zone.add(entity);
		assertTrue(fl.isOnFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(),is(1));
	}
	@Test
	public void testGrowFertileGroundElsewhere() throws Exception {
		FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		StendhalRPZone zone = new StendhalRPZone("zone");
		Entity entity = new Allotment();
		entity.setPosition(10,10);
		zone.add(fl);
		zone.add(entity);
		assertFalse(fl.isOnFertileGround());
	}
	@Test
	public void testGrowOnInFertileGround() throws Exception {
		
		FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		
		assertFalse(fl.isOnFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(),is(0));
	}
}
