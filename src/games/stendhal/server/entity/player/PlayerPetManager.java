package games.stendhal.server.entity.player;

import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.player.Player.NoPetException;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerPetManager {
	private Player player = null;

	PlayerPetManager(Player player) {
		this.player = player;
	}

	void storePet(Pet pet) {

		if (!player.hasSlot("#pets")) {
			player.addSlot(new RPSlot("#pets"));
		}

		RPSlot slot = player.getSlot("#pets");
		slot.clear();
		slot.add(pet);
		player.put("pet", pet.getID().getObjectID());

	}

	public Pet retrievePet() throws NoPetException {

		try {
			if (player.hasSlot("#pets")) {
				RPSlot slot = player.getSlot("#pets");
				if (slot.size() > 0) {
					RPObject object = slot.getFirst();
					slot.remove(object.getID());
					player.removeSlot("#pets");

					Pet pet = null;
					if (object.get("type").equals("cat")) {
						pet = new Cat(object, player);
					// } else if (object.get("type").equals("dog")) {
					//	pet = new Dog(object, player);
					// }
					} else {
						throw new NoPetException("Unknown type " + object.get("type"));
					}
					return pet;
				}
			}

			throw new NoPetException();
		} finally {

		}
	}

}
