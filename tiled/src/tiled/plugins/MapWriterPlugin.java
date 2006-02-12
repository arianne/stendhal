/**
 * 
 */
package tiled.plugins;


import java.io.OutputStream;

import tiled.core.Map;

/**
 * The plugin writes a map file
 * 
 * @author mtotz
 */
public interface MapWriterPlugin extends IOPlugin
{
  /**
   * Writes a map to a file. 
   *
   * @param filename the filename of the map file
   * @param map a{@link tiled.core.Map}
   */
  void writeMap(Map map, String filename);
  /**
   * Writes a map to a stream. 
   *
   * @param outputStream the OutputStream where to write the map to
   * @param map a{@link tiled.core.Map}
   */
  void writeMap(Map map, OutputStream outputStream);

}
