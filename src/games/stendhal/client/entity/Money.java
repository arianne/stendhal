package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class Money extends Item
  {
  private int quantity;
  private Sprite quantityImage;
  
  public Money(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    quantity=0;
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("quantity"))
      {
      quantity=changes.getInt("quantity");
      quantityImage=GameScreen.get().createString(Integer.toString(quantity),Color.white);
      }
    }
  
  public void draw(GameScreen screen)
    {
    super.draw(screen);    
    
    if(quantityImage!=null)
      {
      screen.draw(quantityImage,x+1,y+1);
      }
    }
  }
