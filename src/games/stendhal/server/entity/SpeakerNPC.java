package games.stendhal.server.entity;

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

  private Player getNearestPlayerThatHasSpeaken(NPC npc, double range)
    {
    double x=npc.getx();
    double y=npc.gety();
    
    for(Player player: rp.getPlayers())
      {
      double px=player.getx();
      double py=player.gety();
      
      if(Math.abs(px-x)<range && Math.abs(py-y)<range && player.has("text"))
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
    if(speaker!=null && chat(speaker))
      {
      world.modify(this);
      }
    }

  abstract protected boolean chat(Player player) throws AttributeNotFoundException;
  abstract protected boolean move();
  }
