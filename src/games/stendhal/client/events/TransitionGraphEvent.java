package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.admin.TransitionDiagram;


/**
 * shows the transition diagram of a finite state machine.
 *
 * @author hendrik
 */
public class TransitionGraphEvent extends Event<Entity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		new TransitionDiagram().showTransitionDiagram(event.get("data"));
	}

}
