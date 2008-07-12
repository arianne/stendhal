package games.stendhal.server.events;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * An examine event that opens an image viewer with
 * the specified image in the client.
 *
 * @author timothyb89 / hendrik
 */
public class ExamineEvent extends RPEvent {
	private static final String RPCLASS_NAME = "examine";
	private static final String PATH = "path";
	private static final String ALT = "alt";
	private static final String TITLE = "title";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, PATH, Type.STRING, Definition.PRIVATE);
		rpclass.add(DefinitionClass.ATTRIBUTE, ALT, Type.STRING, Definition.PRIVATE);
		rpclass.add(DefinitionClass.ATTRIBUTE, TITLE, Type.STRING, Definition.PRIVATE);
	}

	/**
	 * Creates a new ExamineEvent.
	 *
	 * @param image image file
	 * @param title title of image viewer
	 * @param alt alternative text
	 */
	public ExamineEvent(final String image, final String title, final String alt) {
		super(RPCLASS_NAME);
		super.put(PATH, "/data/sprites/examine/" + image);
		super.put(TITLE, title);
		super.put(ALT, alt);
	}
}
