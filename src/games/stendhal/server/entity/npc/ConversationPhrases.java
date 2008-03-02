package games.stendhal.server.entity.npc;

import java.util.Arrays;
import java.util.List;

/**
 * Common phrases used by players to interact with a SpeakerNPC.
 * 
 * @author hendrik
 */
public class ConversationPhrases {

	public static final String NO_EXPRESSION = "|EXACT|NOCASE|no";

	// do not use a mutable list here
	@SuppressWarnings("unchecked")
	public static final List<String> EMPTY = Arrays.asList();

	public static final List<String> GREETING_MESSAGES = Arrays.asList("hi",
			"hello", "hallo", "greetings", "hola");

	public static final List<String> JOB_MESSAGES = Arrays.asList("job", "work");

	public static final List<String> HELP_MESSAGES = Arrays.asList("help",
			"ayuda");

	public static final List<String> QUEST_MESSAGES = Arrays.asList("task",
			"quest", "favor", "favour");

	public static final List<String> OFFER_MESSAGES = Arrays.asList("offer",
			"deal", "trade");

	public static final List<String> YES_MESSAGES = Arrays.asList("yes", "ok");

	public static final List<String> NO_MESSAGES = Arrays.asList(NO_EXPRESSION, "nothing");

	public static final List<String> GOODBYE_MESSAGES = Arrays.asList("bye",
			"farewell", "cya", "adios");

}
