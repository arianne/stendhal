package games.stendhal.tools.itemlog;


/**
 * represents an entry of the itemlog table.
 *
 * @author hendrik
 */
public class LogEntry {
	private String timestamp;
	private String source;
	private String event;
	private String param1;
	private String param2;
	private String param3;
	private String param4;

	/**
	 * creates a new LogEntry
	 *
	 * @param timestamp timestamp
	 * @param source name of player
	 * @param event  name of event
	 * @param param1 additional param1
	 * @param param2 additional param2
	 * @param param3 additional param3
	 * @param param4 additional param4
	 */
	public LogEntry(String timestamp, String source, String event, String param1, String param2, String param3, String param4) {
	    this.timestamp = timestamp;
	    this.source = source;
	    this.event = event;
	    this.param1 = param1;
	    this.param2 = param2;
	    this.param3 = param3;
	    this.param4 = param4;
    }

    public String getEvent() {
    	return event;
    }

    public String getParam1() {
    	return param1;
    }

    public String getParam2() {
    	return param2;
    }

    public String getParam3() {
    	return param3;
    }

    public String getParam4() {
    	return param4;
    }

    public String getSource() {
    	return source;
    }

    public String getTimestamp() {
    	return timestamp;
    }
}
