package games.stendhal.tools.itemlog;

/**
 * represents the location of an item (slot or ground)
 *
 * @author hendrik
 */
public class ItemLocation {
	private String type;
	private String param1;
	private String param2;

	/**
	 * extract source from event name
	 *
	 * @param event name of event
	 * @return source
	 */
	private String getSourceFromEventName(String event) {
		if (event.indexOf("-to-") < 0) {
			return null;
		}
		return event.substring(0, event.indexOf("-to-"));
	}

	/**
	 * extract destination from event name
	 *
	 * @param event name of event
	 * @return destination
	 */
	private String getDestFromEventName(String event) {
		if (event.indexOf("-to-") < 0) {
			return null;
		}
		return event.substring(event.indexOf("-to-") + 4);
	}

	/**
	 * checks the consitency between the stored information and the
	 * logged source. Returns true in case no information is stored.
	 *
	 * @param event  name of event
	 * @param param1 param1 from database
	 * @param param2 param2 from database
	 * @return true in case the location is consistent or unknown
	 */
	public boolean check(String event, String param1, String param2) {
		String source = getSourceFromEventName(event);
		if (source == null) {
			return true;
		}
		
		if (type == null) {
			return true;
		}

		return source.equals(type)
			&& this.param1.equals(param1)
			&& this.param2.endsWith(param2);
	}

	/**
	 * updates the location in case the event is a location change event. 
	 *
	 * @param event  name of event
	 * @param param3 param3 from database
	 * @param param4 param4 from database
	 */
	public void update(String event, String param3, String param4) {
		String dest = getDestFromEventName(event);
		if (dest == null) {
			return;
		}

		type = dest;
		param1 = param3;
		param2 = param4;
	}
}
