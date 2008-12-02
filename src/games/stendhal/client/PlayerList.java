package games.stendhal.client;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerList {
	private Vector<String> namesList = new Vector<String>();

	public Vector<String> getNamesList() {
		return namesList;
	}

	public void generateWhoPlayers(final String text) {
	
		Matcher matcher = Pattern.compile("^[0-9]+ Players online:( .+)$").matcher(
				text);
	
		if (matcher.find()) {
			final String[] names = matcher.group(1).split("\\s+");
	
			getNamesList().removeAllElements();
			for (int i = 0; i < names.length; i++) {
				/*
				 * NOTE: On the future Players names won't have any non ascii
				 * character.
				 */
				matcher = Pattern.compile(
						"^([-_a-zA-Z0-9äöüßÄÖÜ]+)\\([0-9]+\\)$").matcher(
						names[i]);
				if (matcher.find()) {
					getNamesList().addElement(matcher.group(1));
				}
			}
		}
	
	}
}
