package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.util.*;

public abstract class AnimatedGameEntity extends GameEntity 
  {
  protected Map<String,Sprite[]> sprites;
  
  public AnimatedGameEntity(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }

  abstract protected void buildAnimations(String type);
  abstract protected Sprite defaultAnimation();
    
  protected void loadSprite(String type)
    {
    sprites=new HashMap<String,Sprite[]>();
    
    buildAnimations(type);
    sprite=defaultAnimation();
    }
  }
