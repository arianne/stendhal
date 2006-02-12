/**
 * 
 */
package tiled.plugins;

import java.io.InputStream;

import tiled.core.TileSet;

/**
 * The plugin reads tileset
 * @author mtotz
 */
public interface TilesetReaderPlugin extends IOPlugin
{
  /**
   * Loads a fileset from a file. 
   *
   * @param filename the filename of the tileset file
   * @return a {@link tiled.core.TileSet}
   */
  TileSet readTileset(String filename);
  /**
   * Loads a fileset from a stream. 
   *
   * @param inputStream the InputStream from where to load the tileset
   * @return a {@link tiled.core.TileSet}
   */
  TileSet readTileset(InputStream inputStream);

}
