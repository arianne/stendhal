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
package games.stendhal.client;

import java.awt.Graphics;
import java.util.*;
import java.io.*;

public class StaticGameLayers
  {
  static private class Pair
    {
    public String name;
    public TileRenderer renderer;
    
    Pair(String name, TileRenderer renderer)
      {
      this.name=name;
      this.renderer=renderer;
      }
    }
    
  private LinkedList<Pair> layers;
  private TileStore tilestore;
  private String area;
  
  public StaticGameLayers()
    {
    layers=new LinkedList<Pair>();
    tilestore=TileStore.get("sprites/zelda_outside_chipset.gif");
    area=null;
    }
  
  public double getWidth()
    {
    if(layers.size()>0)
      {
      return layers.get(0).renderer.getWidth();
      }
    else
      {
      return 0;
      }
    }

  public double getHeight()
    {
    if(layers.size()>0)
      {
      return layers.get(0).renderer.getHeight();
      }
    else
      {
      return 0;
      }
    }

  public void addLayer(Reader reader, String name) throws IOException
    {
    TileRenderer renderer=new TileRenderer(tilestore);
    renderer.setMapData(reader);

    int i;
    for( i=0;i<layers.size();i++)
      {
      if(layers.get(i).name.compareTo(name)==0)
        {
        /** Repeated layers should be ignored. */
        return;
        }
        
      if(layers.get(i).name.compareTo(name)>=0)
        {
        break;
        }
      }
      
    layers.add(i,new Pair(name, renderer));    
    }
  
  public void clear()
    {
    layers.clear();
    }
  
  public void setRPZoneLayersSet(String area)
    {
    this.area=area;
    }
    
  public void draw(GameScreen screen)
    {
    for(Pair p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        p.renderer.draw(screen);
        }
      }
    }
  }
