package games.stendhal.client;

import marauroa.common.game.*;
import java.awt.Graphics;

public class GameEntity extends Entity  
  {
  private RPObject.ID id;
  private String type;
  
  private Sprite sprite;
  
  private static String translate(String type)
    {
    return "sprites/"+type+".gif";
    }

  public GameEntity(RPObject object) throws AttributeNotFoundException
    {
    super(0,0);
    SpriteStore store=SpriteStore.get();    
    
    modify(object);
    
    id=object.getID();
    type=object.get("type");    
    
    sprite=store.getSprite(translate(type));
    }
  
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    x=object.getInt("x");
    y=object.getInt("y");
    }
  
  public RPObject.ID getID()
    {
    return id;
    }
    
  public void doLogic()
    {
    }

  public void draw(GameScreen screen)
    {
    screen.draw(sprite,x,y);
    }
  }
