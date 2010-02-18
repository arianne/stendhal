package games.stendhal.tools.loganalyser.itemlog.contraband;

public class ItemEventTypeFactory {

	public static ItemEventType create() {
		return null;
	}

	public static ItemEventType create(String event) {
		return new DoNothingItemEventType();
	}
}
