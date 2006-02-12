/**
 * 
 */
package tiled.plugins;

import java.io.InputStream;

import tiled.core.TileSet;

/**
 * The plugin writes tileset.
 * 
 * @author mtotz
 */
public interface TilesetWriterPlugin extends IOPlugin
{
  /**
   * Writes a fileset to a file. 
   *
   * @param filename the filename of the tileset file
   * @param tileSet a {@link tiled.core.TileSet}
   */
  void writeTileset(TileSet tileSet, String filename);

  /**
   * Writes a fileset to a stream. 
   *
   * @param inputStream the InputStream where to write the tileset to
   * @param tileSet a {@link tiled.core.TileSet}
   */
  void writeTileset(TileSet tileSet, InputStream inputStream);
}
