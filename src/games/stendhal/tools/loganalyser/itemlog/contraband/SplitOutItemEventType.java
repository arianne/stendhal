package games.stendhal.tools.loganalyser.itemlog.contraband;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;

public class SplitOutItemEventType implements ItemEventType {

	public void process(LogEntry entry, ItemInfo info) {
		info.setQuantity(entry.getParam4());
	}

}
