package games.stendhal.client.events;

import games.stendhal.client.entity.*;
import java.util.List;
import java.util.LinkedList;

public class EventObserver 
  {  
  private List<AttackEvent> listenersAttackEvent;
  
  public EventObserver()
    {
    listenersAttackEvent=new LinkedList<AttackEvent>();
    }
  
  public void joinAttackEvent(AttackEvent e)
    {
    listenersAttackEvent.add(e);
    }
  
  public void leaveAttackEvent(AttackEvent e)
    {
    listenersAttackEvent.remove(e);
    }
  
  // When this entity attacks target.
  public void fireOnAttackEvent(RPEntity target)
    {
    // Code here all the logic for attack
    }
  }
