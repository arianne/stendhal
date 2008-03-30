package games.stendhal.server.core.pathfinder;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PathTest {
	static List<Node> expected;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearchPathEntityIntInt() {
		Entity entity = new Entity() {
		};
		StendhalRPZone zone = new StendhalRPZone("test", 10, 10);
		zone.add(entity);
		assertArrayEquals(expected.toArray(), Path.searchPath(entity, 6, 6).toArray());
	}
}
