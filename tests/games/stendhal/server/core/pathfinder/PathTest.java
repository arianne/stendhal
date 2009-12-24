package games.stendhal.server.core.pathfinder;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PathTest {
	static List<Node> expected;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		expected = new LinkedList<Node>();
		expected.add(new Node(0, 0));
		expected.add(new Node(1, 0));
		expected.add(new Node(1, 1));
		expected.add(new Node(2, 1));
		expected.add(new Node(2, 2));
		expected.add(new Node(3, 2));
		expected.add(new Node(3, 3));
		expected.add(new Node(4, 3));
		expected.add(new Node(4, 4));
		expected.add(new Node(5, 4));
		expected.add(new Node(5, 5));
		expected.add(new Node(6, 5));
		expected.add(new Node(6, 6));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for searchPathEntityIntInt.
	 */
	@Test
	public void testSearchPathEntityIntInt() {
		final Entity entity = new Entity() {
		};
		final StendhalRPZone zone = new StendhalRPZone("test", 10, 10);
		zone.add(entity);
		assertArrayEquals(expected.toArray(), Path.searchPath(entity, 6, 6).toArray());
	}
}
