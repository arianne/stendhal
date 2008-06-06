package games.stendhal.server.actions;

import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.player.Player;

import java.util.List;

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

	/**
	 * Handle the /name action.
	 */
	public void onAction(Player player, RPAction action) {
		String curName = action.get("target");
		String newName = action.get("args");

		if (newName == null || newName.length() == 0) {
			player.sendPrivateText("Please issue the old and the new name.");
			return;
		}

		List<DomesticAnimal> animals = player.getAnimals();

		if (animals.isEmpty()) {
    		player.sendPrivateText("You don't own any " + curName);
		} else {
			DomesticAnimal animal = null;

			do {
				animal = player.searchAnimal(curName, false);

    			if (animal != null) {
        			if (animal.getTitle().equalsIgnoreCase(curName)) {
        				// remove quotes, if present
        				if (newName.charAt(0) == '\'' && newName.charAt(newName.length() - 1) == '\'') {
        					newName = newName.substring(1, newName.length() - 1);
        				}

        				newName = newName.trim();

        				if (player.searchAnimal(newName, false) != null) {
        					player.sendPrivateText("You own already a pet named '" + newName + "'");
        				} else if (newName.length() > 0) {
            				String oldName = animal.getTitle();

        					animal.setTitle(newName);

                			if (oldName != null) {
                				player.sendPrivateText("You changed the name of '" + oldName + "' to '" + newName + "'");
                			} else {
                				player.sendPrivateText("Congratulations, your " + curName + " is now called '" + newName + "'.");
                			}
        				} else {
        					player.sendPrivateText("Please don't use empty names.");
        				}
        			}
    			} else {
        			// see if we can move the word separator one space to the right to search for a pet name
    				int idxSpace = newName.indexOf(' ');

    				if (idxSpace != -1) {
    					int idxLastSpace = newName.lastIndexOf(' ', idxSpace);
    					curName += " " + newName.substring(0, idxSpace);
    					newName = newName.substring(idxLastSpace + 1);
    				} else {
    					// There is no more other command interpretation.
    					break;
    				}
    			}
			} while (animal == null);

			if (animal == null) {
				player.sendPrivateText("You don't own a pet called '" + curName + "'");
		    }
        }
	}

}
