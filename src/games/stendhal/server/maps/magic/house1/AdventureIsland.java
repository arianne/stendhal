package games.stendhal.server.maps.magic.house1;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.portal.Teleporter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deathmatch.CreatureSpawner;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import org.apache.log4j.Logger;


public class AdventureIsland extends StendhalRPZone {

 private static final Logger logger = Logger.getLogger(AdventureIsland.class);


/** how many creatures will be spawned.*/
 private static final int NUMBER_OF_CREATURES = 5;
 /** Cost multiplier for getting to island. */
 private static final int COST_FACTOR = 300;
/** island coordinates for placing monsters. */
 private static final int MIN_X = 10;
 /** island coordinates for placing monsters. */
 private static final int MIN_Y = 10;
 /** island coordinates for placing monsters. */
 private static final int MAX_X = 25;
 /** island coordinates for placing monsters. */
 private static final int MAX_Y = 25;
 /** max numbers of fails to place a creature before we just make the island as it is. */
 private static final int ALLOWED_FAILS = 5;
 /** The creatures spawned are between player level * ratio and player level. */
 private static final double LEVEL_RATIO = 0.75;

	public AdventureIsland(final String name, final StendhalRPZone zone,
			final Player player) {
		super(name, zone);

		init(player);

	}

	private void init(final Player player) {
		int cost = COST_FACTOR * player.getLevel();
		Portal portal = new Teleporter(new Spot(player.getZone(), player.getX(), player.getY()));
		portal.setPosition(6, 3);
		add(portal);
		int i = 0;
		int count = 0;
		// max ALLOWED_FAILS fails to place all creatures before we give up
		while (i < NUMBER_OF_CREATURES && count < ALLOWED_FAILS) {
			int level = Rand.randUniform((int) (player.getLevel() * LEVEL_RATIO), player.getLevel()); 
			CreatureSpawner creatureSpawner = new CreatureSpawner();
			Creature creature = new Creature(creatureSpawner.calculateNextCreature(level));
				if (StendhalRPAction.placeat(this, creature, Rand.randUniform(MIN_X, MAX_X), Rand.randUniform(MIN_Y, MAX_Y))) {
					i++;
				} else {
					logger.info(" could not add a creature to adventure island: " + creature);
					count++;	
				}
		}
		String message;
		if (count >= ALLOWED_FAILS) {
			// if we didn't manage to spawn NUMBER_OF_CREATURES they get a reduction
			cost =  (int) (cost * ((float) i / (float) NUMBER_OF_CREATURES));
			message = "Haastaja bellows from below: I could only fit " + i + " creatures on the island for you. You have therefore been charged less, a fee of only " + cost + " money. Good luck.";
			logger.info("Tried too many times to place creatures in adventure island so less than the required number have been spawned");
		} else { 
			message = "Haastaja bellows from below: I took the fee of " + cost + " money. Good luck and remember to be careful with your items, as if you place them on the ground and then leave, they are lost. Most of all, take care with your life.";
		}
		
		disallowIn();
		this.addMovementListener(new ChallengeMovementListener());
	}

		private final class ChallengeMovementListener implements
				MovementListener {
			public Rectangle2D getArea() {
				return new Rectangle2D.Double(0, 0, 100, 100);
			}

			public void onEntered(final ActiveEntity entity, final StendhalRPZone zone, final int newX,
					final int newY) {

			}

			public void onExited(final ActiveEntity entity, final StendhalRPZone zone, final int oldX,
					final int oldY) {
				if (!(entity instanceof Player)) {
					return;
				}
			    if (zone.getPlayers().size() == 1) {
			    	// since we are about to destroy the arena, change the player zoneid to house1 so that 
			    	// if they are relogging, 
			    	// they can enter back to the bank (not the default zone of PlayerRPClass). 
			    	// If they are scrolling out or walking out the portal it works as before.
			    	entity.put("zoneid", "int_magic_house1");
					entity.put("x", "12");
					entity.put("y", "3");
					// iterate through all items left in the zone and for the listeners, stop them listening before we remove the zone
					
					SingletonRepository.getRuleProcessor().removeZone(zone);

			    }
			}

			public void onMoved(final ActiveEntity entity, final StendhalRPZone zone, final int oldX,
					final int oldY, final int newX, final int newY) {

			}
		}

}
