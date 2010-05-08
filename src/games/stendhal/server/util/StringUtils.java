package games.stendhal.server.util;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * little methods to work with strings.
 *
 * @author hendrik
 */
public class StringUtils {
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * Counts the number of upper case characters.
	 *
	 * @param text text to count
	 * @return number of upper case characters
	 */
	public static int countUpperCase(final String text) {
		int count = 0;
		for (final char chr : text.toCharArray()) {
			if (Character.isUpperCase(chr)) {
				count++;
			}
		}
		return count;
	}


	/**
	 * Counts the number of lower case characters.
	 *
	 * @param text text to count
	 * @return number of lower case characters
	 */
	public static int countLowerCase(final String text) {
		int count = 0;
		for (final char chr : text.toCharArray()) {
			if (Character.isLowerCase(chr)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * creates a random string which only consists of letters
	 *
	 * @param count length of the string
	 * @return generated string
	 */
	public static String generateStringOfCharacters(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(CHARACTERS.charAt((int)(Math.random() * CHARACTERS.length())));
		}
		return sb.toString();
	}
	
    /**
     * Replaces [variables] in a string
     *
     * @param string with [variables]
     * @param params replacement parameters
     * @return string with substituted parameters
     */
    public static String subst(String string, Map<String, ?> params)  {
    	if (params == null) {
    		return string;
    	}
        StringBuffer res = new StringBuffer();
        StringTokenizer st = new StringTokenizer(string, "([]'", true);
        String lastToken = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (lastToken.equals("[")) {

                Object temp = params.get(token);
                if (temp != null) {
                    token = temp.toString();
                } else {
                    token = "";
                }

            }
            lastToken = token.trim();
            if (token.equals("[") || token.equals("]")) {
                token = "";
            }
            res.append(token);
        }
        return res.toString();
    }
}
