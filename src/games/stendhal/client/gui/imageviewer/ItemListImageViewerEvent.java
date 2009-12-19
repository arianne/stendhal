package games.stendhal.client.gui.imageviewer;

import java.awt.Dimension;

import javax.swing.JLabel;

import marauroa.common.game.RPEvent;

/**
 * Opens a styled internal frame displaying an item list
 * 
 * @author hendrik
 */
public class ItemListImageViewerEvent extends ViewPanel {
	private static final long serialVersionUID = -6114543463410539585L;

	private RPEvent event;

	/**
	 * creates a new ItemListImageViewerEvent
	 *
	 * @param event event
	 */
	public ItemListImageViewerEvent(RPEvent event) {
		this.event = event;
	}

	/**
	 * shows the window
	 */
	public void view() {
		new ImageViewWindow(event.get("title"), this);
	}


	@Override
	public void prepareView(final Dimension maxSize) {
		// only display when not null
		StringBuilder html = new StringBuilder();
		html.append("<html><table border=\"1\">");
		html.append("<tr><th>Item</th><th>Price</th><th>Description</th></tr>");
		html.append("<tr><td>Club</td><th>1234</td><td>You see bla</td></tr>");
		
		html.append("</table></html>");

		final JLabel imageLabel = new JLabel(html.toString());

		add(imageLabel);

		setVisible(true);
	}
}
