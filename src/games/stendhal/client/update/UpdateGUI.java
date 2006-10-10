package games.stendhal.client.update;

import javax.swing.JOptionPane;

/**
 * Dialogboxes used during updating
 *
 * @author hendrik
 */
public class UpdateGUI {

	/**
	 * Asks the user to accept an update.
	 *
	 * @return true if the update should be performed, false otherwise
	 */
	public static boolean askForUpdate() {
		int resCode = JOptionPane.showConfirmDialog(null, 
				"There is a new version. Should Stendhal be updated?",
				"Stendhal Update",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		
		return (resCode == JOptionPane.YES_OPTION);
	}
	
}
