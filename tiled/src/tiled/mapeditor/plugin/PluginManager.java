/**
 * 
 */
package tiled.mapeditor.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tiled.plugins.MapReaderPlugin;
import tiled.plugins.MapWriterPlugin;
import tiled.plugins.TiledPlugin;
import tiled.plugins.stendhal.StendReader;
import tiled.plugins.stendhal.StendWriter;
import tiled.plugins.stendhal.XStendReader;
import tiled.plugins.tiled.MapReader;

/**
 * A generic PlugIn Manager
 * @author mtotz
 */
public class PluginManager
{
  private Map<Class,List<Class>> plugins;
  private static PluginManager instance;

  /**    */
  private PluginManager()
  {
    super();
  }
  
  public static PluginManager getInstance()
  {
    if (instance == null)
    {
      instance = new PluginManager();
      instance.readPlugins(null);
    }
    return instance;
  }


  /**
   * @param base base dir
   * 
   */
  public void readPlugins(String base)
  {
    plugins = new HashMap<Class,List<Class>>();
    
    // buildin plugins
    addPlugin(MapReaderPlugin.class, MapReader.class);
    addPlugin(MapReaderPlugin.class, StendReader.class);
    addPlugin(MapReaderPlugin.class, XStendReader.class);
    
    addPlugin(MapWriterPlugin.class, StendWriter.class);
  }

  /**
   * @param interfaceClass the plugin interface
   * @param pluginClass
   */
  private void addPlugin(Class<? extends TiledPlugin> interfaceClass, Class<? extends TiledPlugin> pluginClass)
  {
    if (!interfaceClass.isAssignableFrom(pluginClass))
    {
      System.out.println(pluginClass.getName()+" is not an instance of "+interfaceClass.getName());
    }
    if (!plugins.containsKey(interfaceClass))
    {
      plugins.put(interfaceClass,new ArrayList<Class>());
    }
    
    List<Class> list = plugins.get(interfaceClass);
    if (!list.contains(pluginClass))
    {
      list.add(pluginClass);
    }
  }

  /**
   * @param pluginInterface the plugin interface
   * @return the list of known plugins of this type
   */
  public  List<Class> getPlugins(Class pluginInterface)
  {
    return plugins.get(pluginInterface);
  }

}
