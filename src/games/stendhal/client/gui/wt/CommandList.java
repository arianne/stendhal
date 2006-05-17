/**
 * 
 */
package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.wt.core.WtList;

/**
 * This is the command list of any entities
 * 
 * @author mtotz
 */
public class CommandList extends WtList
{
  /** the entity associated with the command list */
  private Entity entity;
  /** the client */
  private StendhalClient client;
  
  /** This flag will be true of the object is contained inside another one */
  private boolean contained;
  /** In case the item is contained the base object that contain it */
  private String baseObject;
  /** In case the item is contained the slot of the base object that contains it */
  private String baseSlot;

  /** creates a new CommandList */
  public CommandList(String name, String[] items, int x, int y, int width, int maxHeight, StendhalClient client, Entity entity)
  {
    super(name,items,x,y,width,maxHeight);
    this.entity = entity;
    this.client = client;
    this.contained=false;
  }
  
  public void setContext(int baseObject, String baseSlot)
  {
    this.baseObject=Integer.toString(baseObject);
    this.baseSlot=baseSlot;
    this.contained=true;
  }
  
  /** an action has been chosen */
  public void onClick(String name, boolean pressed)
  {
    // tell the entity what happened
    if(contained)
      {
      entity.onAction(client,name, baseObject, baseSlot);
      }
    else
      {
      entity.onAction(client,name);
      }
    // the base class takes care of the rest
    super.onClick(name,pressed);
  }
  

}
