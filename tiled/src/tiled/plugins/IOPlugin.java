/**
 * 
 */
package tiled.plugins;

import javax.swing.filechooser.FileFilter;

/**
 * The plugin reads or saves files.
 *  
 * @author mtotz
 */
public interface IOPlugin extends TiledPlugin
{
  /**
   * Returns a list of FileFilters. This list is used in the FileSelection
   * Dialogs. 
   */
  public FileFilter[] getFilters();

}
