package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;
import java.awt.geom.*;

public class NPC extends RPEntity
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
