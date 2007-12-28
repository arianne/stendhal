package games.stendhal.server.entity.npc.parser;

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

	/**
	 * append string separated by space
	 * @param s
	 */
	public void append(final String s) {
		if (first) {
			first = false;
		} else {
			builder.append(space);
		}

		builder.append(s);
	}

	/**
	 * directly append the given character
	 * @param c
	 */
	public void append(char c) {
	    builder.append(c);	    
    }

	@Override
	public String toString() {
		return builder.toString();
	}
}