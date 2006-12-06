package games.stendhal.server.util;

import java.awt.geom.Rectangle2D;

/**
 * An area is a specified place on a specified map like
 * (88, 78) to (109, 98) in 0_ados_wall_n.
 *
 * @author hendrik
 */
public class Area {
	private String mapName = null;
	private Rectangle2D /* Shape */ shape = null; 

	/**
	 * Create a new Area
	 *
	 * @param mapName name of the map
	 * @param shape   shape on that map
	 */
	public Area(String mapName, Rectangle2D shape) {
		this.mapName = mapName;
		this.shape = shape;
	}
}
