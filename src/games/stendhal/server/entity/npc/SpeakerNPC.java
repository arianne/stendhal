package games.stendhal.server.entity.npc;

import java.util.List;
import java.util.LinkedList;
//import java.awt.*;
import java.awt.geom.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;
import games.stendhal.common.*;

import games.stendhal.server.entity.*;

public abstract class SpeakerNPC extends NPC 
  {
  // FSA state table
  private List<StatePath> statesTable;
  // FSA actual state
  private int actualState;
  // Default wait message when NPC is busy
  private String waitMessage;
  // Default wait action when NPC is busy
  private ChatAction waitAction;
  
  // Timeout control value
  private long lastMessageTurn;
  private static long TIMEOUT_PLAYER_CHAT=100; // 30 seconds at 300ms.
  
  // Attended players
  private Player attending;
    
  
  public SpeakerNPC() throws AttributeNotFoundException
    {
    super();
    createPath();
    
    statesTable=new LinkedList<StatePath>();
    actualState=0;
    lastMessageTurn=0;
    
    createDialog();
    }

  abstract protected void createPath();
  abstract protected void createDialog();

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }  

  private List<Player> getNearestPlayersThatHasSpeaken(NPC npc, double range)
    {
    int x=npc.getx();
    int y=npc.gety();
    
    List<Player> players=new LinkedList<Player>();
    
    for(Player player: rp.getPlayers())
      {
      int px=player.getx();
      int py=player.gety();
      
      if(get("zoneid").equals(player.get("zoneid")) && Math.abs(px-x)<range && Math.abs(py-y)<range && player.has("text"))
        {
        players.add(player);
        }
      }
    
    return players;
    }
      
  public void onDead(RPEntity who)
    {
    setHP(getBaseHP());
    world.modify(this);
    }
   
  public void logic()
    {
    if(has("text")) remove("text");
    
    if(actualState==0)
      {
      Path.followPath(this,0.2);
      StendhalRPAction.move(this);
      }
    
    List<Player> speakers=getNearestPlayersThatHasSpeaken(this,5);
    for(Player speaker: speakers)
      {
      tell(speaker, speaker.get("text"));
      }

    world.modify(this);
    }

  abstract public class ChatAction
    {    
    abstract public void fire(Player player, String text, SpeakerNPC engine);
    }
    
  class StatePath
    {
    public int state;
    public int nextState;
    public String trigger;
    public String reply;
    public ChatAction action;
    
    StatePath(int state, String trigger, int nextState, String reply, ChatAction action)
      {
      this.state=state;
      this.nextState=nextState;
      this.trigger=trigger.toLowerCase();
      this.reply=reply;
      this.action=action;
      }

    public boolean absoluteJump(int state, String text)
      {
      if(this.state==-1 && trigger.equalsIgnoreCase(text))
        {
        return true;
        }
      
      return false;
      }

    public boolean equals(int state, String text)
      {
      if(state==this.state && trigger.equalsIgnoreCase(text))
        {
        return true;
        }
      
      return false;
      }

    public boolean contains(int state, String text)
      {
      text=text.toLowerCase();
      if(state==this.state && text.startsWith(trigger))
        {
        return true;
        }
      
      return false;
      }
      
    public String toString()
      {
      return "["+state+","+trigger+","+nextState+"]";
      }
    }
    
  /** Message when NPC is attending another player. */
  public void addWaitMessage(String trigger, ChatAction action)
    {
    waitMessage=trigger;
    waitAction=action;
    }

  /** Add a new state to FSA */
  public void add(int state, String trigger, int next_state, String reply, ChatAction action)
    {
    StatePath item=new StatePath(state, trigger,next_state, reply, action);
    statesTable.add(item);
    }

  /** Add a new set of states to FSA */
  public void add(Integer[] states, String trigger, int next_state, String reply, ChatAction action)
    {
    for(int state: states)
      {
      StatePath item=new StatePath(state, trigger,next_state, reply, action);
      statesTable.add(item);
      }
    }

  /** Add a new set of states to FSA */
  public void add(int state, String[] triggers, int next_state, String reply, ChatAction action)
    {
    for(String trigger: triggers)
      {
      StatePath item=new StatePath(state, trigger,next_state, reply, action);
      statesTable.add(item);
      }
    }
  
  /** This function evolves the FSA */
  private boolean tell(Player player, String text)
    {
    // If we are no attening a player attend, this one.
    if(actualState==0)
      {
      attending=player;
      }
    
    // If the attended player got idle, attend this one.
    if(System.currentTimeMillis()-lastMessageTurn>TIMEOUT_PLAYER_CHAT)
      {
      attending=player;
      actualState=0;
      }
    
    // If we are attending another player make this one waits.
    if(!attending.equals(player))
      {
      if(waitMessage!=null)
        {
        say(waitMessage);
        }
        
      if(waitAction!=null)
        {
        waitAction.fire(player,text,this);
        }
       
      return true;
      }
      
    lastMessageTurn=System.currentTimeMillis();
    
    // First we try to match with stateless states.
    for(StatePath state: statesTable)
      {
      if(state.absoluteJump(actualState,text))
        {
        executeState(player,text,state);
        return true;        
        }
      }

    // Now we try to match with the exact trigger string 
    for(StatePath state: statesTable)
      {
      if(state.equals(actualState,text))
        {
        executeState(player,text,state);
        return true;        
        }
      }

    // Finally we try to match with any string that starts with trigger
    for(StatePath state: statesTable)
      {
      if(state.contains(actualState,text))
        {
        executeState(player,text,state);
        return true;        
        }
      }

    // Couldn't match the text with the current FSA state
    return false;
    }
  
  private void executeState(Player player, String text, StatePath state)
    {
    int nextState=state.nextState;
    if(state.reply!=null)
      {
      say(state.reply);
      }

    if(state.action!=null)
      {
      state.action.fire(player,text,this);
      }

    actualState=nextState;
    }
  }
