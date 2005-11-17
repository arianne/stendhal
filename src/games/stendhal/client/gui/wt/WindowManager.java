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
package games.stendhal.client.gui.wt;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * This manager keeps track of all the windows and their positions/
 * minimized state.
 * 
 * @author mtotz
 */
public class WindowManager
{
  /** the instance */
  private static WindowManager instance;
  
  /** maps the window names to their configs */
  private Map<String, WindowConfiguration> configs = new HashMap<String, WindowConfiguration>();
  
  /** returns the windowmanagers instance */
  public static WindowManager getInstance()
  {
    if (instance == null)
    {
      instance = new WindowManager();
    }
    return instance;
  }

  /** no public constuctor */
  private WindowManager()
  { }
  
  /** */
  private WindowConfiguration getConfig(String name)
  {
    if (!configs.containsKey(name))
    {
      configs.put(name,new WindowConfiguration(name));
    }
    return configs.get(name);
  }

  /** Formats the window with the saved config.
   * Nothing happens when this windows config is not known. 
   */
  public void formatWindow(Panel panel)
  {
    WindowConfiguration config = configs.get(panel.getName());
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
    WindowConfiguration config = getConfig(panel.getName());
    config.x = x;
    config.y = y;
  }

  /** the panels minimized state changed, update the internal representation */
  public void setMinimized(Panel panel, boolean state)
  {
    WindowConfiguration config = getConfig(panel.getName());
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
    public void readFromProperties(Properties props)
    {
      minimized = Boolean.parseBoolean(props.getProperty("window."+name+".minimized","false"));
      enabled = Boolean.parseBoolean(props.getProperty("window."+name+".enabled","true"));
      x = Integer.parseInt(props.getProperty("window."+name+".x","0"));
      y = Integer.parseInt(props.getProperty("window."+name+".y","0"));
    }
    
  }
  
}
