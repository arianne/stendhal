/* $Id$
 */
package games.stendhal.common;

/**
 * maintains the current version
 *
 * @author hendrik
 */
public class Version {
	
	public static final String VERSION = "0.52";

	/**
	 * Extract the specified number of parts from a version-string.
	 *
	 * @param version version-string
	 * @param parts number of parts to extract
	 * @return parts of the version-string
	 */
	public static String extractVersion(String version, int parts) {
		int pos = 0;
		for (int i = 0; i < parts; i++) {
			int temp = version.indexOf(".", pos + 1);
			if (temp < 0) {
				pos = version.length();
				break;
			}
			pos = temp;
		}
		return version.substring(0, pos);
	}
	
	public static boolean areVersionsCompatible(String v1, String v2) {
		return true;
	}
	
	private Version() {
		// hide constructor; this is a static class
	}

}
