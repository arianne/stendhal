/**
 * 
 */
package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.wt.core.List;

/**
 * This is the command list of any entities
 * 
 * @author mtotz
 */
public class CommandList extends List
{
  /** the entity associated with the command list */
  private Entity entity;
  /** the client */
  private StendhalClient client;

  /** creates a new CommandList */
  public CommandList(String name, String[] items, int x, int y, int width, int maxHeight, StendhalClient client, Entity entity)
  {
    super(name,items,x,y,width,maxHeight);
    this.entity = entity;
    this.client = client;
  }
  
  /** an action has been chosen */
  public void onClick(String name, boolean pressed)
  {
    // tell the entity what happened
    entity.onAction(client,name);
    // the base class takes care of the rest
    super.onClick(name,pressed);
  }
  

}
