package games.stendhal.server.entity;

import marauroa.common.game.*;

public class PersonalChest extends Chest
  {
  public PersonalChest() throws AttributeNotFoundException
    {
    super();
    }

  public void onUsed(RPEntity user)
    {
    Player player=(Player)user;    
    
    if(player.nextto(this,0.25))
      {
      if(isOpen())
        {
        System.out.println ("Closing");
        RPSlot slot=getSlot("content");
        RPSlot copy=(RPSlot)slot.clone();
        
        copy.setName("bank");

        player.removeSlot("bank");
        player.addSlot(copy);
        
        close();
        }
      else
        {
        System.out.println ("Opening");
        RPSlot slot=player.getSlot("bank");        
        RPSlot copy=(RPSlot)slot.clone();
        
        copy.setName("content");
        
        removeSlot("content");
        addSlot(copy);        
        
        open();
        }
      
      System.out.println (this);
      
      world.modify(this);
      }
    }
  }
