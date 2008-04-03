package games.stendhal.server.entity.npc.parser;

/**
 * PunctuationParser is used to trim preceding and trailing punctuation characters from a string.
 *
 * @author Martin Fuchs
 */
public final class PunctuationParser {

    private String text;

    private String preceding = "";
    private String trailing = "";

    public PunctuationParser(final String s) {
        text = s;

        while (text.length() > 0) {
            char c = text.charAt(0);

            if (c == '.' || c == ',' || c == '!' || c == '?') {
                preceding += c;
                text = text.substring(1);
            } else {
                break;
            }
        }

        while (text.length() > 0) {
            char c = text.charAt(text.length() - 1);

            if (c == '.' || c == ',' || c == '!' || c == '?') {
                trailing += c;
                text = text.substring(0, text.length() - 1);
            } else {
                break;
            }
        }
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
