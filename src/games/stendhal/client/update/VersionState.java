package games.stendhal.client.update;

/**
 * Possible State of the Client Version
 *
 * @author hendrik
 */
public enum VersionState {

	/** this version is up to date, no update available */
	CURRENT,

	/** there are updates, which should be installed */
	UPDATE_NEEDED,

	/** 
	 * sorry, this version is not supported with the 
	 * update-system anymore. This flag should not be used.
	 * It seems, however, to be a good idea to have the client understand
	 * it in case we mess something up in the future.
	 */
	OUTDATED
}
