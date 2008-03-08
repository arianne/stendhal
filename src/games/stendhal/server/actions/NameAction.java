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

		if (newName.length() == 0) {
			player.sendPrivateText("Please issue the old and the new name.");
			return;
		}

		List<DomesticAnimal> animals = player.getAnimals();

		if (animals.isEmpty()) {
    		player.sendPrivateText("You don't own any " + curName);
		} else {
			boolean found = false;

			do {
    			for(DomesticAnimal animal : animals) {
    				if (animal != null) {
            			if (animal.getTitle().equalsIgnoreCase(curName)) {
            				String oldName = animal.getTitle();

            				// remove quotes, if present
            				if (newName.charAt(0)=='\'' && newName.charAt(newName.length()-1)=='\'') {
            					newName = newName.substring(1, newName.length()-1);
            				}

            				animal.setTitle(newName);

                			if (oldName != null) {
                				player.sendPrivateText("You changed the name of '" + oldName + "' to '" + newName + "'");
                			} else {
                				player.sendPrivateText("Congratulations, your " + curName + " is now called '" + newName + "'.");
                			}

                			found = true;
                			break;
            		    }
    				}
    			}

    			// see if we can move the word separator one space to the right to search for a pet name
    			if (!found) {
    				int idx = newName.indexOf(' ');
    				if (idx == -1) {
    					break;
    				}

    				curName += " " + newName.substring(0, idx);
    				newName = newName.substring(idx + 1);
    			}
			} while (!found);

			if (!found) {
				player.sendPrivateText("You don't own a pet called '" + curName + "'");
		    }
        }
	}

}
