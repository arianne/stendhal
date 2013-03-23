package games.stendhal.server.entity.mapstuff.block;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.BlockTestHelper;

/**
 * Tests for the pushable block
 * 
 * @author madmetzger
 */
public class BlockTest {
	
	@BeforeClass
	public static void beforeClass() {
		BlockTestHelper.generateRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();
	}

	@Test
	public final void testReset() {
		Block b = new Block(0, 0, true);
		StendhalRPZone z = new StendhalRPZone("test", 10, 10);
		z.add(b);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));

		b.reset();
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));

		b.put("x", 2);
		b.reset();
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));

		b.put("y", 2);
		b.reset();
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));

		b.put("x", 2);
		b.put("y", 2);
		b.reset();
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
	}

	@Test
	public void testPush() {
		Block b = new Block(0, 0, true);
		StendhalRPZone z = new StendhalRPZone("test", 10, 10);
		Player p = PlayerTestHelper.createPlayer("pusher");
		z.add(b);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(p, Direction.RIGHT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(p, Direction.LEFT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(p, Direction.DOWN);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(1)));
		
		b.push(p, Direction.UP);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
	}
	
	@Test
	public void testMultiPush() {
		Block b = new Block(0, 0, false);
		StendhalRPZone z = new StendhalRPZone("test", 10, 10);
		Player p = PlayerTestHelper.createPlayer("pusher");
		
		z.add(b);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(p, Direction.RIGHT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(p, Direction.LEFT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.reset();
		//after a reset the block does not count as pushed
		
		b.push(p, Direction.DOWN);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(1)));
		
		// but a second push should be prevented
		b.push(p, Direction.UP);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(1)));
	}
		
	
	@Test
	public void testCoordinatesAfterPush() {
		Block b = new Block(0, 0, true);
		assertThat(Integer.valueOf(b.getXAfterPush(Direction.UP)), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getYAfterPush(Direction.UP)), is(Integer.valueOf(-1)));
		
		assertThat(Integer.valueOf(b.getXAfterPush(Direction.DOWN)), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getYAfterPush(Direction.DOWN)), is(Integer.valueOf(1)));
		
		assertThat(Integer.valueOf(b.getXAfterPush(Direction.LEFT)), is(Integer.valueOf(-1)));
		assertThat(Integer.valueOf(b.getYAfterPush(Direction.LEFT)), is(Integer.valueOf(0)));
		
		assertThat(Integer.valueOf(b.getXAfterPush(Direction.RIGHT)), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getYAfterPush(Direction.RIGHT)), is(Integer.valueOf(0)));
	}
	
	@Test
	public void testCollisionOnPush() throws Exception {
		Block b1 = new Block(0, 0, true);
		StendhalRPZone z = new StendhalRPZone("test", 10, 10);
		Player p = PlayerTestHelper.createPlayer("pusher");
		z.add(b1, false);
		
		// one successful push
		b1.push(p, Direction.RIGHT);
		assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
		
		// now we add an obstacle right of b1
		Block b2 = new Block(2, 0, true);
		z.add(b2, false);
		
		// push should not be executed now and stay at the former place
		b1.push(p, Direction.RIGHT);
		assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
	}

}
