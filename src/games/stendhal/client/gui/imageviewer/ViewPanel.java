package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPanel;

import java.awt.Dimension;

/**
 * an abstract base class for all ViewPanels
 *
 * @author hendrik
 */
public abstract class ViewPanel extends StyledJPanel {

	private static final long serialVersionUID = 7442185832293104642L;

	/**
	 * creates a new ViewPanel
	 */
	public ViewPanel() {
		super(WoodStyle.getInstance());
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
