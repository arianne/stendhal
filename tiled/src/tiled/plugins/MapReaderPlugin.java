/**
 * 
 */
package tiled.plugins;

import java.io.InputStream;

import tiled.core.Map;

/**
 * The plugin reads a map file
 * @author mtotz
 */
public interface MapReaderPlugin extends IOPlugin
{
  /**
   * Loads a map from a file. 
   *
   * @param filename the filename of the map file
   * @return a {@link tiled.core.Map}
   */
  Map readMap(String filename);
  /**
   * Loads a map from a stream. 
   *
   * @param inputStream the InputStream from where to load the map
   * @return a {@link tiled.core.Map}
   */
  Map readMap(InputStream inputStream);
  
  
}
