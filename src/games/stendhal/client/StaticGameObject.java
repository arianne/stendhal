package games.stendhal.client;

import java.awt.Graphics;
import java.util.*;
import java.io.*;

public class StaticGameObject extends Entity
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
  private int screen_w,screen_h;
  
  public StaticGameObject(int screen_w, int screen_h)
    {
    super(0,0);
    layers=new LinkedList<Pair>();
    tilestore=TileStore.get("sprites/zelda_outside_chipset.gif");
    this.screen_w=screen_w;
    this.screen_h=screen_h;
    }

  public void addLayer(Reader reader, String name) throws IOException
    {
    TileRenderer renderer=new TileRenderer(tilestore);
    renderer.setScreenSize(screen_w,screen_h);
    renderer.setMapData(reader);
    
    int i=0;
    for(i=0;i<layers.size();i++)
      {
      if(layers.get(i).name.compareTo(name)>=0)
        {
        break;
        }
      }
      
    layers.add(i,new Pair(name, renderer));    
    }
    
  public void draw(Graphics g)
    {
    for(Pair p: layers)
      {
      p.renderer.render(g,(int)x,(int)y);
      }
    }
  }
