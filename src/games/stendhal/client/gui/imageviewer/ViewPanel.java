package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.styled.StyledPanelUI;
import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * an abstract base class for all ViewPanels
 *
 * @author hendrik
 */
public abstract class ViewPanel extends JPanel {

	private static final long serialVersionUID = 7442185832293104642L;

	/**
	 * creates a new ViewPanel
	 */
	public ViewPanel() {
		setUI(new StyledPanelUI(WoodStyle.getInstance()));
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
