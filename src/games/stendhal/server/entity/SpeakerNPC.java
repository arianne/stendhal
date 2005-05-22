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
    chatStop=0;
    }

  public SpeakerNPC() throws AttributeNotFoundException
    {
    super();
    chatStop=0;
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
      
  public void onDead(RPEntity who)
    {
    setHP(getbaseHP());
    world.modify(this);
    }
  
  private int chatStop;
    
  public void logic()
    {
    if(has("text")) remove("text");
    
    if(chatStop>0) chatStop--;
    
    if(chatStop==0)
      {
      move();
      if(!stopped())
        {
        StendhalRPAction.move(this);
        }
      }
    
    Player speaker=getNearestPlayerThatHasSpeaken(this,5);
    if(speaker!=null)
      {
      if(chat(speaker))
        {
        stop();
        chatStop=30;
        }
      }

    world.modify(this);
    }

  abstract protected boolean chat(Player player) throws AttributeNotFoundException;
  abstract protected boolean move();
  }
