/*
 * @(#) src/games/stendhal/client/NotificationType.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

/**
 * A logical notification type, which can be mapped to UI specific contexts.
 * This would be similar to logic styles vs. physical styles in HTML.
 */
public enum NotificationType {
	INFORMATION("information"),
	NEGATIVE("negative"),
	NORMAL("normal"),
	POSITIVE("positive"),
	TUTORIAL("tutorial");

	/**
	 * The mapping mnemonic.
	 */
	protected String	mnemonic;


	/**
	 * Create a notification type.
	 *
	 * @param	mnemonic	The mapping mnemonic.
	 */
	private NotificationType(final String mnemonic) {
		this.mnemonic = mnemonic;
	}


	//
	// NotificationType
	//

	/**
	 * Get the mapping mnemonic (programatic name).
	 *
	 * @return	The mapping mnemonic.
	 */
	public String getMnemonic() {
		return mnemonic;
	}
}
