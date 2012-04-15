package games.stendhal.client.gui.spells;

import games.stendhal.client.gui.SlotWindow;
import games.stendhal.client.listener.FeatureChangeListener;

import javax.swing.SwingUtilities;
/**
 * Container displaying the spells of the player.
 * 
 * @author madmetzger
 *
 */
public class Spells extends SlotWindow implements FeatureChangeListener {

	private static final long serialVersionUID = 79889495195014549L;

	public Spells() {
		super("spells", 3, 2);
		//panel window, no closing allowed
		setCloseable(false);
	}

	public void featureDisabled(final String name) {
		if (name.equals("spells")) {
			if(isVisible()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setVisible(false);
					}
				});
			}
		}
	}

	public void featureEnabled(final String name, final String value) {
		if (name.equals("spells")) {
			if(!isVisible()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setVisible(true);
					}
				});
			}
		}
	}

}
