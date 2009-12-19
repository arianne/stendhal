package games.stendhal.server.events;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.Type;

/**
 * A graphvis diagram showing the FSM of an NPC
 *
 * @author hendrik
 */
public class TransitionGraphEvent extends RPEvent {
	
	private static final String TRANSITION_GRAPH = "transition_graph";
	private static final String DATA = "data";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(TRANSITION_GRAPH);
		rpclass.addAttribute(DATA, Type.LONG_STRING);
	}

	/**
	 * Creates a new TransitionGraphEvent.
	 *
	 * @param data data to display
	 */
	public TransitionGraphEvent(String data) {
		super(TRANSITION_GRAPH);		
		put(DATA, data);
	}
}
