/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class ItemAchievementFactoryTest extends AchievementTestHelper {

	private final Player player;


	public ItemAchievementFactoryTest() {
		player = createPlayer("player");
	}

	@Before
	public void setUp() {
		assertNotNull(player);
		init(player);
	}

	private void checkSingleItemLoot(final String id, final String item,
			final int amount, final int inc) {
		assertTrue(achievementEnabled(id));
		while (player.getNumberOfLootsForItem(item) < amount) {
			assertFalse(achievementReached(player, id));
			player.incLootForItem(item, inc);
		}
		assertTrue(achievementReached(player, id));
	}

	private void checkSingleItemLoot(final String id, final String item,
			final int amount) {
		checkSingleItemLoot(id, item, amount, 1);
	}

	private void checkItemSetLoot(final String id, final String[] items) {
		assertTrue(achievementEnabled(id));
		for (final String item: items) {
			assertFalse(achievementReached(player, id));
			player.incLootForItem(item, 1);
		}
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testFirstPocketMoney() {
		checkSingleItemLoot("item.money.100", "money", 100, 10);
	}

	@Test
	public void testGoldshower() {
		checkSingleItemLoot("item.money.10000", "money", 10000, 100);
	}

	@Test
	public void testMovingUpInTheWorld() {
		checkSingleItemLoot("item.money.100000", "money", 100000, 1000);
	}

	@Test
	public void testYouDontNeedItAnymore() {
		checkSingleItemLoot("item.money.1000000", "money", 1000000, 10000);
	}

	@Test
	public void testCheeseWiz() {
		checkSingleItemLoot("item.cheese.2000", "cheese", 2000, 100);
	}

	@Test
	public void testHamHocks() {
		checkSingleItemLoot("item.ham.2500", "ham", 2500, 100);
	}

	@Test
	public void testAmazonsMenace() {
		checkItemSetLoot("item.set.red", new String[] {
				"red armor", "red helmet", "red cloak",
				"red legs", "red boots", "red shield"});
	}

	@Test
	public void testFeelingBlue() {
		checkItemSetLoot("item.set.blue", new String[] {
				"blue armor", "blue helmet", "blue striped cloak",
				"blue legs", "blue boots", "blue shield"});
	}

	@Test
	public void testNalworsBane() {
		checkItemSetLoot("item.set.elvish", new String[] {
				"elvish armor", "elvish hat", "elvish cloak",
				"elvish legs", "elvish boots", "elvish shield"});
	}

	@Test
	public void testShadowDweller() {
		checkItemSetLoot("item.set.shadow", new String[] {
				"shadow armor", "shadow helmet", "shadow cloak",
				"shadow legs", "shadow boots", "shadow shield"});
	}

	@Test
	public void testChaoticLooter() {
		checkItemSetLoot("item.set.chaos", new String[] {
				"chaos armor", "chaos helmet", "chaos cloak",
				"chaos legs", "chaos boots", "chaos shield"});
	}

	@Test
	public void testGoldenBoy() {
		checkItemSetLoot("item.set.golden", new String[] {
				"golden armor", "golden helmet", "golden cloak",
				"golden legs", "golden boots", "golden shield"});
	}

	@Test
	public void testComeToTheDarkSide() {
		checkItemSetLoot("item.set.black", new String[] {
				"black armor", "black helmet", "black cloak",
				"black legs", "black boots", "black shield"});
	}

	@Test
	public void testExcellentStuff() {
		checkItemSetLoot("item.set.mainio", new String[] {
				"mainio armor", "mainio helmet", "mainio cloak",
				"mainio legs", "mainio boots", "mainio shield"});
	}

	@Test
	public void testABitXeno() {
		checkItemSetLoot("item.set.xeno", new String[] {
				"xeno armor", "xeno helmet", "xeno cloak",
				"xeno legs", "xeno boots", "xeno shield"});
	}

	@Test
	public void testDragonSlayer() {
		checkItemSetLoot("item.cloak.dragon", new String[] {
				"black dragon cloak", "blue dragon cloak", "bone dragon cloak",
				"green dragon cloak", "red dragon cloak"});
	}

	@Test
	public void testRoyallyEndowed() {
		checkItemSetLoot(ItemAchievementFactory.ID_ROYAL, new String[] {
				"royal armor", "royal helmet", "royal cloak",
				"royal legs", "royal boots", "royal shield"});
	}

	@Test
	public void testMagicSupplies() {
		checkItemSetLoot(ItemAchievementFactory.ID_MAGIC, new String[] {
				"magic plate armor", "magic chain helmet", "magic plate legs",
				"magic plate boots", "magic cloak", "magic plate shield"});
	}
}
