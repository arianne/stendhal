package games.stendhal.client.update;

import javax.swing.JOptionPane;

/**
 * Dialogboxes used during updating
 *
 * @author hendrik
 */
public class UpdateGUI {
	private static final String DIALOG_TITLE = "Stendhal Update";

	/**
	 * Asks the user to accept an update.
	 *
	 * @param updateSize size of the files to download
	 * @return true if the update should be performed, false otherwise
	 */
	public static boolean askForUpdate(int updateSize) {
		int resCode = JOptionPane.showConfirmDialog(null, 
				"There is a new version. " + updateSize + " bytes needs to be downloaded. Should Stendhal be updated?",
				DIALOG_TITLE,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		
		return (resCode == JOptionPane.YES_OPTION);
	}


	/**
	 * Displays a message box
	 *
	 * @param message message to display
	 */
	public static void messageBox(String message) {
		JOptionPane.showMessageDialog(null, message, 
						DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}
}
