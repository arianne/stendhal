package games.stendhal.client;

import marauroa.common.game.*;
import java.awt.Graphics;

public class GameEntity implements IEntity  
  {
  private double x, y;
  private double dx, dy;
  
  private RPObject.ID id;
  private String type;
  
  private Sprite sprite;
  
  private static String translate(String type)
    {
    return "sprites"+type+".gif";
    }

  public GameEntity(RPObject object) throws AttributeNotFoundException
    {
    SpriteStore store=SpriteStore.get();
    
    id=object.getID();
    type=object.get("type");    
    x=object.getInt("x");
    y=object.getInt("y");
    sprite=store.getSprite(translate(type));
    }
    
  public void move(long delta)
    {
    x += (delta * dx) / 1000.0;
    y += (delta * dy) / 1000.0;    
    }
    
  public void setHorizontalMovement(double dx)
    {
    this.dx=dx;
    }
    
  public void setVerticalMovement(double dy)
    {
    this.dy=dy;
    }
    
  public double getHorizontalMovement()
    {
    return dx;
    }
    
  public double getVerticalMovement()
    {
    return dy;
    }
    

  public void doLogic()
    {
    }
    

  public void setX(double x)
    {
    this.x=x;
    }
    
  public void setY(double y)
    {
    this.y=y;
    }
    
  public int getX()
    {
    return (int)x;
    }
    
  public int getY()
    {
    return (int)y;
    }

  public void draw(Graphics g)
    {
    sprite.draw(g,(int)x,(int)y);
    }
  }
