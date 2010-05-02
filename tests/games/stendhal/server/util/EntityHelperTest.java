package games.stendhal.server.util;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.BASEOBJECT;
import static games.stendhal.common.constants.Actions.BASESLOT;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class EntityHelperTest {

	private static final String TEST_ENTITY_HELPER = "test_entity_helper";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testEntityFromZoneByID() {
		int idRPO1 = 1;
		int idRPO2 = 2;
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		RPObject rpo1 = new Entity() {};
		rpo1.setID(new RPObject.ID(idRPO1, zone.getID()));
		zone.add(rpo1);
		RPObject rpo2 = new Entity() {};
		rpo2.setID(new RPObject.ID(idRPO2, zone.getID()));
		zone.add(rpo2);
		Entity entityFromZoneByID = EntityHelper.entityFromZoneByID(idRPO1, zone);
		assertThat(entityFromZoneByID, is(rpo1));
		assertThat(entityFromZoneByID, not((is(rpo2))));
	}

	@Test
	public void testEntityFromTargetName() {
		Entity player = new Entity() {};
		Entity entityFromTargetName = EntityHelper.entityFromTargetName("#1", player);
		assertThat(entityFromTargetName, not(notNullValue()));
		int idRPO1 = 1;
		int idRPO2 = 2;
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		Entity rpo1 = new Entity() {};
		zone.add(rpo1);
		RPObject rpo2 = new Entity() {};
		rpo2.setID(new RPObject.ID(idRPO2, zone.getID()));
		zone.add(rpo2);
		rpo1.setID(new RPObject.ID(idRPO1, zone.getID()));
		player.addSlot("test");
		player.getSlot("test").add(rpo2);
		Entity entityFromTargetName2 = EntityHelper.entityFromTargetName("#2", rpo1);
		assertThat(entityFromTargetName2, is(rpo2));
	}

	@Test
	public void testEntityFromTargetNameAnyZone() {
		Entity player = new Entity() {};
		Entity entityFromTargetName = EntityHelper.entityFromTargetNameAnyZone("1", player);
		assertThat(entityFromTargetName, not(notNullValue()));
		int idRPO1 = 1;
		int idRPO2 = 2;
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		Entity rpo1 = new Entity() {};
		rpo1.setID(new RPObject.ID(idRPO1, zone.getID()));
		zone.add(rpo1);
		RPObject rpo2 = new Entity() {};
		rpo2.setID(new RPObject.ID(idRPO2, zone.getID()));
		zone.add(rpo2);
		rpo1.addSlot("test");
		rpo1.getSlot("test").add(rpo2);
		Entity entityFromTargetName2 = EntityHelper.entityFromTargetNameAnyZone("#2", rpo1);
		assertThat(entityFromTargetName2, is(rpo2));
	}

	@Test
	public void testEntityFromSlot() {
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		Player player = PlayerTestHelper.createPlayer("helpertester");
		player.setID(new RPObject.ID(2, zone.getID()));
		RPAction action = new RPAction();
		assertNull(EntityHelper.entityFromSlot(player, action));
		Entity rpo1 = new Entity() {};
		rpo1.setID(new RPObject.ID(1, TEST_ENTITY_HELPER));
		zone.add(rpo1);
		assertNull(EntityHelper.entityFromSlot(player, action));
		zone.add(player);
		player.getSlot("bag").add(rpo1);
		action.put(BASESLOT, "bag");
		action.put(BASEOBJECT, player.getID().getObjectID());
		action.put(BASEITEM, 1);
		assertThat(EntityHelper.entityFromSlot(player, action), is(rpo1));
	}

}
