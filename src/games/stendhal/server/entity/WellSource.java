package games.stendhal.server.entity;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.events.UseListener;

import java.lang.ref.WeakReference;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A well source is a spot where a player can make a wish to gain an item. He
 * needs time and luck.
 *
 * Wishing takes 10 seconds; during this time, the player keep standing next to
 * the well source. At every well are two sources next to each other, so the
 * player can actually make 2 wishes at once.
 *
 * @author kymara (based on FishSource by daniel)
 *
 */
public class WellSource extends Entity implements UseListener {
	private class Wisher implements TurnListener {
		WeakReference<Player> playerRef;

		public Wisher(Player bob) {
			playerRef = new WeakReference<Player>(bob);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Wisher) {
				Wisher wisher = (Wisher) obj;
				return playerRef.get() == wisher.playerRef.get();
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		/**
		 * This method is called when the turn number is reached. NOTE: The
		 * <em>message</em> parameter is deprecated.
		 *
		 * @param currentTurn
		 *            The current turn number.
		 * @param message
		 *            The string that was used.
		 */
		public void onTurnReached(int currentTurn, String message) {
			Player player = playerRef.get();
			// check if the player is still logged in
			if (player != null) {
				// check if the player is still standing next to this well
				// source
				if (nextTo(player, 0.25)) {
					// roll the dice
					if (isSuccessful(player)) {
						String itemName = items[Rand.rand(items.length)];
						Item item = StendhalRPWorld.get().getRuleManager()
								.getEntityManager().getItem(itemName);
						// TODO: player bind the better prizes below:
						// horned_golden_helmet & dark_dagger
						if (item.getName().equals("dark_dagger")
								|| item.getName()
										.equals("horned_golden_helmet")) {
							/*
							 * Bound powerful items.
							 */
							item.put("bound", player.getName());
						} else if (item.getName().equals("money")) {
							/*
							 * Assign a random amount of money.
							 */
							((StackableItem) item)
									.setQuantity(Rand.roll1D100());
						}

						player.equip(item, true);
						player.sendPrivateText("You were lucky and found "
								+ Grammar.a_noun(itemName));
					} else {
						player.sendPrivateText("Your wish didn't come true.");
					}
				}
			}
		}

	}

	private String[] items = { "money", "wood", "iron_ore", "gold_nugget",
			"potion", "home_scroll", "greater_potion", "sapphire", "carbuncle",
			"horned_golden_helmet", "dark_dagger", "present" };

	/**
	 * The chance that wishing is successful.
	 */
	private static final double FINDING_PROBABILITY = 0.05;

	/**
	 * How long it takes to wish at a well (in seconds) TODO: randomize this
	 * number a bit.
	 */
	private static final int DURATION = 10;

	public WellSource() {
		setDescription("You see a wishing well. Something in it catches your eye.");
		setRPClass("well_source");
		put("type", "well_source");

		setResistance(0);
	}

	public WellSource(RPObject object) {
		super(object);
		setDescription("You see a wishing well. Something in it catches your eye.");
		setRPClass("well_source");
		put("type", "well_source");

		setResistance(0);
	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("well_source");
		grower.isA("entity");
	}


	/**
	 * Decides randomly if a wishing action should be successful.
	 *
	 * @return true iff the wishing player should get a prize.
	 */
	private boolean isSuccessful(Player player) {
		int random = Rand.roll1D100();
		return random <= (FINDING_PROBABILITY + player
				.useKarma(FINDING_PROBABILITY)) * 100;
	}

	/**
	 * Is called when a player has started wishing.
	 */
	public boolean onUsed(RPEntity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.nextTo(this)) {
				Wisher wish = new Wisher(player);
				// You can't start a new wishing action before
				// the last one has finished.
				if (TurnNotifier.get().getRemainingTurns(wish) == -1) {
					player.faceToward(this);
					player.notifyWorldAboutChanges();
					// remove 30 money from player as they throw a coin into the
					// well
					// some feedback is needed.
					if (player.isEquipped("money", 30)) {
						player.drop("money", 30);
						player
								.sendPrivateText("You throw 30 coins into the well and make a wish.");
						TurnNotifier.get().notifyInSeconds(DURATION, wish);
					} else {
						player
								.sendPrivateText("You need 30 coins to make a wish.");
					}
				}
			}
		}
		return false;
	}
}
