package games.stendhal.client.update;

/**
 * Represents and classpath entry.
 *
 * @author hendrik
 */
public class ClasspathEntry implements Comparable<ClasspathEntry> {
	private String filename = null;
	private String type = null;
	private String version = null;

	/**
	 * create a classpath entry
	 *
	 * @param filename
	 */
	public ClasspathEntry(String filename) {
		this.filename = filename;
		String temp = filename;
		if (temp.toLowerCase().endsWith(".jar")) {
			temp = temp.substring(0, temp.length() - 4);
		}
		int pos = temp.lastIndexOf('-');
		if (pos > -1) {
			version = temp.substring(pos);
			temp = temp.substring(0, pos - 1);
		}
		pos = temp.lastIndexOf('-');
		if (pos > -1) {
			type = temp.substring(0, pos - 1);
		}
	}

	public int compareTo(ClasspathEntry o) {
		// TODO: implement me
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
