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

/** This class stores the layers that make the floor and the buildings */
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
  
  /** List of pair name, layer */
  private LinkedList<Pair> layers;
  /** Tilestore contains the tiles to draw */
  private TileStore tilestore;
  /** Name of the layers set that we are rendering right now */
  private String area;
    
  public StaticGameLayers()
    {
    layers=new LinkedList<Pair>();
    tilestore=TileStore.get("sprites/zelda_outside_chipset.gif");
    area=null;
    }
  
  /** Returns width in world units */
  public double getWidth()
    {
    double width=0;
    
    for(Pair p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        if(width<p.renderer.getWidth())
          {
          width=p.renderer.getWidth();
          }
        }
      }

    return width;
    }

  /** Returns the height in world units */
  public double getHeight()
    {
    double height=0;
    
    for(Pair p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        if(height<p.renderer.getHeight())
          {
          height=p.renderer.getHeight();
          }
        }
      }

    return height;
    }

  /** Add a new Layer to the set */
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
  
  /** Removes all layers */
  public void clear()
    {
    layers.clear();
    }
  
  /** Set the set of layers that is going to be rendered */
  public void setRPZoneLayersSet(String area)
    {
    this.area=area;
    }
    
  /** Render the choosen set of layers */
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
