package games.stendhal.server.entity.npc.newparser;

/**
 * As Java is unable to return more than one value from
 * a function, we need this class to return punctuation and
 * the left sentence string.
 * 
 * @author Martin Fuchs
 */
public class PunctuationParser {

	private String	text;
	private char	punctuation;

	public PunctuationParser(String s) {
		text = s;
		punctuation = '\0';

		if (s.length() > 0) {
			char c = s.charAt(s.length()-1);

			if (!Character.isLetterOrDigit(s.charAt(s.length()-1))) {
				punctuation = c;
				text = s.substring(0, s.length()-1);
			}
		}
	}

	public boolean hasPunctuation() {
		return punctuation != '\0';
	}

	public char getPunctuation() {
	    return punctuation;
    }

	public String getText() {
	    return text;
    }
}