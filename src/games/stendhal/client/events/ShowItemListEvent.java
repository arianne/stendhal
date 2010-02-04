package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.imageviewer.ItemListImageViewerEvent;


/**
 * shows an item list
 *
 * @author hendrik
 */
public class ShowItemListEvent extends Event<Entity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		new ItemListImageViewerEvent(event).view();
	}

}
