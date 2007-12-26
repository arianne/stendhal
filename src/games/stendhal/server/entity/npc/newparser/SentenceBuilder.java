package games.stendhal.server.entity.npc.newparser;

/**
 * This utility class is used to create string representations
 * of sentences by separating words by space characters.
 * 
 * @author Martin Fuchs
 */
public class SentenceBuilder {
	private StringBuilder builder = new StringBuilder();
	private boolean first = true;
	private char space = ' ';

	public SentenceBuilder() {
		space = ' ';
	}

	public SentenceBuilder(char term) {
		space = term;
	}

	public void append(final String s) {
		if (first) {
			first = false;
		} else {
			builder.append(space);
		}

		builder.append(s);
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}