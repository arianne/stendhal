package games.stendhal.server.entity.creature;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.server.game.db.DAORegister;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import utilities.PlayerTestHelper;
import utilities.RPClass.CreatureTestHelper;


public class ItemGuardCreatureTest {
	
	@Before
	public void setUp() {
		MockStendlRPWorld.get();
		PlayerTestHelper.generateNPCRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();
		CreatureTestHelper.generateRPClasses();
		DAORegister.get().register(StendhalKillLogDAO.class, new StendhalKillLogDAO ());
	}
	
	@After
	public void tearDown() {
		MockStendlRPWorld.reset();
	}
	
	@Test
	public void testOnDead() {
		StendhalRPZone zone = new StendhalRPZone("test zone");
		Creature copy = SingletonRepository.getEntityManager().getCreature("rat");
		ItemGuardCreature creature = new ItemGuardCreature(copy, "knife", "test_quest", "start");
		zone.add(creature);
		Player player = PlayerTestHelper.createPlayer("bob");
		creature.onDead(player);
		assertTrue(player.getFirstEquipped("knife") == null);
		player.setQuest("test_quest","notStarted");
		creature.onDead(player);
		assertTrue(player.getFirstEquipped("knife") == null);
		player.setQuest("test_quest","start");
		creature.onDead(player);
		assertTrue(player.getFirstEquipped("knife") != null);
	}

}
