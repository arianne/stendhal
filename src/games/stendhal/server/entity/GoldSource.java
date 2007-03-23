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
public class GoldSource extends Entity implements UseListener, TurnListener {

	/**
	 * The chance that prospecting is successful.
	 */
	private final static double FINDING_PROBABILITY = 0.1;

	/**
	 * How long it takes to prospect for gold (in seconds) 
	 * TODO: randomize this number a bit.
	 */
	private final static int PROSPECTING_DURATION = 10;

	public GoldSource() {
		super();
		setDescription("You see something golden glittering.");
		put("type", "gold_source");
	}

	public GoldSource(RPObject object) {
		super(object);
		setDescription("You see something golden glittering.");
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
		// The player can walk over the PlantGrower.
		return false;
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	/**
	 * Decides randomly if a prospecting action should be
	 * successful.
	 * @return true iff the prospecting player should get
	 *         a nugget. 
	 */
	private boolean prospectSuccessful() {
		int random = Rand.roll1D100();
		return random <= FINDING_PROBABILITY * 100;
	}

	/**
	 * Is called when a player has started prospecting for gold.
	 */
	public void onUsed(RPEntity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.nextTo(this, 0.25)) {

				if (player.isEquipped("gold_pan")) {
					String name = player.getName();
					// You can't start a new prospecting action before
					// the last one has finished.
					if (TurnNotifier.get().getRemainingTurns(this, name) == -1) {
						player.faceTo(this);
						player.notifyWorldAboutChanges();

						// some feedback is needed.
						player.sendPrivateText("You have started to prospect for gold.");
						TurnNotifier.get().notifyInSeconds(PROSPECTING_DURATION, this, name);
					}
				} else {
					player.sendPrivateText("You need a gold pan to prospect for gold.");
				}
			}
		}
	}

	/*
	 * Is called when a player has finished prospecting for gold.
	 */
	public void onTurnReached(int currentTurn, String message) {
		Player player = StendhalRPRuleProcessor.get().getPlayer(message);
		// check if the player is still logged in
		if (player != null) {
			// check if the player is still standing next to this gold source
			if (player.nextTo(this, 0.25)) {
				// roll the dice
				if (prospectSuccessful()) {
					Item nugget = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("gold_nugget");
					player.equip(nugget, true);
					player.sendPrivateText("You found a gold nugget.");
				} else {
					player.sendPrivateText("You didn't find anything.");
				}
			}
		}
	}
}
