package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Handles ownership of pets and sheep.
 *
 * @author hendrik
 */
class PetOwner {
	private static Logger logger = Logger.getLogger(PetOwner.class);

	/**
	 * The pet ID attribute name.
	 */
	protected static final String ATTR_PET = "pet";

	/**
	 * The sheep ID attribute name.
	 */
	protected static final String ATTR_SHEEP = "sheep";
	
	private Player player;

	private PlayerSheepManager playerSheepManager;
	private PlayerPetManager playerPetManager;


	public PetOwner(Player player) {
		this.player = player;
		playerSheepManager = new PlayerSheepManager(player);
		playerPetManager = new PlayerPetManager(player);
	}
	
	public void removeSheep(Sheep sheep) {
		if (sheep != null) {
			sheep.setOwner(null);
		}

		if (player.has(ATTR_SHEEP)) {
			player.remove(ATTR_SHEEP);
		} else {
			logger.warn("Called removeSheep but player has not sheep: " + this);
		}
	}

	public void removePet(Pet pet) {
		if (pet != null) {
			pet.setOwner(null);
		}

		if (player.has(ATTR_PET)) {
			player.remove(ATTR_PET);
		} else {
			logger.warn("Called removePet but player has not pet: " + this);
		}
	}

	public boolean hasSheep() {
		return player.has(ATTR_SHEEP);
	}

	public boolean hasPet() {
		return player.has(ATTR_PET);
	}

	/**
	 * Set the player's pet. This will also set the pet's owner.
	 * 
	 * @param pet
	 *            The pet.
	 */
	public void setPet(Pet pet) {
		player.put(ATTR_PET, pet.getID().getObjectID());
		pet.setOwner(player);
	}

	/**
	 * Set the player's sheep. This will also set the sheep's owner.
	 * 
	 * @param sheep
	 *            The sheep.
	 */
	public void setSheep(Sheep sheep) {
		player.put(ATTR_SHEEP, sheep.getID().getObjectID());
		sheep.setOwner(player);
	}

	/**
	 * Get the player's sheep.
	 * 
	 * @return The sheep.
	 */
	public Sheep getSheep() {
		if (player.has(ATTR_SHEEP)) {
			try {
				return (Sheep) SingletonRepository.getRPWorld().get(
						new RPObject.ID(player.getInt(ATTR_SHEEP), player.get("zoneid")));
			} catch (Exception e) {
				logger.error("Pre 1.00 Marauroa sheep bug. (player = "
						+ player.getName() + ")", e);

				if (player.has(ATTR_SHEEP)) {
					player.remove(ATTR_SHEEP);
				}

				if (player.hasSlot("#flock")) {
					player.removeSlot("#flock");
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public Pet getPet() {
		try {
			if (player.has(ATTR_PET)) {
				return (Pet) SingletonRepository.getRPWorld().get(
						new RPObject.ID(player.getInt(ATTR_PET), player.get("zoneid")));
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			player.remove(ATTR_PET);
			logger.error("removed pets attribute" + e);
			return null;
		}
	}

	public void destroy() {
		Sheep sheep = player.getSheep();

		if (sheep != null) {
			sheep.getZone().remove(sheep);

			/*
			 * NOTE: Once the sheep is stored there is no more trace of zoneid.
			 */
			playerSheepManager.storeSheep(sheep);
		} else {
			// Bug on pre 0.20 released
			if (player.hasSlot("#flock")) {
				player.removeSlot("#flock");
			}
		}

		Pet pet = player.getPet();

		if (pet != null) {
			pet.getZone().remove(pet);

			/*
			 * NOTE: Once the pet is stored there is no more trace of zoneid.
			 */
			playerPetManager.storePet(pet);
		}
	}

	public Pet retrievePet() {
		return playerPetManager.retrievePet();
	}

	public Sheep retrieveSheep() {
		return playerSheepManager.retrieveSheep();
	}
}
