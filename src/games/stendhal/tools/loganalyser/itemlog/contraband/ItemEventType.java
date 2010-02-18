package games.stendhal.tools.loganalyser.itemlog.contraband;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;

/**
 * handles item events
 */
public interface ItemEventType {

	
	/**
	 * processes a log entry
	 *
	 * @param entry LogEntry
	 * @param info ItemInfo
	 */
	public void process(LogEntry entry, ItemInfo info);
}
