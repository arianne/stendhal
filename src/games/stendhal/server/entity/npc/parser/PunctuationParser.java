package games.stendhal.server.entity.npc.parser;

/**
 * PunctuationParser is used to trim preceding and trailing punctuation
 * characters from a string.
 * 
 */
public final class PunctuationParser {

	private String text;

	private String preceding = "";
	private String trailing = "";

	public PunctuationParser(final String s) {
		if (s != null) {
			parseString(s);
		}
	}

	private void parseString(final String string) {
		text = string;

		extractPreceedingAndTrimText();

		extractTrailingAndTrimText();
	}

	private void extractTrailingAndTrimText() {
		int i = text.length() - 1;
		while (i >= 0 && isPunctuation(text.charAt(i))) {
			i--;
		}

		trailing = text.substring(i + 1);
		text = text.substring(0, i + 1);
	}

	private void extractPreceedingAndTrimText() {
		int i = 0;
		while (i < text.length() && isPunctuation(text.charAt(i))) {
			i++;
		}

		preceding = text.substring(0, i);
		text = text.substring(i, text.length());
	}

	/**
	 * Evaluates if the passed char is one of . , ! or ? .
	 * @param c 
	 * @return
	 */
	private boolean isPunctuation(char c) {
		return (c == '.' || c == ',' || c == '!' || c == '?');
	}

	/**
	 * Return preceding punctuation characters.
	 * 
	 * @return
	 */
	public String getPrecedingPunctuation() {
		return preceding;
	}

	/**
	 * Return trailing punctuation characters.
	 * 
	 * @return
	 */
	public String getTrailingPunctuation() {
		return trailing;
	}

	/**
	 * Return remaining text.
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

}
