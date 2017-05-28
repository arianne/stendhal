/***************************************************************************
 *               (C) Copyright 2003-2013 - Faiumoni e.V                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.block;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
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
        MockStendlRPWorld.get();
	}

	@Test
	public final void testReset() {
		Block b = new Block(true);
		b.setPosition(0, 0);
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
		Block b = new Block(true);
		b.setPosition(0, 0);
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
		Block b = new Block(false);
		b.setPosition(0, 0);
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
		Block b = new Block(true);
		b.setPosition(0, 0);
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
		Block b1 = new Block(true);
		b1.setPosition(0, 0);
		StendhalRPZone z = new StendhalRPZone("test", 10, 10);
		Player p = PlayerTestHelper.createPlayer("pusher");
		z.add(b1, false);

		// one successful push
		b1.push(p, Direction.RIGHT);
		assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));

		// now we add an obstacle right of b1
		Block b2 = new Block(true);
		b2.setPosition(02, 0);
		z.add(b2, false);

		// push should not be executed now and stay at the former place
		b1.push(p, Direction.RIGHT);
		assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
	}

    @Test
    public void testTimeOut() throws Exception {
		Block b1 = new Block(true);
		b1.setPosition(0, 0);
        StendhalRPZone z = new StendhalRPZone("test", 10, 10);
        Player p = PlayerTestHelper.createPlayer("pusher");
        z.add(b1, false);

        // one successful push
        b1.push(p, Direction.RIGHT);
        assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));

        // world progresses till timeout reached
        int currentTurn = SingletonRepository.getTurnNotifier().getCurrentTurnForDebugging();
        int endTurn = 1 + currentTurn + SingletonRepository.getRPWorld().getTurnsInSeconds(Block.RESET_TIMEOUT_IN_SECONDS);
        for (int i = 0; i <= endTurn; i++) {
            assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
            SingletonRepository.getRPWorld().nextTurn();
            SingletonRepository.getTurnNotifier().logic(i);
        }
        assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(0)));
    }

    /**
     * Test timeout in the situation that a player blocks the return position.
     */
    @Test
    public void testTimeOutBlocked() {
		Block b1 = new Block(true);
		b1.setPosition(0, 0);
        StendhalRPZone z = new StendhalRPZone("test", 10, 10);
        Player p = PlayerTestHelper.createPlayer("pusher");
        z.add(b1, false);
        z.add(p);

        // one successful push
        b1.push(p, Direction.RIGHT);
        assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));

        // world progresses till timeout reached
        int currentTurn = SingletonRepository.getTurnNotifier().getCurrentTurnForDebugging();
        int endTurn = 1 + currentTurn + SingletonRepository.getRPWorld().getTurnsInSeconds(Block.RESET_TIMEOUT_IN_SECONDS);
        for (int i = currentTurn; i <= endTurn; i++) {
            assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
            SingletonRepository.getRPWorld().nextTurn();
            SingletonRepository.getTurnNotifier().logic(i);
        }
        // Can't move
        assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
        // Move player out of the way so that the block may return to 0, 0
        p.setPosition(0, 1);
        // world progresses till timeout reached
        currentTurn = SingletonRepository.getTurnNotifier().getCurrentTurnForDebugging();
        endTurn = 1 + currentTurn + SingletonRepository.getRPWorld().getTurnsInSeconds(Block.RESET_AGAIN_DELAY);
        for (int i = currentTurn; i <= endTurn; i++) {
            assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(1)));
            SingletonRepository.getRPWorld().nextTurn();
            SingletonRepository.getTurnNotifier().logic(i);
        }
        assertThat(Integer.valueOf(b1.getX()), is(Integer.valueOf(0)));
    }
}
