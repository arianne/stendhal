/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.wt.core;

import games.stendhal.client.SoundSystem;
import games.stendhal.client.gui.wt.Character;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import games.stendhal.client.stendhal;

/**
 * This manager keeps track of all the windows and their positions/
 * minimized state.
 * 
 * @author mtotz
 */
public class WindowManager
{
  /** filename for the settings persistence */
  private static final String FILE_NAME = "windows.properties";
  /** the saved window positions */
  private Properties properties;
  /** the instance */
  private static WindowManager instance;
  
  /** maps the window names to their configs */
  private Map<String, WindowConfiguration> configs = new HashMap<String, WindowConfiguration>();
  
  /** no public constuctor */
  private WindowManager()
  {
    // try to read the configurations from disk
    read();
  }

  /** returns the windowmanagers instance */
  public static WindowManager getInstance()
  {
    if (instance == null)
    {
      instance = new WindowManager();
    }
    return instance;
  }
  
  /** 
   * Sets default window properties. These are used only when there are no 
   * properties known for this panel. 
   */
  public void setDefaultProperties(String name, boolean minimized, int x, int y)
  {
    if (!configs.containsKey(name))
    {
      WindowConfiguration config = new WindowConfiguration(name);
      config.readFromProperties(properties, minimized,x,y);
      configs.put(name,config);
    }
  }
  
  /** saves the current settings to a file */
  public void save()
  {
    StringBuilder buf = new StringBuilder(); 
    for (WindowConfiguration config : configs.values())
    {
      buf.append(config.writeToPropertyString());
    }
    try
    {
      FileWriter writer = new FileWriter(new File(stendhal.STENDHAL_FOLDER+FILE_NAME));
      writer.append(buf.toString());
      writer.close();
    }
    catch (IOException e)
    {
      // ignore exception
      e.printStackTrace();
    } 
  }

  /** saves the current settings to a file */
  public void read()
  {
    properties = new Properties();
    try
    {
      File file = new File(stendhal.STENDHAL_FOLDER+FILE_NAME);
      if(file.exists())
        {
        InputStream propsFile = new FileInputStream(file);
        properties.load(propsFile);
        propsFile.close();
        }
    }
    catch (IOException e)
    {
      // ignore exception
      e.printStackTrace();
    }
  }
  
  /** returns the config. If it does not exist yet, a new one is created. */
  private WindowConfiguration getConfig(Panel panel)
  {
    String name = panel.getName();
    WindowConfiguration winC = configs.get(name); 
    if ( winC == null )
    {
      winC = new WindowConfiguration(name);
      winC.readFromProperties(properties, panel);
      configs.put(name,winC);
    }
    return winC;
  }

  /** Formats the window with the saved config.
   * Nothing happens when this windows config is not known. 
   */
  public void formatWindow(Panel panel)
  {
    WindowConfiguration config = getConfig(panel);
    if (config == null)
    {
      // window not supervised
      return;
    }
    
    panel.moveTo(config.x,config.y);
    panel.setMinimized(config.minimized);
  }
  
  /** the panel was moved, so update the internal representation */
  public void moveTo(Panel panel, int x, int y)
  {
    WindowConfiguration config = getConfig(panel);
    config.x = x;
    config.y = y;
  }

  /** the panels minimized state changed, update the internal representation */
  public void setMinimized(Panel panel, boolean state)
  {
    WindowConfiguration config = getConfig(panel);
    
    if ( config.minimized != state )
       if ( !state )
       {
          if ( config.name.equals("bag") ) 
             SoundSystem.playSound( "inventory-open", 10 );
          
          else if ( (panel instanceof Character) )
              SoundSystem.playSound( "window-open-1", 40 );
          
          else if ( config.name.equals("settings") || config.name.equals("minimap") )
             SoundSystem.playSound( "window-open-2", 30 );
   
          else if ( config.name.equals("chest") )
             SoundSystem.playSound( "chest-open", 75 );
       }
       else
          SoundSystem.playSound( "window-close", 10 );
          
    
    config.minimized = state;
  }
  
  /** encapsulates the configuration of a window */
  private class WindowConfiguration
  {
    /** name of the window */
    public String name;
    /** minimized state of the window */
    public boolean minimized;
    /** is the window enabled? */
    public boolean enabled;
    /** x-pos */
    public int x;
    /** y-pos */
    public int y;
    
    public WindowConfiguration(String name)
    {
      this.name = name;
    }

    /** returns to config as a property string */
    public String writeToPropertyString()
    {
      return
        "window."+name+".minimized="+minimized+"\n"+
        "window."+name+".enabled="+enabled+"\n"+
        "window."+name+".x="+x+"\n"+
        "window."+name+".y="+y+"\n";
    }
    
    /** returns to config as a property string */
    public String toString()
    {
      return writeToPropertyString();
    }
    
    /** adds all props to the property */
    public void writeToProperties(Properties props)
    {
      props.put("window."+name+".minimized",minimized);
      props.put("window."+name+".enabled",enabled);
      props.put("window."+name+".x",x);
      props.put("window."+name+".y",y);
    }

    /** reads the config from the properties */
    public void readFromProperties(Properties props, boolean defaultMinimized, int defaultX, int defaultY)
    {
      minimized = Boolean.parseBoolean(props.getProperty("window."+name+".minimized",Boolean.toString(minimized)));
      enabled = Boolean.parseBoolean(props.getProperty("window."+name+".enabled","true"));
      x = Integer.parseInt(props.getProperty("window."+name+".x",Integer.toString(defaultX)));
      y = Integer.parseInt(props.getProperty("window."+name+".y",Integer.toString(defaultY)));
    }

    /** reads the config from the properties */
    public void readFromProperties(Properties props, Panel defaults)
    {
      readFromProperties(props, defaults.isMinimized(),defaults.getX(),defaults.getY());
    }
    
    
  }
  
}
