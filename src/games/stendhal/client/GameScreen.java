package games.stendhal.client;

import java.awt.image.BufferStrategy;
import java.awt.Graphics2D;
import java.awt.Color;

public class GameScreen 
  {
  // One unit are 32 pixels 
  public final static int PIXEL_SCALE=32;
  
  private BufferStrategy strategy;
  private Graphics2D g;
  private double x,y;
  private double dx,dy;
  private int sw,sh;  
  private static GameScreen screen;
  
  public static void createScreen(BufferStrategy strategy, int sw, int sh)
    {
    if(screen==null)
      {
      screen=new GameScreen(strategy,sw,sh);
      }
    }
  
  public static GameScreen get()
    {
    return screen;
    }
  
  private GameScreen(BufferStrategy strategy, int sw, int sh)
    {
    this.strategy=strategy;
    this.sw=sw;
    this.sh=sh;
    x=y=0;
    dx=dy=0;
    g=(Graphics2D)strategy.getDrawGraphics();
    }
  
  public void nextFrame()
    {
    g.dispose();
    strategy.show();
    
    g=(Graphics2D)strategy.getDrawGraphics();
    
    x+=dx;
    y+=dy;
    }
  
  public Graphics2D expose()
    {
    return g;
    }
  
  public void move(double dx, double dy)
    {
    this.dx=dx;
    this.dy=dy;
    }
    
  public void draw(Sprite sprite, double wx, double wy)
    {
    int sx=(int)((wx-x)*32);
    int sy=(int)((wy-y)*32);
    
    if(sx>=-64 && sx<sw+64 && sy>=-64 && sy<sh+64)
      {
      sprite.draw(g,sx,sy);
      }
    }
  }
