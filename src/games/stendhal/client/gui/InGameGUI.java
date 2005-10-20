/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import games.stendhal.client.*;
import games.stendhal.client.entity.*;
import games.stendhal.client.gui.wt.*;
import games.stendhal.common.Direction;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;


public class InGameGUI implements MouseListener, MouseMotionListener, KeyListener
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(InGameGUI.class);

  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;

  /** this is a list of commands that is shown when right click on an entity */
  private wtList widgetCommandList;
  
  /** this is the entity to which it is associated the wtList */
  private Entity widgetAssociatedEntity;

  /** buttons to exit and return to game */
  private java.util.List<wtButton> buttons;  

  /** a list of droppable areas: gui, items, creatures,... */
  private java.util.List<wtDroppableArea> droppableAreas;  
  
  /** a nicer way of handling the keyboard */
  private Map<Integer, Object> pressed;
  
  
  /** the sprite to show the player inventory */
  private Sprite inGameInventory;
  
  /** a graphical representation of some droppable areas. */
  private Sprite slot;
  
  
  /** player can inspect entities to check what they contains. This variable stores
   *  the actual inspected entity. */
  private Entity inspectedEntity;
  /** this is the slot of the above entity that is being inspecting right now. */
  private RPSlot inspectedSlot;
  
  /** Stores droppable areas needed to represent content of the above slot. */  
  private java.util.List<wtDroppableArea> inspectedDroppableAreas;


  private void fixkeyboardHandlinginX()
    {
    logger.debug("OS: "+System.getProperty("os.name"));
    
    if(System.getProperty("os.name").toLowerCase().contains("linux"))
      {
      try
        {
        // NOTE: X does handle input in a different way of the rest of the world.
        // This fixs the problem.
        Runtime.getRuntime().exec("xset r off");
        Runtime.getRuntime().addShutdownHook(new Thread()
          {
          public void run()
            {
            try
              {
              Runtime.getRuntime().exec("xset r on");
              }
            catch(Exception e)
              {
              logger.fatal(e);
              }
            }
          });
        }
      catch(Exception e)
        {
        System.out.println (e);
        }
      }
    }
    
  public InGameGUI(StendhalClient client)
    {
    fixkeyboardHandlinginX();
    
    client.setGameGUI(this);
    this.client=client;
    
    gameObjects=client.getGameObjects();
    screen=GameScreen.get();
    
    pressed=new HashMap<Integer, Object>();
    
    buttons=new java.util.LinkedList<wtButton>();
    droppableAreas=new java.util.LinkedList<wtDroppableArea>();
    inspectedDroppableAreas=new java.util.LinkedList<wtDroppableArea>();
    
    buildGUI();
    }
  
  private void createQuitMenuButtons(SpriteStore st)
    {    
    wtButton button=new wtButton("exit",st.getSprite("data/exit.png"), st.getSprite("data/exit_pressed.png"), 320,360);
    button.addActionListener(new wtEventListener()
      {
      public void onAction(Object... param)
        {
        InGameGUI.this.client.requestLogout();
        }
      });
    button.setEnabled(false);
    buttons.add(button);
    
    button=new wtButton("back",st.getSprite("data/back.png"), st.getSprite("data/back_pressed.png"), 220,360);
    button.addActionListener(new wtEventListener()
      {
      public void onAction(Object... param)
        {
        for(wtButton button: buttons)    
          {
          if(button.getName().equals("exit") || button.getName().equals("back"))
            {
            button.setEnabled(false);
            }
          } 
        }
      });
    button.setEnabled(false);
    buttons.add(button);
    }
  
  private void createPlayerInventory(SpriteStore st)
    {
    inGameInventory=SpriteStore.get().getSprite("data/equipmentGUI.png",true);
    
    wtEventListener dropToInventory=new wtEventListener()
      {
      public void onAction(Object... param)
        {
        int playerid=client.getPlayer().getID().getObjectID();
        if(param[0] instanceof Entity) 
          {
          // From floor to inventory
          RPAction action=new RPAction();
          action.put("type","equip");
          action.put("target",((Entity)param[0]).getID().getObjectID());
          action.put("baseslot",((wtDroppableArea)param[1]).getName());
          action.put("baseobject",playerid);
          InGameGUI.this.client.send(action);
          }
        else if(param[0] instanceof wtDroppableArea)
          {
          // From another droppable area to inventory
          RPAction action=new RPAction();

          wtDroppableArea sourceSlot=((wtDroppableArea)param[0]); //left_003
          wtDroppableArea targetSlot=((wtDroppableArea)param[1]); //bag
          
          logger.info("Drop slot "+sourceSlot.getName()+" to slot "+targetSlot.getName());
          
          action.put("type","moveequip");
          action.put("targetslot",targetSlot.getName());
          action.put("targetobject",playerid);
          if(sourceSlot.getName().startsWith("left"))
            {
            // To inspected object
            action.put("baseobject",inspectedEntity.getID().getObjectID());
            action.put("baseslot","content");
            
            /** BUG: Handle case of multiple items. */
            int item=-1;
            
            String choosenarea=sourceSlot.getName();
            int itemPos=Integer.parseInt(choosenarea.substring(choosenarea.length()-1));
            
            int i=0;
            for(RPObject object: inspectedSlot)
              {
              if(i == (itemPos-1))
                {
                item=object.getID().getObjectID();
                break;
                }
              i++;
              }

            if(item>0)
              {
              action.put("item",item);              
              }
            }
          else
            {
            // To player
            action.put("baseobject",playerid);
            action.put("baseslot",sourceSlot.getName());
            }

          InGameGUI.this.client.send(action);
          }
        }
      };
    
    wtDroppableArea area=null;
    
    area=new wtDroppableArea("lhand",515,68,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);
    
//    area=new wtDroppableArea("head",558,14,32,32);
//    area.addActionListener(dropToInventory);
//    droppableAreas.add(area);
    
    area=new wtDroppableArea("armor",558,56,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);
    
//    area=new wtDroppableArea("legs",558,98,32,32);
//    area.addActionListener(dropToInventory);
//    droppableAreas.add(area);
//    
//    area=new wtDroppableArea("feet",558,141,32,32);
//    area.addActionListener(dropToInventory);
//    droppableAreas.add(area);
    
    area=new wtDroppableArea("rhand",601,68,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);    

    area=new wtDroppableArea("bag",601,122,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);        
    }

  private void createInspectedEntity(SpriteStore st)
    {
    slot=st.getSprite("data/slot.png");

    wtEventListener transferFromContainer=new wtEventListener()
      {
      public void onAction(Object... param)
        {
        if(param[0] instanceof Entity)
          {
          // Moved from floor to droppable area
          logger.info("Drop object "+param[0]+" to slot "+((wtDroppableArea)param[1]).getName());
          RPAction action=new RPAction();
          action.put("type","equip");
          action.put("target",((Entity)param[0]).getID().getObjectID());
          /* It doesn't matter which droppable area we use, the slot name is
           * content for chests and corpses. */
          action.put("baseslot","content"); 
          action.put("baseobject",inspectedEntity.getID().getObjectID());
          InGameGUI.this.client.send(action);
          }
        else if(param[0] instanceof wtDroppableArea)
          {
          // Move from other droppable area to droppable object
          wtDroppableArea sourceSlot=((wtDroppableArea)param[0]);
          wtDroppableArea targetSlot=((wtDroppableArea)param[1]);
          
          logger.info("Drop slot "+sourceSlot.getName()+" to slot "+targetSlot.getName());
          RPAction action=new RPAction();
          action.put("type","moveequip");

          /* It doesn't matter which droppable area we use, the slot name is
           * content for chests and corpses. */
          action.put("targetslot","content");          
          action.put("targetobject",inspectedEntity.getID().getObjectID());
          
          action.put("baseslot",sourceSlot.getName());
          if(sourceSlot.getName().startsWith("left"))
            {
            // To inspected object
            action.put("baseobject",inspectedEntity.getID().getObjectID());
            }
          else
            {
            // To player
            int playerid=client.getPlayer().getID().getObjectID();
            action.put("baseobject",playerid);
            }
           
          InGameGUI.this.client.send(action);

          logger.info("Action: "+action);
          }
        }
      };

    wtDroppableArea area=null;

    area=new wtDroppableArea("left_001",6,414,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    area=new wtDroppableArea("left_002",6,369,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    area=new wtDroppableArea("left_003",6,324,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    area=new wtDroppableArea("left_004",6,279,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    for(wtDroppableArea disabledArea:inspectedDroppableAreas)
      {
      disabledArea.setEnabled(false);
      }
    }
    
  private void buildGUI()
    {
    SpriteStore st=SpriteStore.get();
    
    createQuitMenuButtons(st);
    createPlayerInventory(st);
    createInspectedEntity(st);
    }
  
  private wtDroppableArea getDroppableArea(String name)
    {
    for(wtDroppableArea item: droppableAreas)
      {
      if(item.getName().equals(name))
        {
        return item;
        }     
      }
    
    return null;
    }
  
  /** keeps track of the last drag event, its position mainly, so that we can know
   *  it when the mouse button is released. */
  private MouseEvent lastDraggedEvent;
  /** To manage too the drag event we need to record what was the entity in which the
   *  mouse was pressed for the first drag event. */
  private Entity choosenEntity;
  /** There is a second case where the use drag from a droppable area to game, and so 
   *  we record from what droppable area the event started */
  private wtDroppableArea choosenWidget;
    
  public void mouseDragged(MouseEvent e) 
    {
    lastDraggedEvent=e;
    }
    
  public void mouseMoved(MouseEvent e)  
    {
    lastDraggedEvent=null;
    
    if(widgetCommandList!=null)  // Notify the wtList so it can update itself 
      {
      widgetCommandList.onMouseOver(e.getPoint());
      }
    
    for(wtButton button: buttons)  // Notify buttons so they can update itself     
      {
      button.onMouseOver(e.getPoint());
      } 
    }

  /** the user has pressed and released the mouse button */
  public void mouseClicked(MouseEvent e) 
    {
    Point2D screenPoint=e.getPoint();
    
    // check if the command list is clicked.    
    if(widgetCommandList!=null && widgetCommandList.clicked(screenPoint))     
      {
      // check that the entity still exists
      if(gameObjects.has(widgetAssociatedEntity))                             
        {
        // execute the action
        widgetAssociatedEntity.onAction(client, widgetCommandList.choosen()); 
        // clean the command list.
        widgetCommandList=null;                                               
        return;
        }
      }

    // check if someone clicked the 'quit' or 'cancel' button
    for(wtButton button: buttons)    
      {
      button.clicked(e.getPoint());
      } 
    
    /* If no command list was clicked but a click happened then delete the actual command list  */
    widgetCommandList=null;
    
    // get clicked entity
    Point2D point=screen.translate(screenPoint);
    Entity entity=gameObjects.at(point.getX(),point.getY());
    // for the clicked entity....
    if(entity!=null)
      {
      if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
        {        
        // ... do the default action
        String action=entity.defaultAction();
        entity.onAction(client, action);
        }
      else if(e.getButton()==MouseEvent.BUTTON3)
        {
        // ... show context menu (aka command list) 
        String[] actions=entity.offeredActions();
        widgetCommandList=new wtList(actions,screenPoint.getX(),screenPoint.getY());      
        widgetAssociatedEntity=entity;  
        }
      }
    }

  /** the user has pressed the mouse button */
  public void mousePressed(MouseEvent e) 
    {
    if(e.getButton()==MouseEvent.BUTTON1) 
      {        
      Point2D point=screen.translate(e.getPoint());
      choosenEntity=gameObjects.at(point.getX(),point.getY()); //check if entity exists at x,y
      
      // If there is no entity there, check if there is a droppable area.
      if(choosenEntity==null)
        {
        for(wtDroppableArea item: droppableAreas)    
          {
          if(item.onMouseOver(e.getPoint()))
            {
            choosenWidget=item;
            logger.debug("Pressed on droppable area named "+choosenWidget.getName());
            break;
            }
          } 
        }
      else
        {
        logger.debug("Pressed on entity "+choosenEntity);
        choosenWidget=null;
        }      
      }
    }
  
  /** This method request the player to displace the given entity to x,y */
  private void displace(Entity entity, int x, int y)
    {
    entity.onAction(client,"Displace", Integer.toString(x),Integer.toString(y));
    }
  
  private void drop(int x, int y)
    {
    int baseobjectid=0;
    int item=-1;
    String slot=null;
    
    if(inspectedSlot==null || !choosenWidget.getName().startsWith("left"))
      {
      /* If we are not inspecting any slot OR if the choosen widget from where we
       * are going to drop the object doesn't start with left ( that are the droppable 
       * areas used for inspecting objects ) that means we are dropping the item from
       * one of our player slots */         
      baseobjectid=client.getPlayer().getID().getObjectID();
      slot=choosenWidget.getName();
      logger.debug("Drop from player's slot "+slot);
      }
    else
      {
      /* On the other hand, if we are inspecting an entity and the slot starts with left
       * that means that we want to drop the item from the entity that we are inspecting. */
      baseobjectid=inspectedEntity.getID().getObjectID();
      slot=inspectedSlot.getName();
      
      String choosenarea=choosenWidget.getName();
      int itemPos=Integer.parseInt(choosenarea.substring(choosenarea.length()-1));
      
      int i=0;
      for(RPObject object: inspectedSlot)
        {
        if(i == (itemPos-1))
          {
          item=object.getID().getObjectID();
          break;
          }
        i++;
        }
      }
    
    RPAction action=new RPAction();
    action.put("type","drop");
    action.put("baseobject",baseobjectid);
    action.put("baseslot",slot);
    action.put("item",item);
    action.put("x",x);
    action.put("y",y);
    
    InGameGUI.this.client.send(action);
    }

  /** the user has released the mouse button */
  public void mouseReleased(MouseEvent e) 
    {
    // Check if we were dragging an entity.
    if(lastDraggedEvent!=null && choosenEntity!=null) 
      {
      Point2D point=screen.translate(e.getPoint());

      // We check first inventory and if it fails we wanted to move the object so. 
      for(wtDroppableArea item: droppableAreas)    
        {
        // Returns true if it is released inside the droppable area
        if(item.released(e.getPoint(),choosenEntity))
          {
          // We dropped it in inventory
          logger.debug("Dropped "+choosenEntity+" into "+item.getName());
          choosenEntity=null;
          lastDraggedEvent=null;
          return;
          }
        }

      logger.debug("Moved "+choosenEntity+" to "+point);
      displace(choosenEntity,(int)point.getX(), (int)point.getY());
      
      choosenEntity=null;
      lastDraggedEvent=null;
      }

    // Check if we were dragging a widget ( droppable areas only ) 
    if(lastDraggedEvent!=null && choosenWidget!=null)
      {
      Point2D point=screen.translate(e.getPoint());

      // We check first inventory and if it fails we wanted to move the object so. 
      for(wtDroppableArea item: droppableAreas)    
        {
        // Returns true if it is released inside the droppable area
        if(item.released(e.getPoint(),choosenWidget))
          {
          // We dropped it in inventory
          System.out.println ("Moved from "+choosenWidget.getName()+" into "+item.getName());
          choosenWidget=null;
          lastDraggedEvent=null;
          return;
          }
        }
      
      logger.debug(choosenWidget.getName()+" dropped to "+point);
      drop((int)point.getX(),(int)point.getY());

      choosenWidget=null;
      lastDraggedEvent=null;
      }
    }

  public void mouseEntered(MouseEvent e) 
    {
    }

  public void mouseExited(MouseEvent e) 
    {
    }    

  public void onKeyPressed(KeyEvent e)  
    {
    RPAction action;
    
    if(e.isShiftDown())
      {
      /* We are going to use shift to move to previous/next line of text with arrows
       * so we just ignore the keys if shift is pressed. */
      return;
      }
      
    if(e.getKeyCode()==KeyEvent.VK_L && e.isControlDown())
      {
      /* Ifwe Ctrl+L we set the Game log dialog visible */
      client.getGameLogDialog().setVisible(true);
      }
    else if(e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_DOWN)
      {
      action=new RPAction();
      if(e.isControlDown())
        {
        // We use Ctrl+arrow to face
        action.put("type","face");
        }
      else
        {
        // While arrow only moves the player
        action.put("type","move");
        }
      
      switch(e.getKeyCode())
        {
        case KeyEvent.VK_LEFT:
          action.put("dir",Direction.LEFT.get());
          break;
        case KeyEvent.VK_RIGHT:
          action.put("dir",Direction.RIGHT.get());
          break;
        case KeyEvent.VK_UP:
          action.put("dir",Direction.UP.get());
          break;
        case KeyEvent.VK_DOWN:
          action.put("dir",Direction.DOWN.get());
          break;
        }
      
      client.send(action);
      }
    }
    
  public void onKeyReleased(KeyEvent e)  
    {
    RPAction action=new RPAction();
    action.put("type","move");

    switch(e.getKeyCode())
      {
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:   
        // Notify server that player is stopped.
        int keys=(pressed.containsKey(KeyEvent.VK_LEFT)?1:0)+(pressed.containsKey(KeyEvent.VK_RIGHT)?1:0)+(pressed.containsKey(KeyEvent.VK_UP)?1:0)+(pressed.containsKey(KeyEvent.VK_DOWN)?1:0);   
        if(keys==1)
          {
          action.put("dir",Direction.STOP.get());
          client.send(action);
          }
        break;
      }
    }
    
  public void keyPressed(KeyEvent e) 
    {
    widgetCommandList=null;
    
    if(!pressed.containsKey(new Integer(e.getKeyCode())))
      {
      onKeyPressed(e);
      pressed.put(new Integer(e.getKeyCode()),null);
      }      
    }
      
  public void keyReleased(KeyEvent e) 
    {
    onKeyReleased(e);
    pressed.remove(new Integer(e.getKeyCode()));
    }

  public void keyTyped(KeyEvent e) 
    {
    if (e.getKeyChar() == 27) 
      {
      // On ESC key we stop the player and 
      RPAction rpaction=new RPAction();
      rpaction.put("type","stop");
      client.send(rpaction);

      for(wtButton button: buttons)    
        {
        if(button.getName().equals("exit") || button.getName().equals("back"))
          {
          button.setEnabled(true);
          }
        } 
      }
    }
  
  /** This methods inspects an entity by enabling all the droppable areas.
   * To stop inspecting this method is called with entity=null */
  public void inspect(Entity entity, RPSlot slot)
    {
    for(wtDroppableArea area:inspectedDroppableAreas)
      {
      area.setEnabled(entity!=null);
      }
      
    inspectedEntity=entity;
    inspectedSlot=slot;
    }

  /** Returns true if the given object is being inspected */
  public boolean isInspecting(Entity entity, String slot)
    {
    if(inspectedEntity==null || inspectedSlot==null)
      {
      return false;
      }
      
    if(inspectedEntity.getID().equals(entity.getID()) && inspectedSlot.getName().equals(slot))
      {
      return true;
      }    
    else
      {
      return false;
      }
    }

  public void draw(GameScreen screen)
    {
    // Draw the inventory graphics 
    screen.drawInScreen(inGameInventory,510,10);

    // Draw the inventory's droppable areas
    for(wtDroppableArea item: droppableAreas)    
      {
      item.draw(screen);
      } 
    
    RPObject player=client.getPlayer();
    if(player!=null)
      {
      // For each of the existing slots we draw the item in it.
      String[] slots=new String[]{"lhand","rhand","armor","bag"};
      for(String slotName: slots)
       {
       if(player.hasSlot(slotName))
         {
         RPSlot slot=player.getSlot(slotName);
         if(slot.size()>0)
           {
           // BUG: Only draws one object... we need a new way of drawing slots.
           wtDroppableArea dropArea=getDroppableArea(slotName);
           RPObject object=slot.iterator().next();
           screen.drawInScreen(gameObjects.spriteType(object),dropArea.getx(),dropArea.gety());
           }
         }
       }

      // Write player info in screen
      screen.drawInScreen(screen.createString("HP : "+player.get("hp")+"/"+player.get("base_hp"),Color.white),550, 184);
      screen.drawInScreen(screen.createString("ATK: "+player.get("atk")+" ("+player.get("atk_xp")+")",Color.white),550, 204);
      screen.drawInScreen(screen.createString("DEF: "+player.get("def")+" ("+player.get("def_xp")+")",Color.white),550, 224);
      screen.drawInScreen(screen.createString("XP : "+player.get("xp"),Color.white),550, 244);
      
      if(inspectedSlot!=null)
        {
        /* If we are inspecting an object, we draw all the objects that the 
         * inspected object's slot contains. */
        int i=1;
        for(RPObject object: inspectedSlot)
          {
          // BUG: Only draws upto existing areas... we can have a problem with this...
          wtDroppableArea area=getDroppableArea("left_00"+i);                    
          screen.drawInScreen(slot,area.getx()-4,area.gety()-4);
          screen.drawInScreen(gameObjects.spriteType(object),area.getx(),area.gety());
          i++;
          }
        
        /* If we are far from the inspected object, we stop inspecting it. */
        if(inspectedEntity.distance(player)>2.5*2.5)
          {
          for(wtDroppableArea area:inspectedDroppableAreas)
            {
            area.setEnabled(false);
            }
            
          inspectedEntity=null;
          inspectedSlot=null;
          }
        }
      }

    // Draw any relevant button if needed.    
    for(wtButton button: buttons)
      {
      button.draw(screen);
      }
    
    // If there is a context menu (aka Command list) open, draw it 
    if(widgetCommandList!=null)
      {
      widgetCommandList.draw(screen);
      }

    // if the currently dragged item is a passive entity (items, corpses)
    // show it as a mouse cursor. Note: sign are also passive entities
    if (choosenEntity != null && lastDraggedEvent != null && choosenEntity instanceof PassiveEntity)
      {
      Point2D p = lastDraggedEvent.getPoint();
      screen.drawInScreen(choosenEntity.getSprite(), (int) p.getX(), (int) p.getY());
      }    
    }
  }
