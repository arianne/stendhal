package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;

/** A Wolf entity */
public class Wolf extends AnimatedGameEntity 
  {
  public Wolf(RPObject object) throws AttributeNotFoundException
    {
    super(object);
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
