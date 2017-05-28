/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.puzzle;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * tests for PuzzleBuildingBlock
 *
 * @author hendrik
 */
public class PuzzleBuildingBlockTest {
	private static final String ACTIVE = "active";

	@Test
	public void testDefineProperty() {
		PuzzleBuildingBlock block = new PuzzleBuildingBlock("zone", "name", null);
		block.put("variable", Boolean.FALSE);
		block.defineProperty("variable", "local.active == testzone.remote.active");

		assertThat(block.getDependencies(),
			allOf(hasItem("local"), hasItem("testzone.remote")));
	}


	@Test
	public void testEvalutateProperty() {

		// setup zones with puzzle

		StendhalRPWorld.get().addRPZone("testing", new StendhalRPZone("testzone"));

		PuzzleBuildingBlock door = new PuzzleBuildingBlock("zone", "name", null);
		door.put(ACTIVE, Boolean.FALSE);
		door.defineProperty(ACTIVE, "local.active == testzone.remote.active");

		PuzzleBuildingBlock localSwitch = new PuzzleBuildingBlock("zone", "local", null);
		localSwitch.put(ACTIVE, Boolean.TRUE);

		PuzzleBuildingBlock remoteSwitch = new PuzzleBuildingBlock("testzone", "remote", null);
		remoteSwitch.put(ACTIVE, Boolean.TRUE);

		PuzzleEventDispatcher.get().register(door);
		PuzzleEventDispatcher.get().register(localSwitch);
		PuzzleEventDispatcher.get().register(remoteSwitch);


		// test input propagation
		door.onInputChanged();
		assertTrue(((Boolean) door.get(ACTIVE)).booleanValue());

		// no change
		door.onInputChanged();
		assertTrue(((Boolean) door.get(ACTIVE)).booleanValue());

		// remote change
		remoteSwitch.put(ACTIVE, Boolean.FALSE);
		assertFalse(((Boolean) door.get(ACTIVE)).booleanValue());

		// local change
		localSwitch.put(ACTIVE, Boolean.FALSE);
		assertTrue(((Boolean) door.get(ACTIVE)).booleanValue());

	}


}
