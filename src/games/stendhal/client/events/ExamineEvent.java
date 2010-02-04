package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.imageviewer.RPEventImageViewer;


/**
 * Shows an image like a map of semos
 *
 * @author hendrik
 */
public class ExamineEvent extends Event<Entity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		RPEventImageViewer.viewImage(event);
	}

}
