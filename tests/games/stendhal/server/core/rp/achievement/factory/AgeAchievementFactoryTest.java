/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class AgeAchievementFactoryTest extends AchievementTestHelper {

	private Player player;


	@Before
	public void setUp() {
		player = createPlayer(AgeAchievementFactoryTest.class.getSimpleName() + "Player");
		assertThat(player, notNullValue());
		init(player);
	}

	/**
	 * Simulates player aging logic for a turn.
	 *
	 * The documentation suggests that `Player.getAge` represents the player's age in minutes. But
	 * `MockStendhalRPRuleProcessor.beginTurn` increments this value every turn making the
	 * representation actually age in turns. So we need to create a simulation that ages the player
	 * 1 minute for every 200 turns.
	 */
	private void simulateLogic(final int currentTurn) {
		if ((currentTurn + 1) % MathHelper.TURNS_IN_ONE_MINUTE == 0) {
			// age player 1 minute
			player.setAge(player.getAge() + 1);
		}
	}

	/**
	 * Ages player and checks achievement status.
	 *
	 * @param reqHours
	 *   Age of player in hours required for achievement.
	 * @param score
	 *   Achievement score value.
	 */
	private void testAchievement(final int reqHours, final int score) {
		final String id = "age.hours." + String.format("%05d", reqHours);
		final Achievement ac = getById(id);
		assertThat(ac, notNullValue());

		System.out.println("testing age achievement: " + ac.getTitle() + " (" + reqHours + " hours)");
		assertThat(ac.isActive(), is(true));
		assertThat(ac.getBaseScore(), is(score));

		assertThat(player.getAge(), is(0));
		final int reqMinutes = reqHours * MathHelper.MINUTES_IN_ONE_HOUR;
		final int reqTurns = reqMinutes * MathHelper.TURNS_IN_ONE_MINUTE;
		// set age to 1 hour before target to expedite test
		final int startMinutes = reqMinutes - MathHelper.MINUTES_IN_ONE_HOUR;
		final int startTurns = startMinutes * MathHelper.TURNS_IN_ONE_MINUTE;
		player.setAge(startMinutes);
		assertThat(player.getAge(), is(startMinutes));
		assertThat(player.getAge(), greaterThan(-1));

		assertThat(achievementReached(player, id), is(false));

		int currentTurn;
		for (currentTurn = startTurns; currentTurn < reqTurns; currentTurn++) {
			assertThat(achievementReached(player, id), is(false));
			// simulate turn
			simulateLogic(currentTurn);
		}

		final int totalMinutes = player.getAge();
		final int totalHours = totalMinutes / MathHelper.MINUTES_IN_ONE_HOUR;
		assertThat(currentTurn, is(reqTurns));
		assertThat(totalMinutes, is(reqMinutes));
		assertThat(totalHours, is(reqHours));
		assertThat(achievementReached(player, id), is(true));
	}

	@Test
	public void testCuttingTeeth() {
		testAchievement(24, Achievement.EASY_BASE_SCORE);
	}

	@Test
	public void testAdolescent() {
		testAchievement(168, Achievement.EASY_BASE_SCORE);
	}

	@Test
	public void testAcclimating() {
		testAchievement(744, Achievement.MEDIUM_BASE_SCORE);
	}

	@Test
	public void testSturdyFoundation() {
		testAchievement(4368, Achievement.HARD_BASE_SCORE);
	}

	@Test
	public void testDevout() {
		testAchievement(8760, Achievement.EXTREME_BASE_SCORE);
	}
}
