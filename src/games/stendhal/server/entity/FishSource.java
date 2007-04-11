package games.stendhal.server.entity;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPClass;

/**
 * A fish source is a spot where a player can fish.
 * He needs a fishing rod, time and luck.
 * Before he catches fish he needs to make a license.
 * 
 * Fishing takes 15 seconds; during this time, the player keep standing
 * next to the fish source. In fact, the player only has to be there
 * when the prospecting action has finished. Therefore, make sure that two
 * fish sources are always at least 8 sec of walking away from each other,
 * so that the player can't fish at several sites simultaneously.
 * 
 * @author dine
 *
 */
public class FishSource extends Entity implements UseListener, TurnListener {

	private String itemName;
	
	/**
	 * Calculates the probability that the given player catches a fish.
	 * This is based on the player's fishing skills, however even
	 * players with no skills at all have a 5 % probability of success.
	 * @param player
	 * @return
	 */
	private double getSuccessProbability(Player player) {
		String skill = player.getSkill("fishing");
		if (skill != null) {
			return Math.max(0.05, Double.parseDouble(skill));
		} else {
			return 0.05;
		}
	}

	/**fishing
	 * How long it takes to fish (in seconds) 
	 * TODO: randomize this number a bit.
	 */
	private final static int PROSPECTING_DURATION = 8;

	public FishSource(String itemName) {
		super();
		this.itemName = itemName;
		setDescription("There is something in the water.");
		put("type", "fish_source");
	}

//	public FishSource(RPObject object) {
//		super(object);
//		setDescription("You see something in the water.");
//		put("type", "fish_source");
//	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("fish_source");
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
		// The player cannot walk over the PlantGrower.
		return true;
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	/**
	 * Decides randomly if a prospecting action should be
	 * successful.
	 * @return true iff the prospecting player should get
	 *         a fish. 
	 */
	private boolean fishingSuccessful(Player player) {
		int random = Rand.roll1D100();
		return random <= getSuccessProbability(player) * 100;
	}

	/**
	 * Is called when a player has started fishing.
	 */
	public void onUsed(RPEntity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.nextTo(this)) {

				if (player.isEquipped("fishing_rod")) {
					String name = player.getName();
					// You can't start a new prospecting action before
					// the last one has finished.
					if (TurnNotifier.get().getRemainingTurns(this, name) == -1) {
						player.faceTo(this);
						player.notifyWorldAboutChanges();

						// some feedback is needed.
						player.sendPrivateText("You have started fishing.");
						TurnNotifier.get().notifyInSeconds(PROSPECTING_DURATION, this, name);
					}
				} else {
					player.sendPrivateText("You need a fishing rod for fishing.");
				}
			}
		}
	}

	/*
	 * Is called when a player has finished.
	 */
	public void onTurnReached(int currentTurn, String message) {
		Player player = StendhalRPRuleProcessor.get().getPlayer(message);
		// check if the player is still logged in
		if (player != null) {
			// check if the player is still standing next to this fish source
			if (player.nextTo(this)) {
				// roll the dice
				if (fishingSuccessful(player)) {
					Item fish = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
					player.equip(fish, true);
					player.sendPrivateText("You caught a fish.");
				} else {
					player.sendPrivateText("You didn't get a fish.");
				}
			}
		}
	}
}
