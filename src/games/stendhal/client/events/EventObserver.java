package games.stendhal.client.events;

import games.stendhal.client.entity.*;
import marauroa.common.game.*;
import java.util.List;
import java.util.LinkedList;

public class EventObserver 
  {  
  private List<AttackEvent> listenersAttackEvent;
  
  public EventObserver()
    {
    listenersAttackEvent=new LinkedList<AttackEvent>();
    }

  public void fireAdded(RPObject target)
    {
    // Code here all the logic for attack
    }

  public void fireRemoved(RPObject target)
    {
    // Code here all the logic for attack
    }

  public void fireChanged(RPObject object, RPObject changes)
    {
    // Code here all the logic for attack
    }
  }
