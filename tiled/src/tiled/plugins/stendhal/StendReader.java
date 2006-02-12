/**
 * 
 */
package tiled.plugins.stendhal;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.filechooser.FileFilter;

import tiled.core.Map;
import tiled.plugins.MapReaderPlugin;

/**
 * @author mtotz
 *
 */
public class StendReader extends Reader implements MapReaderPlugin
{
  /** reads the map */
  public Map readMap(String filename)
  {
    try
    {
      return readMap(new FileInputStream(new File(filename)));
    } catch (Exception e)
    {
      throw new RuntimeException(e); 
    }
  }

  /** reads the map */
  public Map readMap(InputStream inputStream)
  {
    return readMap(inputStream,false);
  }

  /** all filefilters */
  public FileFilter[] getFilters()
  {
    return new FileFilter[] { new FileFilter()
        {

          public boolean accept(File pathname)
          {
            return pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".stend");
          }

          public String getDescription()
          {
            return "Stendhal Map Files (*.stend)";
          }

        } };
  }

  /** returns the description */
  public String getPluginDescription()
  {
    return "Mapreader for the Stendhal map format";
  }
}
