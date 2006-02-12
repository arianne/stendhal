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
public class XStendReader extends Reader implements MapReaderPlugin
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
    return readMap(inputStream,true);
  }

  /** all filefilters */
  public FileFilter[] getFilters()
  {
    return new FileFilter[] { new FileFilter()
        {

          public boolean accept(File pathname)
          {
            return pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".xstend");
          }

          public String getDescription()
          {
            return "Stendhal Compressed Map Files (*.xstend)";
          }

        } };
  }

  /** returns the description */
  public String getPluginDescription()
  {
    return "Mapreader for the Stendhal compressed map format";
  }
}
