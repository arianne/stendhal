package games.stendhal.server.entity;

import java.awt.*;
import java.awt.geom.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;

abstract class SpeakerNPC extends NPC 
  {
  public SpeakerNPC(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }

  public SpeakerNPC() throws AttributeNotFoundException
    {
    super();
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }  

  private Player getNearestPlayerThatHasSpeaken(NPC npc, double range)
    {
    int x=npc.getx();
    int y=npc.gety();
    
    for(Player player: rp.getPlayers())
      {
      int px=player.getx();
      int py=player.gety();
      
      if(get("zoneid").equals(player.get("zoneid")) && Math.abs(px-x)<range && Math.abs(py-y)<range && player.has("text"))
        {
        return player;
        }
      }
    
    return null;
    }
      
  public void logic()
    {
    move();
    if(!stopped())
      {
      StendhalRPAction.move(this);
      }
    
    Player speaker=getNearestPlayerThatHasSpeaken(this,5);
    if(speaker!=null)
      {
      chat(speaker);
      }

    world.modify(this);
    }

  abstract protected boolean chat(Player player) throws AttributeNotFoundException;
  abstract protected boolean move();
  }
