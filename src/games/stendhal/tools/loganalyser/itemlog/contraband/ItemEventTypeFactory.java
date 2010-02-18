package games.stendhal.tools.loganalyser.itemlog.contraband;

public class ItemEventTypeFactory {

	public static ItemEventType create() {
		return null;
	}

	public static ItemEventType create(String event) {
		if (event.equals("register")) {
			return new RegisterItemEventType();
		} else if (event.equals("merge in")) {
			return new MergedInItemEventType();
		} else if (event.equals("splitted out")) {
			return new SplittedOutItemEventType();
		}
		return new DoNothingItemEventType();
	}
}
