package games.stendhal.client.gui;

/**
 * Parsing of formated text. 
 *
 * @author Martin Fuchs
 */
public abstract class FormatTextParser {
	/**
	 * Write text optionally colored, and strips '\' from the front of the '#' signs
	 * 
	 * @param text the text to write
	 * @param color true if the text should be coloured
	 * @throws Exception
	 */
	private void writeText(final String text, boolean color) throws Exception {
		if (color) {
			colorText(text.replaceAll("\\\\#", "#"));
		} else {
			normalText(text.replaceAll("\\\\#", "#"));
		}
	}

	public void format(String text) throws Exception {
		int startIndex = 0;
		int unquotedIndex, quotedIndex;
		
		while (!text.isEmpty()) {
			unquotedIndex = text.indexOf('#', startIndex);
			if (unquotedIndex == -1) {
				writeText(text, false);
				text = "";
			} else {
				quotedIndex = text.indexOf("\\#", startIndex);
				if (quotedIndex != -1 && quotedIndex == (unquotedIndex - 1)) {
					// the next match is \#. skip it and let writeText replace it
					startIndex = quotedIndex + 2;
				} else {
					// found a lone #. start coloring
					// Write the text before the #
					if (unquotedIndex != 0) {
						//writeText(text.substring(0, unquotedIndex - 1), false);
						writeText(text.substring(0, unquotedIndex), false);
					}
					
					// Find the region to color
					char endMarker = ' ';
					int shift = 0;
					if ((text.length() > (unquotedIndex + 1)) && (text.charAt(unquotedIndex + 1) == '\'')) {
						endMarker = '\'';
						shift = 1;
					}
					// skip the #, and possible quoting
					text = text.substring(unquotedIndex + 1 + shift);
					int stopAt = text.indexOf(endMarker);
					if (stopAt  == -1) {
						// No end marker found. the rest is colored (colors improperly quoted strings) 
						writeText(text, true);
						text = "";
					} else {
						// color until the endMarker, and skip the possible quoting
						writeText(text.substring(0, stopAt), true);
						text = text.substring(stopAt + shift);
					}
				}
			}
		}
	}

	public abstract void normalText(String txt) throws Exception;
	public abstract void colorText(String txt) throws Exception;
}
