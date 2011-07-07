package games.stendhal.client.update;

import java.text.NumberFormat;

import javax.swing.JOptionPane;

/**
 * Dialogboxes used during updating..
 * 
 * @author hendrik
 */
public class UpdateGUIDialogs {

	private static final String DIALOG_TITLE = "Stendhal Update";

	/**
	 * Asks the user to accept an update.
	 * 
	 * @param updateSize
	 *            size of the files to download
	 * @param update
	 *            true, if it is an update, false on first install
	 * @return true if the update should be performed, false otherwise
	 */
	public static boolean askForDownload(final int updateSize, final boolean update) {
		// format number, only provide decimal digits on very small sizes
		float size = (float) updateSize / 1024;
		if (size > 10) {
			size = (int) size;
		}
		final String sizeString = NumberFormat.getInstance().format(size);

		// ask user
		int resCode;
		if (update) {
			resCode = JOptionPane.showConfirmDialog(null,
					new SelectableLabel("There is a new version which is " + sizeString
							+ " KB. Should Stendhal be updated?"), DIALOG_TITLE,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		} else {
			resCode = JOptionPane.showConfirmDialog(null,
					new SelectableLabel("We need to download some additional files which are "
							+ sizeString
							+ " KB.\r\n Should Stendhal be installed?"),
					DIALOG_TITLE, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
		}

		return (resCode == JOptionPane.YES_OPTION);
	}

	/**
	 * Displays a message box.
	 * 
	 * @param message
	 *            message to display
	 */
	public static void messageBox(final String message) {
		JOptionPane.showMessageDialog(null, new SelectableLabel(message), DIALOG_TITLE,
				JOptionPane.INFORMATION_MESSAGE);
	}

}
