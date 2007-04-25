package games.stendhal.tools.tiled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DeflaterOutputStream;

import marauroa.common.net.OutputSerializer;

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
	/** The same data in a raw byte array, so we save reencoding it again for serialization */
	byte[] raw;

	/**
	 * Constructor
	 * @param layerWidth the width of the layer.
	 * @param layerHeight the height of the layer
	 */ 
	public LayerDefinition(int layerWidth, int layerHeight) {
		raw=new byte[4*layerWidth*layerHeight];
		width=layerWidth;
		height=layerHeight;
	}

	/**
	 * Builds the real data array based on the byte array.
	 * It is only needed for objects, collision and protection, which is at most 40% of the layers.
	 */
	public void build() {
		data=new int[height*width];
		int offset=0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {								
				int tileId = 0;
				tileId |= ((int)raw[x*4+0+offset]& 0xFF);
				tileId |= ((int)raw[x*4+1+offset]& 0xFF) <<  8;
				tileId |= ((int)raw[x*4+2+offset]& 0xFF) << 16;
				tileId |= ((int)raw[x*4+3+offset]& 0xFF) << 24;
				
				data[x+y*width]=tileId;
			}
		
		offset+=4*width;
		}
	}

	/**
	 * Returns the allocated raw array so it can be modified.
	 * @return
	 */
	public byte[] exposeRaw() {
		return raw;
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

	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] encode() throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		OutputSerializer out = new OutputSerializer(array);
		
		out.write(name);
		out.write(width);
		out.write(height);
		
		out.write(raw);
		
		return array.toByteArray();
    }

	/**
	 * Returns the width of the layer
	 * @return
	 */
	public int getWidth() {
	    return width;
    }

	/**
	 * Returns the height of the layer
	 * @return
	 */
	public int getHeight() {
	    return height;
    }
}
