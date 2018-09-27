package net.sf.launch4j.config;

/**
 * This class will abstract application from JRE versioning schema and provide
 * comparing capabilities
 * 
 * @author sergeyk
 *
 */
public class JreVersion implements Comparable<JreVersion> {
	private int x1;
	private int x2;
	private int x3;
	private int x4;

	public static JreVersion parseString(String versionStr) {
		JreVersion ret = new JreVersion();
		if (versionStr == null || versionStr.trim().length() == 0) {
			return ret;
		}
		if (!versionStr.matches(Jre.VERSION_PATTERN)) {
			// NOTE: This is actually shouldn't happen because version format had to be
			// checked by Jre#checkInvariants BEFORE calling this method
			throw new IllegalArgumentException("JRE version is not in a right format.");
		}

		String[] parts = versionStr.split("[\\._]");
		int first = Integer.parseInt(parts[0]);
		if (first > 1) {
			// java 9+ version schema
			ret.x1 = 1;
			ret.x2 = first;
			if (parts.length >= 2) {
				ret.x3 = Integer.parseInt(parts[1]);
				if (parts.length >= 3) {
					ret.x4 = Integer.parseInt(parts[2]);
				}
			}
		} else {
			// java <= 1.8 version schema
			ret.x1 = first;
			if (parts.length >= 2) {
				ret.x2 = Integer.parseInt(parts[1]);
				if (parts.length >= 3) {
					ret.x3 = Integer.parseInt(parts[2]);
					if (parts.length == 4) {
						ret.x4 = Integer.parseInt(parts[3]);
					}
				}
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		if (x2 >= 9) {
			return x2 + "." + x3 + "." + x4;
		}

		return x1 + "." + x2 + "." + x3 + (x4 > 0 ? "_" + x4 : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x1;
		result = prime * result + x2;
		result = prime * result + x3;
		result = prime * result + x4;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JreVersion other = (JreVersion) obj;
		if (x1 != other.x1) {
			return false;
		}
		if (x2 != other.x2) {
			return false;
		}
		if (x3 != other.x3) {
			return false;
		}
		if (x4 != other.x4) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(JreVersion o) {
		if (this.equals(o)) {
			return 0;
		}

		if (x1 != o.x1) {
			return x1 - o.x1;
		}
		if (x2 != o.x2) {
			return x2 - o.x2;
		}
		if (x3 != o.x3) {
			return x3 - o.x3;
		}
		if (x4 != o.x4) {
			return x4 - o.x4;
		}

		throw new IllegalStateException("If you see this exception it means JreVersion::equals() method is buggy");
	}
}
