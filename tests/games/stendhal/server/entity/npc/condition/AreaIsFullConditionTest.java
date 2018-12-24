/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

/**
 * Tests for {@link AreaIsFullCondition}.
 */
public class AreaIsFullConditionTest {

	@Test
	public void testAreaFullWhenMaxIsZero() {
		final StendhalRPZone zone = new StendhalRPZone("zone");
		Area area = new Area(zone, 0, 0, 1, 1);
		Player player = PlayerTestHelper.createPlayer("player");

		AreaIsFullCondition condition = new AreaIsFullCondition(area, 0);

		assertTrue(condition.fire(player, someSentence(), someNpc()));
	}

	@Test
	public void testAreaNotFullWhenMaxIsOneAndPlayerOutside() {
		final StendhalRPZone zone = new StendhalRPZone("zone");
		Area area = new Area(zone, 0, 0, 1, 1);
		Player player = PlayerTestHelper.createPlayer("player");

		AreaIsFullCondition condition = new AreaIsFullCondition(area, 1);

		assertFalse(condition.fire(player, someSentence(), someNpc()));
	}

	@Test
	public void testAreaFullWhenMaxIsOneAndPlayerInside() {
		final StendhalRPZone zone = new StendhalRPZone("zone");
		Area area = new Area(zone, 0, 0, 1, 1);
		Player player = PlayerTestHelper.createPlayer("player");
		zone.add(player);

		AreaIsFullCondition condition = new AreaIsFullCondition(area, 1);

		assertTrue(condition.fire(player, someSentence(), someNpc()));
	}

	private Sentence someSentence() {
		return ConversationParser.parse("testAndConditionText");
	}

	private Entity someNpc() {
		return SpeakerNPCTestHelper.createSpeakerNPC();
	}
}
