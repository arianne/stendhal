package games.stendhal.server.entity.mapstuff.area;

import org.apache.log4j.Logger;

import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * an area which consists of tiled images
 *
 * @author hendrik
 */
public class TiledArea extends AreaEntity {
	private static Logger logger = Logger.getLogger(TiledArea.class	);

	private static final String ATTR_TILESET_NAMES = "tileset_names";
	private static final String ATTR_TILESET_INDEX = "tileset_index";
	private static final String ATTR_TILE_DATA = "tile_data";
	private int[] tilesetRef;
	private int[] data;

	/**
	 * Define the RPClass.
	 *
	 * @return The configured RPClass.
	 */
	public static RPClass createRPClass() {
		final RPClass rpclass = new RPClass("tiled_entity");
		rpclass.isA("area");
		rpclass.addAttribute(ATTR_TILESET_NAMES, Type.LONG_STRING);
		rpclass.addAttribute(ATTR_TILESET_INDEX, Type.LONG_STRING);
		rpclass.addAttribute(ATTR_TILE_DATA, Type.LONG_STRING);
		return rpclass;
	}

	/**
	 * sets the tile names
	 *
	 * @param names comma separated list of tileset names
	 */
	public void setTilesetNames(String names) {
		put(ATTR_TILESET_NAMES, names);
		notifyWorldAboutChanges();
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		initDataArrays();
	}

	/**
	 * sets the data for the specified coordinate
	 *
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param tileset tileset index within the tileset names list
	 */
	public void setTilesetIndex(int x, int y, int tileset) {
		int index = x + y * (int) super.getWidth();
		if (index >= data.length) {
			logger.error("Index out of bounds: " + x + ", " + y, new Throwable());
			return;
		}
		tilesetRef[index] = tileset;
		put(ATTR_TILESET_INDEX, arrayToString(tilesetRef));
	}

	/**
	 * sets the data for the specified coordinate
	 *
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param offset offset within the tileset
	 */
	public void setData(int x, int y, int offset) {
		int index = x + y * (int) super.getWidth();
		if (index >= data.length) {
			logger.error("Index out of bounds: " + x + ", " + y, new Throwable());
			return;
		}
		data[index] = offset;
		put(ATTR_TILE_DATA, arrayToString(data));
	}

	/**
	 * converts an array into a string
	 *
	 * @param array array to convert
	 * @return String
	 */
	private static String arrayToString(int[] array) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i : array) {
			if (first) {
				first = false;
			} else {
				sb.append(" ");
			}
			sb.append(i);
		}
		return sb.toString();
	}

	/**
	 * Initializes the data arrays based on the size of the entity.
	 */
	private void initDataArrays() {
		int size = (int) (super.getWidth() * super.getHeight());
		tilesetRef = new int[size];
		data = new int[size];
		for (int i = 0; i < size; i++) {
			tilesetRef[i] = 0;
			data[i] = -1;
		}
	}
}
