package games.stendhal.tools.tiled;

/**
 * Stores a definition of a tileset.
 * Mainly its name, the source image used and the starting global id.
 * 
 * @author miguel
 *
 */
public class TileSetDefinition {
	/** The name of the tileset. Useless */
	private String name;
	/** The source image of this tileset */
	private String source;
	/** The id where this tileset begins to number tiles. */ 
	private int gid;			

	/**
	 * Constructor
	 * @param name the *useless* name of the tileset.
	 * @param firstGid the id where this tileset begins to number tiles.
	 */
	public TileSetDefinition(String name, int firstGid) {
		this.name=name;
		this.gid=firstGid;
    }
	
	/**
	 * Returns the id where this tileset begins to number tiles
	 * @return the id where this tileset begins to number tiles
	 */
	public int getFirstGid() {
		return gid;
	}

	/**
	 * Set the filename of the source image of the tileset. 
	 * @param attributeValue the filename
	 */
	public void setSource(String attributeValue) {
		this.source=attributeValue;
    }
	
	/**
	 * Returns the filename of the source image of the tileset.
	 * @return the filename of the source image of the tileset.
	 */
	public String getSource() {
		return source;		
	}
}
