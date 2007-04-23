package games.stendhal.tools.tiled;

/**
 * The class that stores the definition of a layer.
 * A Layer consists mainly of:<ul>
 * <li>width and height
 * <li>name <b>VERY IMPORTANT</b>
 * <li>data 
 * </ul>
 * 
 * @author miguel
 *
 */
public class LayerDefinition {
	/** Width of the layer that SHOULD be the same that the width of the map. */
	int width;
	/** Height of the layer that SHOULD be the same that the height of the map. */
	int height;

	/** Name of the layer that MUST be one of the available:<ul>
	 * <li>0_floor
	 * <li>1_terrain
	 * <li>2_object
	 * <li>3_roof
	 * <li>4_roof_add
	 * <li>objects
	 * <li>collision
	 * <li>protection
	 * </ul>
	 */
	String name;
	
	/** The data encoded as int in a array of size width*height */
	int[] data;

	/**
	 * Constructor
	 * @param layerWidth the width of the layer.
	 * @param layerHeight the height of the layer
	 */ 
	public LayerDefinition(int layerWidth, int layerHeight) {
		data=new int[layerWidth*layerHeight];
		width=layerWidth;
		height=layerHeight;
	}

	/**
	 * Returns the allocated array so it can be modified.
	 * @return
	 */
	public int[] expose() {
		return data;
	}

	/**
	 * Set a tile at the given x,y position.
	 * @param x the x position
	 * @param y the y position 
	 * @param tileId the tile code to set ( Use 0 for none ).
	 */
	public void set(int x, int y, int tileId) {
		data[y*width+x]=tileId;
	}

	/**
	 * Returns the tile at the x,y position 
	 * @param x the x position
	 * @param y the y position 
	 * @return the tile that exists at that position or 0 for none.
	 */
	public int getTileAt(int x, int y) {
		return data[y*width+x];
	}
}
