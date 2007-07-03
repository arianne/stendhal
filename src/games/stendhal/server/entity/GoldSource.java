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
import marauroa.common.game.RPObject;

/**
 * A gold source is a spot where a player can prospect for gold nuggets.
 * He needs a gold pan, time and luck.
 * 
 * Prospecting takes 10 seconds; during this time, the player keep standing
 * next to the gold source. In fact, the player only has to be there
 * when the prospecting action has finished. Therefore, make sure that two
 * gold sources are always at least 5 sec of walking away from each other,
 * so that the player can't prospect for gold at several sites simultaneously.
 * 
 * @author daniel
 *
 */
public class GoldSource extends Entity implements UseListener {
	private static final String NEEDED_EQUIPMENT = "gold_pan";

	private class Prospector implements TurnListener{
		WeakReference<Player> playerRef;

		public Prospector(Player bob) {
			playerRef = new WeakReference<Player>(bob);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Prospector) {
				Prospector new_name = (Prospector)obj ;
				return playerRef.get()==new_name.playerRef.get();
			}else{
				return false;
			}
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		/**
		 * This method is called when the turn number is reached.
		 * NOTE: The <em>message</em> parameter is deprecated.
		 *
		 * @param	currentTurn	The current turn number.
		 * @param	message		The string that was used.
		 */
		public void onTurnReached(int currentTurn, String message) {
			Player player = playerRef.get();
			// check if the player is still logged in
			if (player != null) {
				// check if the player is still standing next to this gold source
				if (nextTo(player,0.25)) {
					// roll the dice
					if (isSuccessful(player)) {
						Item nugget = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
						player.equip(nugget, true);
						player.sendPrivateText("You found a gold nugget.");
					} else {
						player.sendPrivateText("You didn't find anything.");
					}
				}
			}
		}
	}

	private String itemName ="gold_nugget";

	/**
	 * The chance that prospecting is successful.
	 */
	private final static double FINDING_PROBABILITY = 0.1;

	public GoldSource() {
		setDescription("You see something golden glittering.");
		setRPClass("gold_source");
		put("type", "gold_source");
	}

	public GoldSource(RPObject object) {
		super(object);
		setDescription("You see something golden glittering.");
		setRPClass("gold_source");
		put("type", "gold_source");
	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("gold_source");
		grower.isA("entity");
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>false</code>.
	 */
	@Override
	public boolean isObstacle(Entity entity) {
		return false;
	}

	/**
	 * Decides randomly if a prospecting action should be
	 * successful.
	 * @return true iff the prospecting player should get
	 *         a nugget. 
	 */
	private boolean isSuccessful(Player player) {
		int random = Rand.roll1D100();
		return random <= (FINDING_PROBABILITY + player.useKarma(FINDING_PROBABILITY)) * 100;
	}


	//
	// UseListener
	//

	/**
	 * Is called when a player has started prospecting for gold.
	 */
	public void onUsed(RPEntity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.nextTo(this)) {
				if (player.isEquipped(NEEDED_EQUIPMENT)) {
					Prospector prospect = new Prospector(player);
					// You can't start a new prospecting action before
					// the last one has finished.
					if (TurnNotifier.get().getRemainingTurns(prospect) == -1) {
						player.faceToward(this);
						player.notifyWorldAboutChanges();

						// some feedback is needed.
						player.sendPrivateText("You have started to prospect for gold.");
						TurnNotifier.get().notifyInSeconds(getDuration(), prospect);
					}
				} else {
					player.sendPrivateText("You need a gold pan to prospect for gold.");
				}
			}
		}
	}

	/**
	 * @return an int between 7 and 10 to represent seconds needed for prospecting
	 */
	private int getDuration() {
		return 7  + Rand.rand(4);
	}
}
