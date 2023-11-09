/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Seed;
import games.stendhal.server.entity.mapstuff.area.FertileGround;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.FertileGrounds;
import utilities.AchievementTestHelper;


public class ProductionAchievementFactoryTest extends AchievementTestHelper {

	private Player player;
	private StendhalRPZone zone;

	private static final List<String> all_seeds = Arrays.asList("daisies", "lilia", "pansy");
	private static final List<String> all_bulbs = Arrays.asList("zantedeschia");
	private static final List<String> all_flowers = new ArrayList<String>() {{
		addAll(all_seeds);
		addAll(all_bulbs);
	}};
	private static final int per_unit = 1000;
	private static final int plots_width = 250;
	private static final int plots_height = per_unit * all_flowers.size() / plots_width;


	@Before
	public void setUp() {
		player = createPlayer("player");
		assertNotNull(player);
		init(player);

		zone = new StendhalRPZone("testzone", plots_width, plots_height);
		assertNotNull(zone);
		SingletonRepository.getRPWorld().addRPZone("testregion", zone);

		new FertileGrounds().configureZone(zone, new HashMap<String, String>() {{
			put("x", "0");
			put("y", "0");
			put("width", String.valueOf(plots_width));
			put("height", String.valueOf(plots_height));
		}});

		zone.add(player);
		player.setPosition(0, 0);
	}

	private void testZone() {
		assertEquals(plots_width, zone.getWidth());
		assertEquals(plots_height, zone.getHeight());

		for (int y = 0; y < plots_height; y++) {
			for (int x = 0; x < plots_width; x++) {
				assertTrue(groundIsFertile(x, y));
			}
		}

		assertEquals(zone, player.getZone());
		assertEquals(0, player.getX());
		assertEquals(0, player.getY());
	}

	@Test
	public void testSowingSeedsOfJoy() {
		testZone();

		assertNotNull(SingletonRepository.getEntityManager().getItem("seed"));
		assertNotNull(SingletonRepository.getEntityManager().getItem("bulb"));

		final String id = "production.sow.flowers.all";
		int x = 0;
		int y = 0;
		for (final String flower_name: all_flowers) {
			Seed seed = null;
			if (all_seeds.contains(flower_name)) {
				seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
			} else {
				seed = (Seed) SingletonRepository.getEntityManager().getItem("bulb");
			}
			assertNotNull(seed);
			seed.setItemData(flower_name);
			seed.setQuantity(per_unit);

			zone.add(seed);

			for (int idx = 0; idx < per_unit; idx++) {
				assertFalse(achievementReached(player, id));

				assertFalse(groundHasGrower(x, y));
				seed.setPosition(x, y);
				player.setPosition(x, y);
				assertTrue(seed.onUsed(player));
				assertTrue(groundHasGrower(x, y));
				assertEquals(idx+1, player.getQuantityOfSownItems(flower_name));

				x++;
				if (x >= plots_width) {
					// next row
					x = 0;
					y++;
				}
			}

			assertEquals(0, seed.getQuantity());
			zone.remove(seed);
		}
		assertTrue(achievementReached(player, id));
	}

	private boolean groundIsFertile(final int x, final int y) {
		for (final Entity ent: zone.getEntitiesAt(x, y)) {
			if (ent instanceof FertileGround) {
				return true;
			}
		}
		return false;
	}

	private boolean groundHasGrower(final int x, final int y) {
		for (final Entity ent: zone.getEntitiesAt(x, y)) {
			if (ent instanceof FlowerGrower) {
				return true;
			}
		}
		return false;
	}
}
