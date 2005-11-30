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
package games.stendhal.tools.tiled;

import tiled.core.*;
import tiled.io.*;
import java.util.Stack;
import java.util.Iterator;
import java.util.Properties;
import java.io.*;

/**
 * Writer Plugin for tiled. Saves maps as *.stend files. This plugin ignores the
 * filename. The files are created using the layer names as file names
 */
public class StendhalMapWriter implements MapWriter
{
  /**
   * Method writeMap
   * 
   * @param map
   * @param filename
   * @throws Exception
   */
  public void writeMap(Map map, String filename) throws Exception
  {  
    String level=null;

    File file = new File(map.getFilename());

    String fileContainer=file.getParentFile().getName();
    if(fileContainer.contains("Level "))
      {
      level=fileContainer.split("Level ")[1];
      }
    else
      {
      level="int";
      }
      
    String destination=new File(filename).getParent();
    String mapName=file.getName().split(".tmx")[0];

    FileOutputStream os = new FileOutputStream(destination+File.separator+level+"_"+mapName+".xstend");
    PrintWriter writer = new PrintWriter(os);
    
    writer.println("<map name=\""+mapName+"\">");
    
    Properties prop=map.getProperties();
    String x=(String)prop.get("x");
    String y=(String)prop.get("y");

    if(level.equals("int")==false)
      {
      writer.println("  <location level=\""+level+"\" x=\""+x+"\" y=\""+y+"\"/>");
      }
    else
      {
      writer.println("  <location level=\"int\"/>");
      }

    Iterator ml = map.getLayers();
    boolean firstTime=true;
    while (ml.hasNext())
    {
      MapLayer layer = (MapLayer) ml.next();
      
      if(firstTime)
      {
        firstTime=false;        
        writer.println("  <size width=\""+layer.getWidth()+"\" height=\""+layer.getHeight()+"\"/>");
      }
      
      writer.println("  <layer name=\""+layer.getName()+"\">");
      for (int j = 0; j < layer.getHeight(); j++)
      {
        for (int i = 0; i < layer.getWidth(); i++)
        {
          Tile tile = ((TileLayer) layer).getTileAt(i, j);
          int gid = 0;

          if (tile != null)
          {
            gid = tile.getGid();
          }

          writer.print(gid + ((i == layer.getWidth() - 1) ? "" : ","));
        }

        writer.println();
      }
      writer.println("  </layer>");
    
    }

    writer.println("  </map>");
    writer.close();
  }
 

  /**
   * Method writeTileset. Tilesets won't be written.
   * 
   * @param set
   * @param filename
   * @throws Exception
   */
  public void writeTileset(TileSet set, String filename) throws Exception
  {
  }

  /**
   * Method writeMap. Writing to an outputstream is not supported
   * 
   * @param map
   * @param out
   * @throws Exception
   */
  public void writeMap(Map map, OutputStream out) throws Exception
  {
  }

  /**
   * Method writeTileset. Tilesets won't be written.
   * 
   * @param set
   * @param out
   * @throws Exception
   */
  public void writeTileset(TileSet set, OutputStream out) throws Exception
  {
  }

  /** accepts all filenames ending with .stend */
  public boolean accept(File pathname)
  {
    try
    {
      String path = pathname.getCanonicalPath().toLowerCase();
      if (path.endsWith(".xstend"))
      {
        return true;
      }
    } catch (IOException e)
    {
    }
    return false;
  }

  public String getFilter() throws Exception
  {
    return "*.xstend";
  }

  public String getName()
  {
    return "Stendhal Writer";
  }

  public String getDescription()
  {
    return "+---------------------------------------------+\n"
        + "|      An experimental writer for Stendhal    |\n"
        + "|                                             |\n"
        + "|      (c) Miguel Angel Blanch Lardin 2005    |\n"
        + "|                                             |\n"
        + "+---------------------------------------------+";
  }

  public String getPluginPackage()
  {
    return "Stendhal Reader/Writer Plugin";
  }

  public void setErrorStack(Stack es)
  {
  }
}
