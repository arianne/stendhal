package games.stendhal.server.entity.npc.newparser;

/**
 * A Word is part of a Sentence.
 * It encapsulates the original, white space trimmed text,
 * the word type, a normalised lower case text string
 * and the integer amount.
 *
 * @author Martin Fuchs
 */
public class Word {
	public String	original;
	public WordType	type;
	public String	normalized;
	public Integer	amount;

	public Word(String s) {
		original = s;
	}

	public void parseAmount(String s, ConversationParser parser) {
		try {
			int n = Integer.parseInt(s);

			type = new WordType("NUM");
			amount = n;
			normalized = amount.toString();
		} catch(NumberFormatException e) {
			parser.setError("illegal number format: '" + s + "'");
		}
    }

	@Override
	public String toString() {
		return normalized!=null? normalized: original;
	}
}
