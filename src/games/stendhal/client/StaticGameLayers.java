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
  
  public StaticGameLayers()
    {
    layers=new LinkedList<Pair>();
    tilestore=TileStore.get("sprites/zelda_outside_chipset.gif");
    }

  public void addLayer(Reader reader, String name) throws IOException
    {
    TileRenderer renderer=new TileRenderer(tilestore);
    renderer.setMapData(reader);

    int i;
    for( i=0;i<layers.size();i++)
      {
      if(layers.get(i).name.compareTo(name)>=0)
        {
        break;
        }
      }
      
    layers.add(i,new Pair(name, renderer));    
    }
    
  public void draw(GameScreen screen)
    {
    for(Pair p: layers)
      {
      p.renderer.draw(screen);
      }
    }
  }
