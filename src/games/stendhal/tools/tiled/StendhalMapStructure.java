package games.stendhal.tools.tiled;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the map format that our client uses.
 * 
 * @author miguel
 * 
 */
public class StendhalMapStructure {
	/** TMX Filename that contains this map. */
	String filename;

	/** Width of the map */
	int width;

	/** Height of the map */
	int height;

	/** List of tilesets that this map contains */
	List<TileSetDefinition> tilesets;

	/** List of layers this map contains */
	List<LayerDefinition> layers;

	/**
	 * Constructor.
	 * 
	 * @param w
	 *            the width of the map
	 * @param h
	 *            the height of the map.
	 */
	public StendhalMapStructure(int w, int h) {
		width = w;
		height = h;
		tilesets = new LinkedList<TileSetDefinition>();
		layers = new LinkedList<LayerDefinition>();
	}

	/**
	 * Add a new tileset to the map
	 * 
	 * @param set
	 *            new tileset
	 */
	public void addTileset(TileSetDefinition set) {
		tilesets.add(set);
	}

	/**
	 * Add a new layer to the map
	 * 
	 * @param layer
	 *            new layer
	 */
	public void addLayer(LayerDefinition layer) {
		layer.setMap(this);
		layers.add(layer);
	}

	/**
	 * Sets the map TMX filename
	 * 
	 * @param filename
	 *            the map TMX filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Returns a list of the tilesets this map contains.
	 * 
	 * @return a list of the tilesets this map contains.
	 */
	public List<TileSetDefinition> getTilesets() {
		return tilesets;
	}

	/**
	 * Returns a list of the layers this map contains.
	 * 
	 * @return a list of the layers this map contains.
	 */
	public List<LayerDefinition> getLayers() {
		return layers;
	}

	/**
	 * Return true if the layer with given name exists.
	 * 
	 * @param layername
	 *            the layer name
	 * @return true if it exists.
	 */
	public boolean hasLayer(String layername) {
		return getLayer(layername) != null;
	}

	/**
	 * Returns the layer whose name is layer name or null
	 * 
	 * @param layername
	 *            the layer name
	 * @return the layer object or null if it doesnt' exists
	 */
	public LayerDefinition getLayer(String layername) {
		for (LayerDefinition layer : layers) {
			if (layername.equals(layer.getName())) {
				return layer;
			}
		}

		return null;
	}

	/**
	 * Build all layers data.
	 */
	public void build() {
		for (LayerDefinition layer : layers) {
			layer.build();
		}
	}
}
