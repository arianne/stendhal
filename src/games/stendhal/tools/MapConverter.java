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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import tiled.core.Map;
import tiled.io.xml.XMLMapTransformer;

import tiled.core.MapLayer;
import tiled.view.old.MapView;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

/**
 * Converts the stendhal maps from *.tmx to *.stend
 * This class can be started from the command line or through an ant task
 *
 * @author mtotz
 */
public class MapConverter extends Task
{
  /** path where the *.stend goes */
  private String stendPath;
  private String imagePath;
  /** list of *.tmx files to convert */
  private List<FileSet> filesets = new ArrayList<FileSet>();
  
  /** Creates a new instance of MapConverter */
  public MapConverter()
  {}
  
  /** converts the map files */
  public void convert(String tmxFile) throws Exception
  {
    File resourceFile=new File(stendPath+File.separator+getResourceFilename(tmxFile));
    File file = new File(tmxFile);
    
    if(file.lastModified()<resourceFile.lastModified())
      {
      System.out.println ("There are any changes. Already converted");
      return;
      }

    String filename = file.getAbsolutePath();
    // some internal tiled magic: load the map 
    Map map = new XMLMapTransformer().readMap(filename);
    
    saveMap(map,tmxFile);
    saveImageMap(map,tmxFile);
  }

  private String getResourceFilename(String tmxFile)
    {
    String filename=null;
    
    File file = new File(tmxFile);

    String area=file.getParentFile().getName();   
    String level=null;
     
    String fileContainer=file.getParentFile().getParent();
    
    if(fileContainer.contains("Level "))
      {
      level=fileContainer.split("Level ")[1];
      }
    else
      {
      level="int";
      }
      
    String mapName=file.getName().split(".tmx")[0];

    if(level.equals("int") && area.equals("abstract"))
      {
      filename=level.replace("-","sub_")+"_"+mapName.replace("-","sub_")+".xstend";
      }
    else
      {
      filename=level.replace("-","sub_")+"_"+area+"_"+mapName.replace("-","sub_")+".xstend";
      }
    
    return filename;
    }
 
  private void saveMap(Map map,String tmxFile) throws Exception
    {
    File file = new File(tmxFile);

    // and save it
    String filename = stendPath+File.separator+file.getName().replaceAll("\\.tmx",".stend");
    new games.stendhal.tools.tiled.StendhalMapWriter().writeMap(map, filename);
  }
  
  private void saveImageMap(Map map,String tmxFile)
  {
    File file = new File(tmxFile);
    String filename = file.getAbsolutePath();

    for(MapLayer layer: map)
      {
      if(layer.getName().equals("navigation") || layer.getName().equals("collision") || layer.getName().equals("objects") || layer.getName().equals("protection"))
        {
        layer.setVisible(false);
        }
      }

    MapView myView = MapView.createViewforMap(map);
    myView.enableMode(MapView.PF_NOSPECIAL);
    myView.setZoom(0.0625);
    Dimension d = myView.getPreferredSize();
    BufferedImage i = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = i.createGraphics();
    g.setClip(0, 0, d.width, d.height);
    myView.paint(g);
    
    String area=file.getParentFile().getName();
    String level;
    
    String fileContainer=file.getParentFile().getParent();
    
    if(fileContainer.contains("Level "))
      {
      level=fileContainer.split("Level ")[1];
      }
    else
      {
      level="int";
      }

    if(level.equals("int") && area.equals("abstract"))
      {
      filename = imagePath+File.separator+level.replace("-","sub_")+"_"+file.getName().replaceAll("\\.tmx",".png");
      }
    else
      {
      filename = imagePath+File.separator+level.replace("-","sub_")+"_"+area+"_"+file.getName().replaceAll("\\.tmx",".png");
      }

    try 
      {
      ImageIO.write(i, "png", new File(filename));
      }
    catch (java.io.IOException e) 
      {
      e.printStackTrace();
      }
  }
  
  /**
   * Adds a set of files to copy.
   * @param set a set of files to copy
   */
  public void addFileset(FileSet set)
  {
      filesets.add(set);
  }

  /** The setter for the "stendPath" attribute */
  public void setStendPath(String stendPath)
  {
      this.stendPath = stendPath;
  }
  
  /** The setter for the "stendPath" attribute */
  public void setImagePath(String imagePath)
  {
      this.imagePath = imagePath;
  }
  
  /** ants execute method. */
  public void execute() throws BuildException
  {
    try
    {
      for (FileSet fileset : filesets)
      {
        DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
        String[] includedFiles = ds.getIncludedFiles();
        
        for(String filename : includedFiles)
        {
          System.out.println(ds.getBasedir().getAbsolutePath()+File.separator+filename);
          convert(ds.getBasedir().getAbsolutePath()+File.separator+filename);
        }        
      }
    }
    catch (Exception e)
    {
      throw new BuildException(e);
    }
  }
  
  /** */
  public static void main(String[] args) throws Exception
  {
//    args = new String[] {"G:\\project\\stendhal\\tiled","c:\\temp"};
    if (args.length < 2)
    {
      System.out.println("usage: java games.stendhal.tools.MapConverter <tmx file> <path where the *.stend files goes>");
      return;
    }
    // do the job
    MapConverter converter = new MapConverter();
    converter.stendPath = args[1];
    converter.imagePath = args[2];
    converter.convert(args[0]);
  }
}
