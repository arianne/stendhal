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
		super("spells", 3, 1);
		//panel window, no closing allowed
		setCloseable(false);
	}
	
	private void disableSpells() {
		/*
		 * disabling spells should not happen unless we
		 * decide to implement some harm that could let
		 * a player lose his magical abilities
		 * (i.e. proper equipment is not worn?)
		 */
	}

	public void featureDisabled(String name) {
		disableSpells();
	}

	public void featureEnabled(String name, String value) {
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
