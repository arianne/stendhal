package games.stendhal.client.update;

/**
 * Represents and classpath entry.
 * 
 * @author hendrik
 */
public class ClasspathEntry implements Comparable<ClasspathEntry> {

	private String filename;

	private String type;

	private String version;

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
			version = temp.substring(pos + 1);
			temp = temp.substring(0, pos);
		}
		pos = Math.max(temp.lastIndexOf('/'), temp.lastIndexOf('\\'));
		if (pos > -1) {
			type = temp.substring(pos + 1);
		} else {
			type = temp;
		}
	}

	/**
	 * Returns the filename
	 * 
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * returns the type of this jar
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * returns the version number of this entry
	 * 
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	public int compareTo(ClasspathEntry other) {

		// 1. step: put entries without version to the end
		if (this.version == null) {
			if (other.version == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (other.version == null) {
			return -1;
		}

		// 2. compare versions
		int versionDiff = Version.compare(this.version, other.version);
		if (versionDiff != 0) {
			return versionDiff;
		}

		// 3. TODO: handle ...-diff-... files
		return 0;
	}

}
