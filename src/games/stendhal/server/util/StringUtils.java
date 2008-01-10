package games.stendhal.server.util;

/**
 * little methods to work with strings.
 *
 * @author hendrik
 */
public class StringUtils {

	/**
	 * counts the number of upper case characters
	 *
	 * @param text text to count
	 * @return number of upper case characters
	 */
	public int countUpperCase(String text) {
		int count = 0;
		for (char chr : text.toCharArray()) {
			if (Character.isUpperCase(chr)) {
				count++;
			}
		}
		return count;
	}
}
