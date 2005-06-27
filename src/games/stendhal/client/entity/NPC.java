package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;
import java.awt.geom.*;

public class NPC extends Speaker
  {
  private Sprite ideaImage;

  private static Sprite eat;
  private static Sprite food;
  private static Sprite walk;
  private static Sprite follow;
  
  static
    {
    SpriteStore st=SpriteStore.get();
    
    eat=st.getSprite("sprites/ideas/eat.gif");
    food=st.getSprite("sprites/ideas/food.gif");
    walk=st.getSprite("sprites/ideas/walk.gif");
    follow=st.getSprite("sprites/ideas/follow.gif");
    }
    
  public NPC(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects,object);
    }

  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();

    sprites.put("move_up", store.getAnimatedSprite(translate(object.get("class")),0,4,32,64));
    sprites.put("move_right", store.getAnimatedSprite(translate(object.get("class")),1,4,32,64));
    sprites.put("move_down", store.getAnimatedSprite(translate(object.get("class")),2,4,32,64));
    sprites.put("move_left", store.getAnimatedSprite(translate(object.get("class")),3,4,32,64));

    sprites.get("move_up")[3]=sprites.get("move_up")[1];
    sprites.get("move_right")[3]=sprites.get("move_right")[1];
    sprites.get("move_down")[3]=sprites.get("move_down")[1];
    sprites.get("move_left")[3]=sprites.get("move_left")[1];
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("idea"))
      {
      String idea=changes.get("idea");
      if(idea.equals("eat"))
        {
        ideaImage=eat;
        }
      else if(idea.equals("food"))
        {
        ideaImage=food;
        }
      else if(idea.equals("walk"))
        {
        ideaImage=walk;
        }
      else if(idea.equals("follow"))
        {
        ideaImage=follow;
        }
      }
    }

  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y+1,1,1);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,2);
    }  
    
  public void draw(GameScreen screen)
    {
    super.draw(screen);
    
    if(ideaImage!=null)
      {
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getY();
      screen.draw(ideaImage,sx-0.25,sy-0.25);
      }
    }
  }
