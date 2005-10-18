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

  private wtList widget;
  private java.util.List<wtButton> buttons;  
  private java.util.List<wtDroppableArea> droppableAreas;
  private Entity widgetAssociatedEntity;
  
  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;

  private Map<Integer, Object> pressed;
  
  private Sprite inGameInventory;
  private Sprite inGameDevelPoint;
  private Sprite slot;
  
  private RPSlot inspectedSlot;
  private java.util.List<wtDroppableArea> inspectedDroppableAreas;
  private Entity inspectedEntity;

  public InGameGUI(StendhalClient client)
    {
    logger.debug("OS: "+System.getProperty("os.name"));
    client.setGameGUI(this);
    
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
              System.out.println (e);
              }
            }
          });
        }
      catch(Exception e)
        {
        System.out.println (e);
        }
      }
      
    this.client=client;
    gameObjects=client.getGameObjects();
    screen=GameScreen.get();
    
    pressed=new HashMap<Integer, Object>();
    
    buttons=new java.util.LinkedList<wtButton>();
    droppableAreas=new java.util.LinkedList<wtDroppableArea>();
    inspectedDroppableAreas=new java.util.LinkedList<wtDroppableArea>();
    
    buildGUI();
    }
  
  private void buildGUI()
    {
    SpriteStore st=SpriteStore.get();

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
    
    /** Inventory */
    inGameInventory=SpriteStore.get().getSprite("data/equipmentGUI.png",true);
    
    wtEventListener dropToInventory=new wtEventListener()
      {
      public void onAction(Object... param)
        {
        int playerid=client.getPlayer().getID().getObjectID();
        if(param[0] instanceof Entity) 
          {
          RPAction action=new RPAction();
          action.put("type","equip");
          action.put("target",((Entity)param[0]).getID().getObjectID());
          action.put("slot",((wtDroppableArea)param[1]).getName());
          action.put("baseobject",playerid);
          InGameGUI.this.client.send(action);
          }
        else if(param[0] instanceof wtDroppableArea)
          {
          RPAction action=new RPAction();
          
          action.put("type","moveequip");
          action.put("targetslot",((wtDroppableArea)param[1]).getName());
          action.put("targetobject",playerid);
          action.put("sourceslot",((wtDroppableArea)param[0]).getName());
          action.put("sourceobject",playerid);
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
    
    slot=st.getSprite("data/slot.png");

    wtEventListener transferFromContainer=new wtEventListener()
      {
      public void onAction(Object... param)
        {
        /**TODO: FIXME: BUG: Code this correctly.*/
        if(param[0] instanceof Entity)
          {
          RPAction action=new RPAction();
          action.put("type","equip");
          action.put("target",((Entity)param[0]).getID().getObjectID());
          action.put("slot",((wtDroppableArea)param[1]).getName());
          InGameGUI.this.client.send(action);
          }
        else if(param[0] instanceof wtDroppableArea)
          {
          RPAction action=new RPAction();
          action.put("type","moveequip");
          action.put("targetslot",((wtDroppableArea)param[1]).getName());
          action.put("sourceslot",((wtDroppableArea)param[0]).getName());
          InGameGUI.this.client.send(action);
          }
        }
      };

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
  
  private MouseEvent lastDraggedEvent;
  private Entity choosenEntity;
  private wtDroppableArea choosenWidget;
    
  public void mouseDragged(MouseEvent e) 
    {
    lastDraggedEvent=e;
    }
    
  public void mouseMoved(MouseEvent e)  
    {
    lastDraggedEvent=null;
    
    if(widget!=null)
      {
      widget.onMouseOver(e.getPoint());
      }
    
    for(wtButton button: buttons)    
      {
      button.onMouseOver(e.getPoint());
      } 
    }

  /** the user has pressed and released the mouse button */
  public void mouseClicked(MouseEvent e) 
    {
    Point2D screenPoint=e.getPoint();
        
    if(widget!=null && widget.clicked(screenPoint))
      {
      if(gameObjects.has(widgetAssociatedEntity))
        {
        widgetAssociatedEntity.onAction(client, widget.choosen());
        widget=null;
        return;
        }
      }

    // check if someone clicked the 'quit' or 'cancel' button
    for(wtButton button: buttons)    
      {
      button.clicked(e.getPoint());
      } 

    widget=null;
    
    // get clicked entity
    Point2D point=screen.translate(screenPoint);
    Entity entity=gameObjects.at(point.getX(),point.getY());
    // for the cliecked entity....
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
        // ... show context menu
        String[] actions=entity.offeredActions();
        widget=new wtList(actions,screenPoint.getX(),screenPoint.getY());      
        widgetAssociatedEntity=entity;  
        }
      }
    }

  /** the user has pressed the mouse button () */
  public void mousePressed(MouseEvent e) 
    {
    if(e.getButton()==MouseEvent.BUTTON1)
      {        
      Point2D point=screen.translate(e.getPoint());
      choosenEntity=gameObjects.at(point.getX(),point.getY());
      
      if(choosenEntity==null)
        {
        for(wtDroppableArea item: droppableAreas)    
          {
          if(item.onMouseOver(e.getPoint()))
            {
            System.out.println ("Pressed on "+item.getName());
            choosenWidget = item;
            return;
            }
          } 
        }
      }
    }

  public void mouseReleased(MouseEvent e) 
    {
    if(lastDraggedEvent!=null && choosenEntity!=null)
      {
      Point2D point=screen.translate(e.getPoint());
      System.out.println (choosenEntity+" moved to "+point);
      
      // We check first inventory and if it fails we wanted to move the object so. 
      for(wtDroppableArea item: droppableAreas)    
        {
        if(item.released(e.getPoint(),choosenEntity))
          {
          // We dropped it in inventory
          System.out.println ("Dropped "+choosenEntity+" into "+item.getName());
          choosenEntity=null;
          lastDraggedEvent=null;
          return;
          }
        }

      choosenEntity.onAction(client, "Displace", Integer.toString((int)point.getX()), Integer.toString((int)point.getY()));
      choosenEntity=null;
      lastDraggedEvent=null;
      }

    if(lastDraggedEvent!=null && choosenWidget!=null)
      {
      Point2D point=screen.translate(e.getPoint());
      System.out.println (choosenWidget.getName()+" dropped to "+point);

      // We check first inventory and if it fails we wanted to move the object so. 
      for(wtDroppableArea item: droppableAreas)    
        {
        if(item.released(e.getPoint(),choosenWidget))
          {
          // We dropped it in inventory
          System.out.println ("Moved from "+choosenWidget.getName()+" into "+item.getName());
          choosenWidget=null;
          lastDraggedEvent=null;
          return;
          }
        }
      
      int baseobjectid=0;
      int item=-1;
      String slot=null;
      
      System.out.println (inspectedSlot);
      System.out.println (inspectedEntity);
      
      if(inspectedSlot==null || !choosenWidget.getName().startsWith("left"))
        {
        baseobjectid=client.getPlayer().getID().getObjectID();
        slot=choosenWidget.getName();
        System.out.println ("Want to move from player");
        }
      else
        {
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
      action.put("slot",slot);
      action.put("item",item);
      action.put("x",(int)point.getX());
      action.put("y",(int)point.getY());
      InGameGUI.this.client.send(action);

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
      return;
      }
      
    if(e.getKeyCode()==KeyEvent.VK_L && e.isControlDown())
      {
      client.getGameLogDialog().setVisible(true);
      }
    else if(e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_DOWN)
      {
      action=new RPAction();
      if(e.isControlDown())
        {
        action.put("type","face");
        }
      else
        {
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
    widget=null;
    
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
  
  public void inspect(Entity entity, RPSlot slot)
    {
    for(wtDroppableArea area:inspectedDroppableAreas)
      {
      area.setEnabled(entity!=null);
      }
      
    inspectedEntity=entity;
    inspectedSlot=slot;
    }

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
    screen.drawInScreen(inGameInventory,510,10);

    for(wtDroppableArea item: droppableAreas)    
      {
      item.draw(screen);
      } 
    
    RPObject player=client.getPlayer();
    if(player!=null)
      {
      String[] slots=new String[]{"lhand","rhand","armor","bag"};
      for(String slotName: slots)
       {
       if(player.hasSlot(slotName))
         {
         RPSlot slot=player.getSlot(slotName);
         if(slot.size()==1)
           {
           wtDroppableArea dropArea=getDroppableArea(slotName);
           RPObject object=slot.iterator().next();
           screen.drawInScreen(gameObjects.spriteType(object),dropArea.getx(),dropArea.gety());
           }
         }
       }

      screen.drawInScreen(screen.createString("HP : "+player.get("hp")+"/"+player.get("base_hp"),Color.white),550, 184);
      screen.drawInScreen(screen.createString("ATK: "+player.get("atk")+" ("+player.get("atk_xp")+")",Color.white),550, 204);
      screen.drawInScreen(screen.createString("DEF: "+player.get("def")+" ("+player.get("def_xp")+")",Color.white),550, 224);
      screen.drawInScreen(screen.createString("XP : "+player.get("xp"),Color.white),550, 244);

      if(inspectedSlot!=null)
        {
        int i=1;
        for(RPObject object: inspectedSlot)
          {
          wtDroppableArea area=getDroppableArea("left_00"+i);                    
          screen.drawInScreen(slot,area.getx()-4,area.gety()-4);
          screen.drawInScreen(gameObjects.spriteType(object),area.getx(),area.gety());
          i++;
          }

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
    
    for(wtDroppableArea item: droppableAreas)
      {
      item.draw(screen);
      }
    
    for(wtButton button: buttons)
      {
      button.draw(screen);
      }
    
    if(widget!=null)
      {
      widget.draw(screen);
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
