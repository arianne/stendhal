package games.stendhal.server.events;

import games.stendhal.common.NotificationType;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * A text message
 *
 * @author hendrik
 */
public class PrivateTextEvent extends RPEvent {
	private static final String RPCLASS_NAME = "private_text";
	private static final String TEXT_TYPE = "texttype";
	private static final String CHANNEL = "channel";
	private static final String TEXT = "text";

	/**
	 * Creates the rpclass
	 */
	public static void generateRPClass() {
		RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, TEXT_TYPE, Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, CHANNEL, Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
	}

	/**
	 * Creates a new text event
	 *
	 * @param type NotificationType
	 * @param text Text
	 */
	public PrivateTextEvent(NotificationType type, String text) {
		super(RPCLASS_NAME);		
		put(TEXT_TYPE, type.name());
		put(TEXT, text);
	}
}
