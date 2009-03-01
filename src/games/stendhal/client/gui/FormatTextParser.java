package games.stendhal.client.gui;

/**
 * Parsing of formated text. 
 *
 * @author Martin Fuchs
 */
public abstract class FormatTextParser {
	public void format(final String text) throws Exception {
		final String[] parts = text.split("#");

		int i = 0;
		for (String pieces : parts) {
				if (i > 0) {
					if (pieces.length()> 0) {
						char terminator = ' ';
		
						// color quoted compound words like "#'iron sword'"
						if (pieces.charAt(0) == '\'') {
							terminator = '\'';
							pieces = pieces.substring(1);
						}
		
						int index = pieces.indexOf(terminator);
						if (index == -1) {
							index = pieces.length();
						}
		
						colorText(pieces.substring(0, index));
		
						if ((terminator == '\'') && (index < pieces.length())) {
							++index;
						}
		
						pieces = pieces.substring(index);
					}
				}

			normalText(pieces);

			++i;
		}
	}

	public abstract void normalText(String txt) throws Exception;
	public abstract void colorText(String txt) throws Exception;
}
