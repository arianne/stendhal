package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;
import java.awt.geom.*;


/** A Wolf entity */
public class Wolf extends AnimatedGameEntity 
  {
  public Wolf(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }
  
  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x+0.5,y+1.3,0.87,0.6);
    }

  protected void buildAnimations(String type)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("move_up", store.getAnimatedSprite(translate(type),0,3,64,48));      
    sprites.put("move_right", store.getAnimatedSprite(translate(type),1,3,64,48));      
    sprites.put("move_down", store.getAnimatedSprite(translate(type),2,3,64,48));      
    sprites.put("move_left", store.getAnimatedSprite(translate(type),3,3,64,48));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }
  }
