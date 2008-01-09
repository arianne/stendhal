package games.stendhal.tools.loganalyser.itemlog.consistency;


/**
 * represents an entry of the itemlog table.
 *
 * @author hendrik
 */
public class LogEntry {
	private String timestamp;
	private String itemid;
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
	 * @param itemid id of the item
	 * @param source name of player
	 * @param event  name of event
	 * @param param1 additional param1
	 * @param param2 additional param2
	 * @param param3 additional param3
	 * @param param4 additional param4
	 */
	public LogEntry(String timestamp, String itemid, String source, String event, String param1, String param2, String param3, String param4) {
	    this.timestamp = timestamp;
	    this.itemid = itemid;
	    this.source = source;
	    this.event = event;
	    this.param1 = param1;
	    this.param2 = param2;
	    this.param3 = param3;
	    this.param4 = param4;
    }

	public String getItemid() {
		return itemid;
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

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(timestamp);
    	sb.append('\t');
    	sb.append(itemid);
    	sb.append('\t');
    	sb.append(source);
    	sb.append('\t');
    	sb.append(event);
    	sb.append('\t');
    	sb.append(param1);
    	sb.append('\t');
    	sb.append(param2);
    	sb.append('\t');
    	sb.append(param3);
    	sb.append('\t');
    	sb.append(param4);
    	
    	return sb.toString();
    }
}
