package games.stendhal.server.entity.player;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Handles dieing of players.
 *
 * @author hendrik
 */
public class PlayerDieer {
	/** The name of the zone placed in when killed. */
	public static final String DEFAULT_DEAD_AREA = "int_afterlife";

	private static Logger logger = Logger.getLogger(PlayerDieer.class);
	private Player player;
	
	public PlayerDieer(Player player) {
		this.player = player;
	}


	public void onDead(Entity killer) {
		player.put("dead", "");

		// Abandon dependants
		Sheep sheep = player.getSheep();

		if (sheep != null) {
			player.removeSheep(sheep);
		}

		Pet pet = player.getPet();

		if (pet != null) {
			player.removePet(pet);
		}

		// We stop eating anything
		player.itemsToConsume.clear();
		player.poisonToConsume.clear();

		if (!(killer instanceof RaidCreature)) {

			List<Item> ringList = player.getAllEquipped("emerald_ring");
			boolean eRingUsed = false;

			for (Item emeraldRing : ringList) {
				int amount = emeraldRing.getInt("amount");
				if (amount > 0) {
					// We broke the emerald ring.
					emeraldRing.put("amount", amount - 1);
					eRingUsed = true;
					break;
				}
			}

			if (eRingUsed) {
				// Penalize: 1% less experience if wearing that ring
				player.setXP((int) (player.getXP() * 0.99));
				player.setATKXP((int) (player.getATKXP() * 0.99));
				player.setDEFXP((int) (player.getDEFXP() * 0.99));
			} else {
				// Penalize: 10% less experience
				player.setXP((int) (player.getXP() * 0.9));
				player.setATKXP((int) (player.getATKXP() * 0.9));
				player.setDEFXP((int) (player.getDEFXP() * 0.9));
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

		// Penalize: Respawn on afterlive zone and
		StendhalRPZone zone = StendhalRPWorld.get().getZone(DEFAULT_DEAD_AREA);

		if (zone != null) {
			if (!zone.placeObjectAtEntryPoint(player)) {
				logger.error("Unable to place player in zone " + zone + ": "
						+ player.getName());
			}
		} else {
			logger.error("Unable to find dead area [" + DEFAULT_DEAD_AREA
					+ "] for player: " + player.getName());
		}
	}

	protected void dropItemsOn(Corpse corpse) {
		// drop at least 1 and at most 4 items
		int maxItemsToDrop = Rand.rand(4);
		List<Pair<RPObject, RPSlot>> objects = new LinkedList<Pair<RPObject, RPSlot>>();

		for (String slotName : player.CARRYING_SLOTS) {
			if (!player.hasSlot(slotName)) {
				logger.error("CARRYING_SLOTS contains a slot that player "
						+ player.getName() + " doesn't have.");
			} else {
				RPSlot slot = player.getSlot(slotName);

				// a list that will contain the objects that will
				// be dropped.

				// get a random set of items to drop
				for (RPObject objectInSlot : slot) {
					// don't drop special quest rewards as there is no way to
					// get them again
					// TODO: Assert these as Item's and use getBoundTo() and
					// isUndroppableOnDeath()
					if (objectInSlot.has("bound")
							|| objectInSlot.has("undroppableondeath")) {
						continue;
					}
					objects.add(new Pair<RPObject, RPSlot>(objectInSlot, slot));
				}
			}
		}
		Collections.shuffle(objects);

		for (int i = 0; i < maxItemsToDrop; i++) {
			if (!objects.isEmpty()) {
				Pair<RPObject, RPSlot> object = objects.remove(0);
				if (object.first() instanceof StackableItem) {
					StackableItem item = (StackableItem) object.first();

					// We won't drop the full quantity, but only a
					// percentage.
					// Get a random percentage between 26 % and 75 % to drop
					double percentage = (Rand.rand(50) + 25) / 100.0;
					int quantityToDrop = (int) Math.round(item.getQuantity()
							* percentage);

					if (quantityToDrop > 0) {
						StackableItem itemToDrop = item.splitOff(quantityToDrop);
						corpse.add(itemToDrop);
					}
				} else if (object.first() instanceof PassiveEntity) {
					object.second().remove(object.first().getID());

					corpse.add((PassiveEntity) object.first());
				}
			}
		}
	}

}
