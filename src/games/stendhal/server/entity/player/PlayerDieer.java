package games.stendhal.server.entity.player;

import games.stendhal.common.Constants;
import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.item.StackableItem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Handles death of players.
 *
 * @author hendrik
 */
public class PlayerDieer {
	/** The name of the zone placed in when killed. */
	public static final String DEFAULT_DEAD_AREA = "int_afterlife";

	private static final Logger logger = Logger.getLogger(PlayerDieer.class);
	private final Player player;

	private List<Item> drops;
	// it is not crazy to have a number of drops as well as a drops list
	// because there will be one entry  in the drops list for each item 
	// but could be more than one stackable item per entry
	// i need the number of drops for saying it/them at the end.
	private int numberOfDrops;
	
	public PlayerDieer(final Player player) {
		this.player = player;
	}


	public void onDead(final Entity killer) {
		player.put("dead", "");
		logger.info("ondeadstart");
		abondonPetsAndSheep();

		stopEating();
		

		if (!(killer instanceof RaidCreature)) {
			logger.info("noraidcreature");
			final List<RingOfLife> ringList = player.getAllEquippedWorkingRingOfLife();
			
			logger.info("ringlist " + ringList);
			double penaltyFactor;
			if (ringList.isEmpty()) {
				penaltyFactor = 0.9;
			} else {
				ringList.get(0).damage();
				penaltyFactor = 0.99;
			}
			
			player.setXP((int) (player.getXP() * penaltyFactor));
			player.setATKXP((int) (player.getATKXP() * penaltyFactor));
			player.setDEFXP((int) (player.getDEFXP() * penaltyFactor));
			if (killer instanceof Player) {
								Player playerKiller = (Player) killer;
								handlePlayerKiller(playerKiller);
			}
			player.update();
		}

		player.onDead(killer, false);
		player.setHP(player.getBaseHP());

		player.returnToOriginalOutfit();

		// After a tangle with the grim reaper, give some karma,
		// but limit abuse
		if (player.getKarma() < 75.0) {
			player.addKarma(100.0);
		}

		respawnInAfterLife();
		if (numberOfDrops > 0) {
			Collection<String> strings = new LinkedList<String>();
			for (Item item : this.drops) {
				if (item instanceof StackableItem) {
					StackableItem si = (StackableItem) item;
					if (si.getQuantity() > 1) {
						StringBuilder sb = new StringBuilder();
						sb.append(si.getQuantity());
						sb.append(" ");
						sb.append(Grammar.plural(si.getName()));
						strings.add(sb.toString());
					}
					if (si.getQuantity() == 1) {
						strings.add(Grammar.a_noun(si.getName()));
					}
				} else {
					strings.add(Grammar.a_noun(item.getName()));
				}
			}
			player.sendPrivateText(NotificationType.NEGATIVE, "Your corpse contains " + Grammar.enumerateCollection(strings) + ", but you may be able to retrieve " + Grammar.itthem(numberOfDrops) + ".");
		} else {
			player.sendPrivateText(NotificationType.POSITIVE, "You were lucky and dropped no items when you died.");
		}
	}

	private void handlePlayerKiller(final Player playerKiller) {
				playerKiller.setLastPlayerKill(System.currentTimeMillis());
	}

	private void respawnInAfterLife() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(DEFAULT_DEAD_AREA);

		if (zone == null) {
			logger.error("Unable to find dead area [" + DEFAULT_DEAD_AREA
					+ "] for player: " + player.getName());
		} else {
			if (!zone.placeObjectAtEntryPoint(player)) {
				logger.error("Unable to place player in zone " + zone + ": "
						+ player.getName());
			}
		}
	}


	private void stopEating() {
		player.itemsToConsume.clear();
		player.poisonToConsume.clear();
	}


	private void abondonPetsAndSheep() {
		final Sheep sheep = player.getSheep();

		if (sheep != null) {
			player.removeSheep(sheep);
		}

		final Pet pet = player.getPet();

		if (pet != null) {
			player.removePet(pet);
		}
	}

	protected void dropItemsOn(final Corpse corpse) {
		// drop at least 1 and at most 4 items
		final int maxItemsToDrop = Rand.rand(4);
		final List<Pair<RPObject, RPSlot>> objects = retrieveAllDroppableObjects();
		drops = new LinkedList<Item>();
		numberOfDrops = 0;
		Collections.shuffle(objects);
		for (int i = 0; i < maxItemsToDrop; i++) {
			if (!objects.isEmpty()) {
				final Pair<RPObject, RPSlot> object = objects.remove(0);
				if (object.first() instanceof StackableItem) {
					final StackableItem item = (StackableItem) object.first();

					// We won't drop the full quantity, but only a
					// percentage.
					// Get a random percentage between 25 % and 75 % to drop
					final double percentage = (Rand.rand(50) + 25) / 100.0;
					final int quantityToDrop = (int) Math.round(item.getQuantity()
							* percentage);

					if (quantityToDrop > 0) {
						final StackableItem itemToDrop = item.splitOff(quantityToDrop);
						ItemLogger.splitOff(player, item, itemToDrop, quantityToDrop);
						ItemLogger.equipAction(player, itemToDrop, 
							new String[]{"slot", player.getName(), object.second().getName()}, 
							new String[]{"slot", player.getName(), "content"});
						corpse.add(itemToDrop);
						numberOfDrops += quantityToDrop;
						drops.add(itemToDrop);
					}
				} else if (object.first() instanceof Item) {
					Item justItem = (Item) object.first();
					object.second().remove(object.first().getID());
					ItemLogger.equipAction(player, (Entity) object.first(), 
									new String[]{"slot", player.getName(), object.second().getName()}, 
									new String[]{"slot", player.getName(), "content"});

					corpse.add((PassiveEntity) object.first());
					numberOfDrops += 1;
					drops.add(justItem);
				}
			}
		}
	}

	/**
	 * 
	 * @return a list of all Items in RPEntity carrying slots that can be dropped
	 */
	private List<Pair<RPObject, RPSlot>> retrieveAllDroppableObjects() {
		final List<Pair<RPObject, RPSlot>> objects = new LinkedList<Pair<RPObject, RPSlot>>();

		for (final String slotName : Constants.CARRYING_SLOTS) {
			if (player.hasSlot(slotName)) {
				final RPSlot slot = player.getSlot(slotName);

				// a list that will contain the objects that could
				// be dropped.
				for (final RPObject objectInSlot : slot) {
					// don't drop special quest rewards as there is no way to
					// get them again
					if (objectInSlot instanceof Item) {
						final Item itemInSlot = (Item) objectInSlot;
						if (itemInSlot.isBound() || itemInSlot.isUndroppableOnDeath()) {
							continue;
						} 
					}
						
					objects.add(new Pair<RPObject, RPSlot>(objectInSlot, slot));
				}
			} else {
				logger.error("CARRYING_SLOTS contains a slot that player "
						+ player.getName() + " doesn't have.");
			}
		}
		return objects;
	}

}
