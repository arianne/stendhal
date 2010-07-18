package games.stendhal.server.entity.trade;

/**
 * Interface for objects that can give information about a point of time
 * 
 * @author kiheru
 */
public interface Dateable {
	/**
	 * @return point of time relevant for this object
	 */
	long getTimestamp();
}
