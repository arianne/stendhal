package games.stendhal.client.update;

/**
 * Possible States of the Client Version.
 * 
 * @author hendrik
 */
public enum VersionState {

	/** this version is up to date, no update available. */
	CURRENT,

	/** we are unable to get version state. */
	ERROR,

	/**
	 * sorry, this version is not supported with the update-system anymore. This
	 * flag should not be used. It seems, however, to be a good idea to have the
	 * client understand it in case we mess something up in the future.
	 */
	OUTDATED,

	/** the update system does not know about this version. */
	UNKNOWN,

	/** there are updates, which should be installed. */
	UPDATE_NEEDED,

	/** this is only the initial download. */
	INITIAL_DOWNLOAD;

	/**
	 * converts a string into VersionState.
	 * 
	 * @param versionStateString
	 *            a string representation
	 * @return VersionState. In case of an error VersionState.ERROR is returned
	 */
	public static VersionState getFromString(final String versionStateString) {
		try {
			if ((versionStateString == null)
					|| (versionStateString.trim().equals(""))) {
				return UNKNOWN;
			}
			return VersionState.valueOf(versionStateString.toUpperCase());
		} catch (final IllegalArgumentException e) {
			e.printStackTrace(System.err);
			return ERROR;
		}
	}
}
