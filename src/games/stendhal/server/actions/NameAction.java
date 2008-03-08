package games.stendhal.server.actions;

import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * @author Martin Fuchs
 */
public class NameAction implements ActionListener {

	/**
	 * Registers the "name" action handler.
	 */
	public static void register() {
		NameAction name = new NameAction();
		CommandCenter.register("name", name);
	}

	public void onAction(Player player, RPAction action) {
		String curName = action.get("target");
		String newName = action.get("args");

		DomesticAnimal animal;

		if (player.hasPet()) {
			animal = player.getPet();
		} else if (player.hasSheep()) {
			animal = player.getSheep();
		} else {
			animal = null;
		}

		if (animal != null) {
			if (animal.getTitle().equals(curName)) {
				String oldName = animal.getName();

				animal.setName(newName);

    			if (oldName != null) {
    				player.sendPrivateText("You changed the name of " + oldName + " to " + newName);
    			} else {
    				player.sendPrivateText("Congratulations, your " + curName + " is now called '" + newName + "'.");
    			}
		    } else {
				player.sendPrivateText("You don't own a pet called " + curName);
		    }
	    } else {
			player.sendPrivateText("You don't own any " + curName);
	    }
    }

}
