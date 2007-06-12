package games.stendhal.server.entity;

import java.lang.ref.WeakReference;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A well source is a spot where a player can make a wish to gain an item.
 * He needs time and luck.
 * 
 * Wishing takes 10 seconds; during this time, the player keep standing
 * next to the well source. In fact, the player only has to be there
 * when the wishing action has finished. Therefore, make sure that two
 * well sources are always at least 5 sec of walking away from each other,
 * so that the player can't wish at several sites simultaneously.
 * 
 * @author daniel
 *
 */
public class WellSource extends Entity implements UseListener {

	private class Wisher implements TurnListener{
		WeakReference<Player> playerRef;
		public Wisher(Player bob) {
			playerRef = new WeakReference<Player>(bob);
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Wisher) {
				Wisher new_name = (Wisher)obj ;
				return playerRef.get()==new_name.playerRef.get();
			}else{
				return false;
			}
			
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		
		public void onTurnReached(int currentTurn, String message) {
			Player player = playerRef.get();
			// check if the player is still logged in
			if (player != null) {
				// check if the player is still standing next to this well source
				if (player.nextTo(getX(),getY(),0.25)) {
					// roll the dice
					if (isSuccessful(player)) {
					        String itemName = items[Rand.rand(items.length)];
						Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
						player.equip(item, true);
						player.sendPrivateText("You were lucky and found " + Grammar.a_noun(itemName));
					} else {
						player.sendPrivateText("Your wish didn't come true.");
					}
				}
			}
		}
		
	}
        private  String[] items = {"money", "iron_ore", "gold_nugget", "home_scroll", "greater_potion", "sapphire", "carbuncle", "horned_golden_helmet", "dark_dagger", "present"};

	/**
	 * The chance that wishing is successful.
	 */
	private final static double FINDING_PROBABILITY = 0.05;

	/**
	 * How long it takes to wish at a well (in seconds) 
	 * TODO: randomize this number a bit.
	 */
	private final static int DURATION = 10;

	public WellSource() {
		super();
		setDescription("You see a wishing well. Something in it catches your eye.");
		put("type", "well_source");
	}

	public WellSource(RPObject object) {
		super(object);
		setDescription("You see a wishing well. Something in it catches your eye.");
		put("type", "well_source");
	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("well_source");
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
	 * Decides randomly if a wishing action should be
	 * successful.
	 * @return true iff the wishing player should get
	 *         a prize. 
	 */
	private boolean isSuccessful(Player player) {
		int random = Rand.roll1D100();
		return random <= (FINDING_PROBABILITY + player.useKarma(FINDING_PROBABILITY)) * 100;
	}

	/**
	 * Is called when a player has started wishing.
	 */
	public void onUsed(RPEntity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.nextTo(this)) {
				Wisher wish = new Wisher(player);
				// You can't start a new wishing action before
				// the last one has finished.
				if (TurnNotifier.get().getRemainingTurns(wish) == -1) {
					player.faceToward(this);
				        player.notifyWorldAboutChanges();

					// some feedback is needed.
					player.sendPrivateText("You make a wish.");
				       	TurnNotifier.get().notifyInSeconds(DURATION, wish);
				}
			}
		}
	}

}
