package games.stendhal.server.actions.pet;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class ForsakeAction implements ActionListener {
	private static final Logger logger = Logger.getLogger(ForsakeAction.class);
	public static void register() {
		CommandCenter.register("forsake", new ForsakeAction());
	}
	
	public void onAction(final Player player, final RPAction action) {
		
			final String _SPECIES = "species";
			
			if (action.has(_SPECIES)) {
				final String species = action.get(_SPECIES);

				if (species.equals("sheep")) {
					final Sheep sheep = player.getSheep();

					if (sheep != null) {
						player.removeSheep(sheep);

						// HACK: Avoid a problem on database
						if (sheep.has("#db_id")) {
							sheep.remove("#db_id");
						}
					} else {
						logger.error("sheep not found in disown action: " + action.toString());
					}
				} else if (species.equals("pet")) {
					final Pet pet = player.getPet();

					if (pet != null) {
						player.removePet(pet);

						// HACK: Avoid a problem on database
						if (pet.has("#db_id")) {
							pet.remove("#db_id");
						}
					} else {
						logger.error("pet not found in disown action: " + action.toString());
					}
				}
			}
		}

	

}
