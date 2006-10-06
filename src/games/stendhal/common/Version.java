/* $Id$
 */
package games.stendhal.common;

/**
 * maintains the current version
 *
 * @author hendrik
 */
public class Version {
	
	public static final String VERSION="0.54.1";

	/**
	 * Extract the specified number of parts from a version-string.
	 *
	 * @param version version-string
	 * @param parts number of parts to extract
	 * @return parts of the version-string
	 */
	public static String cut(String version, int parts) {
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

	/**
	 * compares to versions
	 *
	 * @param v1 1st version string
	 * @param v2 2nd version string
	 * @return see compare
	 */
	public static int compare(String v1, String v2) {
		while (!v1.equals("") || !v2.equals("")) {
			String c1;
			int p1 = v1.indexOf(".");
			if (p1 > -1) {
				c1 = v1.substring(0, p1);
				v1 = v1.substring(p1 + 1);
			} else {
				c1 = v1;
				v1 = "";
			}
			if (c1.equals("")) {
				c1 = "0";
			}
	
			String c2;
			int p2 = v2.indexOf(".");
			if (p2 > -1) {
				c2 = v2.substring(0, p2);
				v2 = v2.substring(p2 + 1);
			} else {
				c2 = v2;
				v2 = "";
			}
			if (c2.equals("")) {
				c2 = "0";
			}
			
			int res = c1.compareTo(c2);
			if (res != 0) {
				return res;
			}
		}
		return 0;
	}

	public static boolean checkCompatibility(String v1, String v2) {
		String ev1 = cut(v1, 2);
		String ev2 = cut(v2, 2);
		boolean res = ev1.equals(ev2);
		return res;
	}

	private Version() {
		// hide constructor; this is a static class
	}

}
