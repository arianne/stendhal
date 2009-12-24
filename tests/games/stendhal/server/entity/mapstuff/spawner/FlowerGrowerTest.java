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

import marauroa.common.game.RPClass;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class FlowerGrowerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		if (!RPClass.hasRPClass("area")) {
			AreaEntity.generateRPClass();
		}
		GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
	}

	/**
	 * Tests for onFruitPicked.
	 */
	@Test
	public void testOnFruitPicked() {
		final FlowerGrower fl = new FlowerGrower();
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(fl);
		assertFalse(zone.getPlantGrowers().isEmpty());
		fl.onFruitPicked(null);
		assertTrue(zone.getPlantGrowers().isEmpty());
	}

	/**
	 * Tests for flowerGrower.
	 */
	@Test
	public void testFlowerGrower() {
		final FlowerGrower fl = new FlowerGrower();
		assertThat(fl.getMaxRipeness(), is(4));

	}

	/**
	 * Tests for getDescription.
	 */
	@Test
	public void testGetDescription() {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		assertThat(fl.describe(),
				is("You see a seed which has just been planted."));
		fl.setRipeness(1);
		assertThat(fl.describe(), is("Something is sprouting from the ground."));
		fl.setRipeness(2);
		assertThat(fl.describe(),
				is("A plant is growing here, and you can already see foliage."));
		fl.setRipeness(3);
		assertThat(
				fl.describe(),
				is("You see a plant growing a lilia, it is nearly at full maturity."));
		fl.setRipeness(4);
		assertThat(
				fl.describe(),
				is("You see a fully grown lilia, ready to pull from the ground."));
		fl.setRipeness(5);
		assertThat(fl.describe(), is("You see an unripe lilia."));
	}

	/**
	 * Tests for getDescriptionAnyitem.
	 */
	@Test
	public void testGetDescriptionAnyitem() {
		final FlowerGrower fl = new FlowerGrower("someotherItem");
		fl.setRipeness(0);
		assertThat(fl.describe(),
				is("You see a seed which has just been planted."));
		fl.setRipeness(1);
		assertThat(fl.describe(), is("Something is sprouting from the ground."));
		fl.setRipeness(2);
		assertThat(fl.describe(),
				is("A plant is growing here, and you can already see foliage."));
		fl.setRipeness(3);
		assertThat(
				fl.describe(),
				is("You see a plant growing a someotheritem, it is nearly at full maturity."));
		fl.setRipeness(4);
		assertThat(
				fl.describe(),
				is("You see a fully grown someotheritem, ready to pull from the ground."));
		fl.setRipeness(5);
		assertThat(fl.describe(), is("You see an unripe someotherItem."));
	}

	/**
	 * Tests for growOnFertileGround.
	 */
	@Test
	public void testGrowOnFertileGround() throws Exception {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();
		zone.add(entity);
		zone.add(fl);

		assertTrue(fl.isOnFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(1));
	}

	/**
	 * Tests for growOnFertileGround2.
	 */
	@Test
	public void testGrowOnFertileGround2() throws Exception {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();

		zone.add(fl);
		zone.add(entity);
		assertTrue(fl.isOnFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(1));
	}

	/**
	 * Tests for growFertileGroundElsewhere.
	 */
	@Test
	public void testGrowFertileGroundElsewhere() throws Exception {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();
		entity.setPosition(10, 10);
		zone.add(fl);
		zone.add(entity);
		assertFalse(fl.isOnFertileGround());
	}

	/**
	 * Tests for growOnInFertileGround.
	 */
	@Test
	public void testGrowOnInFertileGround() throws Exception {

		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);

		assertFalse(fl.isOnFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(0));
	}
}
