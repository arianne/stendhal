package games.stendhal.tools.loganalyser.itemlog.contraband;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;

public class MergedInItemEventType implements ItemEventType {

	public void process(LogEntry entry, ItemInfo info) {
		info.setQuantity(entry.getParam2());
	}

}
