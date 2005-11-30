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
 * ImageMapConverter.java
 *
 * Created on 13. Oktober 2005, 18:24
 *
 */

package games.stendhal.tools;

import games.stendhal.tools.tiled.StendhalMapWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.view.MapView;
import tiled.io.xml.XMLMapTransformer;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.util.ListIterator;

/**
 * Converts the stendhal maps from *.tmx to *.stend
 * This class can be started from the command line or through an ant task
 *
 * @author mtotz
 */
public class ImageMapConverter extends Task
{
  /** path where the *.stend goes */
  private String stendPath;
  /** list of *.tmx files to convert */
  private List<FileSet> filesets = new ArrayList<FileSet>();
  
  /** Creates a new instance of ImageMapConverter */
  public ImageMapConverter()
  {}
  
  /** converts the map files */
  public void convert(String tmxFile) throws Exception
  {
    File file = new File(tmxFile);

    String filename = file.getAbsolutePath();
    // some internal tiled magic: load the map 
    Map map = new XMLMapTransformer().readMap(filename);
    
    ListIterator it= map.getLayers();
    
    while(it.hasNext())
      {
      MapLayer layer=(MapLayer)it.next();
      if(layer.getName().equals("navigation") || layer.getName().equals("collision") || layer.getName().equals("objects"))
        {
        layer.setVisible(false);
        }
      }

    MapView myView = MapView.createViewforMap(map);
    myView.enableMode(MapView.PF_NOSPECIAL);
    myView.setZoom(0.125);
    Dimension d = myView.getPreferredSize();
    BufferedImage i = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = i.createGraphics();
    g.setClip(0, 0, d.width, d.height);
    myView.paint(g);
    
    String geolocation=null;
    
    filename=file.getParentFile().getName();
    if(filename.contains("Level "))
      {
      geolocation=filename.split("Level ")[1]+"_";
      geolocation=geolocation.replace("-","sub_");
      }
    else
      {
      geolocation="int_";
      }

    filename = stendPath+"\\"+geolocation+file.getName().replaceAll("\\.tmx",".png");

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
      e.printStackTrace();
      throw new BuildException(e);
    }
  }
  
  /** */
  public static void main(String[] args) throws Exception
  {
//    args = new String[] {"G:\\project\\stendhal\\tiled","c:\\temp"};
    if (args.length < 2)
    {
      System.out.println("usage: java games.stendhal.tools.ImageMapConverter <tmx file> <path where the *.png files goes>");
      return;
    }
    // do the job
    ImageMapConverter converter = new ImageMapConverter();
    converter.stendPath = args[1];
    converter.convert(args[0]);
  }
}
