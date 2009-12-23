/*
 * @(#) src/games/stendhal/server/entity/WellSource.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.source;

//
//

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPClass;

/**
 * A well source is a spot where a player can make a wish to gain an item. He
 * needs time and luck.
 * 
 * Wishing takes 10 seconds + randomized 4 seconds; during this time, the player keep standing next to
 * the well source. At every well are two sources next to each other, so the
 * player can actually make 2 wishes at once.
 * 
 * @author kymara (based on FishSource by daniel)
 * 
 */
public class WellSource extends PlayerActivityEntity {
	/**
	 * The list of possible rewards.
	 */
	private static final String[] items = { "money", "wood", "iron ore",
			"gold nugget", "potion", "home scroll", "greater potion",
			"sapphire", "carbuncle", "horned golden helmet", "dark dagger",
			"present" };
	
	/**
	 * The chance that wishing is successful.
	 */
	private static final double FINDING_PROBABILITY = 0.05;

	/**
	 * How long it takes to wish at a well (in seconds). 
	 */
	private static final int DURATION = 10;

	/**
	 * Create a wishing well source.
	 */
	public WellSource() {
		setRPClass("well_source");
		put("type", "well_source");

		setDescription("You see a wishing well. Something in it catches your eye.");
		setResistance(0);
	}
	
	/**
	 * source name.
	 */
	@Override
	public String getName() {
		return("wishing well");
	}

	//
	// WellSource
	//

	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("well_source");
		rpclass.isA("entity");
	}

	//
	// PlayerActivityEntity
	//

	/**
	 * Get the time it takes to perform this activity.
	 * 
	 * @return The time to perform the activity (in seconds).
	 */
	@Override
	protected int getDuration() {
		return DURATION + Rand.rand(5);
	}

	/**
	 * Decides if the activity can be done.
	 * 
	 * @return <code>true</code> if successful.
	 */
	@Override
	protected boolean isPrepared(final Player player) {
		if (player.isEquipped("money", 30)) {
			return true;
		} else {
			player.sendPrivateText("You need 30 coins to make a wish.");
			return false;
		}
	}

	/**
	 * Decides if the activity was successful.
	 * 
	 * @return <code>true</code> if successful.
	 */
	@Override
	protected boolean isSuccessful(final Player player) {
		final int random = Rand.roll1D100();
		return random <= (FINDING_PROBABILITY + player.useKarma(FINDING_PROBABILITY)) * 100;
	}

	/**
	 * Called when the activity has finished.
	 * 
	 * @param player
	 *            The player that did the activity.
	 * @param successful
	 *            If the activity was successful.
	 */
	@Override
	protected void onFinished(final Player player, final boolean successful) {
		if (successful) {
			final String itemName = items[Rand.rand(items.length)];
			final Item item = SingletonRepository.getEntityManager().getItem(itemName);

			if (itemName.equals("dark dagger")
					|| itemName.equals("horned golden helmet")) {
				/*
				 * Bound powerful items.
				 */
				item.setBoundTo(player.getName());
			} else if (itemName.equals("money")) {
				/*
				 * Assign a random amount of money.
				 */
				((StackableItem) item).setQuantity(Rand.roll1D100());
			}

			player.equipOrPutOnGround(item);
			player.sendPrivateText("You were lucky and found "
					+ Grammar.a_noun(itemName));
		} else {
			player.sendPrivateText("Your wish didn't come true.");
		}
	}

	/**
	 * Called when the activity has started.
	 * 
	 * @param player
	 *            The player starting the activity.
	 */
	@Override
	protected void onStarted(final Player player) {
		// remove 30 money from player as they throw a coin into the
		// well
		player.drop("money", 30);
		player.sendPrivateText("You throw 30 coins into the well and make a wish.");
	}
}
