package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;

public class Wolf extends AnimatedGameEntity 
  {
  private String animation;
  private int frame;
  private long delta;
  private boolean stopped;
  
  public Wolf(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    delta=System.currentTimeMillis();
    frame=0;
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
  

  public void modify(RPObject object) throws AttributeNotFoundException
    {
    super.modify(object);
    
    stopped=(dx==0 && dy==0);
    
    if((dx!=0 || dy!=0))
      {
      if(dx>0)
        {
        animation="move_right";
        }
      else if(dx<0)
        {
        animation="move_left";
        }
        
      if(dy>0)
        {
        animation="move_down";
        }
      else if(dy<0)
        {
        animation="move_up";
        }
      }
    }    

  private Sprite nextFrame()
    {
    Sprite[] anim=sprites.get(animation);

    if(frame==anim.length)
      {
      frame=0;
      }
    
    Sprite sprite=anim[frame];
    
    if(!stopped)
      {
      frame++;
      }
    
    return sprite;
    }

  public void draw(GameScreen screen)
    {
    if(System.currentTimeMillis()-delta>100)
      {
      delta=System.currentTimeMillis();
      sprite=nextFrame();
      }
    
    super.draw(screen);
    }
  }
