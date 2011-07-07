package games.stendhal.client.update;

import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 * A fake label component to work around JLabels not being
 * selectable. JOptionPane creates JLabels for strings, but keeps
 * custom components, like this.
 */
class SelectableLabel extends JTextArea {
	private static final long serialVersionUID = -834949100383673798L;

	SelectableLabel(String text) {
		super(text);
		setEditable(false);
		setBorder(null);
		setOpaque(false);
		/*
		 * Get the font directly from JLabel, in case the user is
		 * using a theme where it does not come from an UIManager
		 * property.
		 */
		JLabel tmp = new JLabel();
		setFont(tmp.getFont());
	}
}