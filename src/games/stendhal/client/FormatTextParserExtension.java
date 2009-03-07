/**
 * 
 */
package games.stendhal.client;

import games.stendhal.client.gui.FormatTextParser;

public final class FormatTextParserExtension extends FormatTextParser {
	private final StringBuilder temp;

	public FormatTextParserExtension(StringBuilder temp) {
		this.temp = temp;
	}

	@Override
	public void normalText(final String tok) {
		temp.append(tok);
	}

	@Override
	public void colorText(final String tok) {
		temp.append(tok);
	}
}
