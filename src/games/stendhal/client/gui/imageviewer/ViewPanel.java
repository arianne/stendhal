package games.stendhal.client.gui.imageviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * an abstract base class for all ViewPanels
 *
 * @author hendrik
 */
public abstract class ViewPanel extends JComponent {

	private static final long serialVersionUID = 7442185832293104642L;

	/**
	 * creates a new ViewPanel
	 */
	public ViewPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
	}

	/**
	 * prepares the view
	 *
	 * @param maxSize of the panel
	 */
	public void prepareView(Dimension maxSize) {
		// do nothing
	}
}
