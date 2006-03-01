/**
 * 
 */
package tiled.plugins.stendhal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.filechooser.FileFilter;

import tiled.core.Map;
import tiled.plugins.MapWriterPlugin;

/**
 * @author mtotz
 *
 */
public class XStendWriter extends Writer implements MapWriterPlugin
{

  /** writes the map */
  public void writeMap(Map map, String filename)
  {
    try
    {
      FileOutputStream os = new FileOutputStream(new File(filename));
      writeMap(map,os,true);
      os.close();
    } catch (Exception e)
    {
      throw new RuntimeException(e); 
    }
  }

  /** writes the map */
  public void writeMap(Map map, OutputStream outputStream)
  {
    writeMap(map,outputStream,true);
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
            return "Stendhal Map Files (*.xstend)";
          }

        } };
  }

  /** returns the description */
  public String getPluginDescription()
  {
    return "Mapreader for the Stendhal map format";
  }
}
