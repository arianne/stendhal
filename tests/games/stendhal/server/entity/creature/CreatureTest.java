package games.stendhal.server.entity.creature;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.CreatureTestHelper;

public class CreatureTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		CreatureTestHelper.generateRPClasses();
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
	public void testGetNearestEnemy() {
		
		Player onebyone = PlayerTestHelper.createPlayer("bob");
		onebyone.setPosition(6, 0);
		MockCreature sevenbyseven = new MockCreature();
	
		StendhalRPZone zone = new StendhalRPZone("test", 20 , 20);
		zone.add(sevenbyseven);
		zone.add(onebyone);
		enemies.add(onebyone);
		assertSame(onebyone, sevenbyseven.getNearestEnemy(6));
		assertSame(onebyone, sevenbyseven.getNearestEnemy(5));
		assertNull(sevenbyseven.getNearestEnemy(4));
		
		sevenbyseven.setSize(7, 7);
		onebyone.setPosition(10, 10);
		assertSame(onebyone, sevenbyseven.getNearestEnemy(7));
		assertSame(onebyone, sevenbyseven.getNearestEnemy(6));
		assertSame(onebyone, sevenbyseven.getNearestEnemy(5));
		assertNull(sevenbyseven.getNearestEnemy(4));
	}

	
	private static List<RPEntity> enemies  = new LinkedList<RPEntity>();
	private static class MockCreature extends Creature {
		

		
		@Override
		protected List<RPEntity> getEnemyList() {
			
			return enemies; 
		}
	}

}
