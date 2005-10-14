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
/*
 * MapConverter.java
 *
 * Created on 13. Oktober 2005, 18:24
 *
 */

package games.stendhal.tools;

import games.stendhal.tools.tiled.StendhalMapWriter;
import java.io.File;
import java.io.FilenameFilter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import tiled.core.Map;
import tiled.io.xml.XMLMapTransformer;

/**
 * Converts the stendhal maps from *.tmx to *.stend
 * This class can be started from the command line or through an ant task
 *
 * @author mtotz
 */
public class MapConverter extends Task
{
  /** path to the tmx files */
  private String tmxPath;
  /** path where the *.stend goes */
  private String stendPath;
  /** Creates a new instance of MapConverter */
  public MapConverter()
  {}
  
  /** converts the map files */
  public void convert() throws Exception
  {
    System.out.println("tmxPath = "+tmxPath);
    System.out.println("stendPath = "+stendPath);
    
    File tmxDir = new File(tmxPath);
    if (!tmxDir.exists())
    {
      throw new IllegalArgumentException("path to tmxfiles ("+tmxPath+") does not exists");
    }
    // find all *.tmx files
    File[] tmxFiles = tmxDir.listFiles(
            new FilenameFilter()
            {
              public boolean accept(File dir, String name)
              {
                  return (name.endsWith(".tmx"));
              }
            } );

    for (File file : tmxFiles)
    {
      String filename = file.getAbsolutePath();
      // some internal tiled magic: load the map 
      Map map = new XMLMapTransformer().readMap(filename);
      // and save it
      filename = stendPath+"\\"+file.getName().replaceAll("\\.tmx",".stend");
      new StendhalMapWriter().writeMap(map, filename);
    }
  }
  /** The setter for the "tmxPath" attribute */
  public void setTmxPath(String tmxPath)
  {
      this.tmxPath = tmxPath;
  }

  /** The setter for the "stendPath" attribute */
  public void setStendPath(String stendPath)
  {
      this.stendPath = stendPath;
  }
  
  /** ants execute method. */
  public void execute() throws BuildException
  {
    try
    {
      convert();
    }
    catch (Exception e)
    {
      throw new BuildException(e);
    }
  }
  
  /** */
  public static void main(String[] args)
  {
//    args = new String[] {"G:\\project\\stendhal\\tiled","c:\\temp"};
    if (args.length < 2)
    {
      System.out.println("usage: java games.stendhal.tools.MapConverter <path to *.tmx files> <path where the *.stend files goes>");
      return;
    }
    // do the job
    MapConverter converter = new MapConverter();
    converter.tmxPath = args[0];
    converter.stendPath = args[1];
    converter.execute();
  }
}
