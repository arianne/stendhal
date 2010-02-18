package games.stendhal.tools.loganalyser.itemlog.contraband;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;

public class RegisterItemEventType implements ItemEventType {

	public void process(LogEntry entry, ItemInfo info) {
		info.setName(entry.getParam1());
	}

}
