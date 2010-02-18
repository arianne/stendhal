package games.stendhal.tools.loganalyser.itemlog.contraband;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;

public class DoNothingItemEventType implements ItemEventType {

	public void process(LogEntry entry, ItemInfo info) {
		// do nothing
	}

}
