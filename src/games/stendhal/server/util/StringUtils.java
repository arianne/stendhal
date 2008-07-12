package games.stendhal.server.util;

/**
 * little methods to work with strings.
 *
 * @author hendrik
 */
public class StringUtils {

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
}
