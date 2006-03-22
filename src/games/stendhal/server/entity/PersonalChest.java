package games.stendhal.server.entity;

import marauroa.common.game.*;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptCondition;
import games.stendhal.server.StendhalScriptSystem;

public class PersonalChest extends Chest
  {
  private Player attending;
  private IRPZone zone;
  private PersonalChest outer;
  
  public PersonalChest() throws AttributeNotFoundException
    {
    super();
    outer=this;

    attending=null;    

    /** Add a script to copy automatically. */
    StendhalScriptSystem scripts=StendhalScriptSystem.get();
    scripts.addScript(null,new ScriptAction()
      {
      public void fire()
        {
        if(attending!=null)
          {
          /* Can be replace when we add Equip event */
          /* Mirror player objects */
          RPSlot content=attending.getSlot("bank");
          content.clear();
          
          for(RPObject item: getSlot("content"))
            {
            content.add(item);
            }
          
          /* If player is not next to depot clean it. */
          if(!nextto(attending,0.25) || !zone.has(attending.getID()))
            {
            attending=null;
            content=getSlot("content");
            content.clear();            
            close();
            world.modify(outer);
            }
          }
        }
      });
    }

  public void onUsed(RPEntity user)
    {
    Player player=(Player)user;    

    zone=world.getRPZone(player.getID());    
    
    if(player.nextto(this,0.25))
      {
      if(isOpen())
        {
        close();
        }
      else
        {
        attending=player;

        RPSlot content=getSlot("content");
        content.clear();
        
        for(RPObject item: player.getSlot("bank"))
          {
          content.add(item);
          }
        
        open();
        }
      
      world.modify(this);
      }
    }
  }
