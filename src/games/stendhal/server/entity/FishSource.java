package games.stendhal.server.entity;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.events.UseListener;

import java.lang.ref.WeakReference;

import marauroa.common.game.RPClass;

/**
 * A fish source is a spot where a player can fish. He needs a fishing rod, time
 * and luck. Before he catches fish he needs to make a license.
 *
 * Fishing takes 8 seconds; during this time, the player keep standing next to
 * the fish source. In fact, the player only has to be there when the
 * prospecting action has finished. Therefore, make sure that two fish sources
 * are always at least 8 sec of walking away from each other, so that the player
 * can't fish at several sites simultaneously.
 *
 * @author dine
 *
 */
public class FishSource extends Entity implements UseListener {
	private static final String NEEDED_EQUIPMENT = "fishing_rod";

	private class Fisher implements TurnListener {
		WeakReference<Player> playerRef;

		public Fisher(Player bob) {
			playerRef = new WeakReference<Player>(bob);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Fisher) {
				Fisher newName = (Fisher) obj;
				return playerRef.get() == newName.playerRef.get();
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
			if (playerRef.get() != null) {
				// check if the player is still standing next to this fish
				// source
				if (nextTo(player, 0.25)) {
					// roll the dice
					if (isSuccessful(player)) {
						Item fish = StendhalRPWorld.get().getRuleManager()
								.getEntityManager().getItem(itemName);
						player.equip(fish, true);
						player.sendPrivateText("You caught a fish.");
					} else {
						player.sendPrivateText("You didn't get a fish.");
					}
				}
			}
		}

	}

	private String itemName;

	/**
	 * Calculates the probability that the given player catches a fish. This is
	 * based on the player's fishing skills, however even players with no skills
	 * at all have a 5 % probability of success.
	 *
	 * @param player
	 * @return
	 */
	private double getSuccessProbability(Player player) {
		String skill = player.getSkill("fishing");
		double probability;

		if (skill != null) {
			probability = Math.max(0.05, Double.parseDouble(skill));
		} else {
			probability = 0.05;
		}

		return probability + player.useKarma(0.05);
	}

	public FishSource(String itemName) {
		this.itemName = itemName;
		setDescription("There is something in the water.");
		setRPClass("fish_source");
		put("type", "fish_source");
	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("fish_source");
		grower.isA("entity");
	}

	/**
	 * Decides randomly if a prospecting action should be successful.
	 *
	 * @return true iff the prospecting player should get a fish.
	 */
	private boolean isSuccessful(Player player) {
		int random = Rand.roll1D100();
		return random <= getSuccessProbability(player) * 100;
	}

	//
	// UseListener
	//

	/**
	 * Is called when a player has started fishing.
	 */
	public boolean onUsed(RPEntity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.nextTo(this)) {

				if (player.isEquipped(NEEDED_EQUIPMENT)) {
					Fisher fisher = new Fisher(player);
					// You can't start a new prospecting action before
					// the last one has finished.
					if (TurnNotifier.get().getRemainingTurns(fisher) == -1) {
						player.faceToward(this);
						player.notifyWorldAboutChanges();

						// some feedback is needed.
						player.sendPrivateText("You have started fishing.");
						TurnNotifier.get().notifyInSeconds(getDuration(),
								fisher);
					}
				} else {
					player
							.sendPrivateText("You need a fishing rod for fishing.");
				}
			}
		}
		return false;
	}

	/**
	 * @return an int between 5 and 8 to represent seconds needed for fishing
	 */
	private int getDuration() {
		return 5 + Rand.rand(4);
	}
}
