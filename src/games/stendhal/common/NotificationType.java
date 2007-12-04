/*
 * @(#) src/games/stendhal/client/NotificationType.java
 *
 * $Id$
 */

package games.stendhal.common;

//
//

/**
 * A logical notification type, which can be mapped to UI specific contexts.
 * This would be similar to logical styles vs. physical styles in HTML.
 */
public enum NotificationType {
	CLIENT("client"), ERROR("error"), INFORMATION("information"), NEGATIVE("negative"), NORMAL(
			"normal"), POSITIVE("positive"), PRIVMSG("privmsg"), RESPONSE(
			"response"), SIGNIFICANT_NEGATIVE("significant_negative"), SIGNIFICANT_POSITIVE(
			"significant_positive"), TUTORIAL("tutorial");

	/**
	 * The mapping mnemonic.
	 */
	protected String mnemonic;

	/**
	 * Create a notification type.
	 * 
	 * @param mnemonic
	 *            The mapping mnemonic.
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
	 * @return The mapping mnemonic.
	 */
	public String getMnemonic() {
		return mnemonic;
	}
}
