package games.stendhal.server.events;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * A public text message.
 *
 * @author hendrik
 */
public class TextEvent extends RPEvent {
	private static final String RPCLASS_NAME = "text";
	private static final String TEXT = "text";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
	}

	/**
	 * Creates a new text event.
	 *
	 * @param text Text
	 */
	public TextEvent(final String text) {
		super(RPCLASS_NAME);		
		put(TEXT, text);
	}
}
