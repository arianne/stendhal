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
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.PassiveEntity;
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


/** TODO: Move InGame classes to their own classes and package as they build the GUI 
 *        and make this code hard to understand ( because of lots of GUI specific code */
public class InGameGUI implements MouseListener, MouseMotionListener, KeyListener
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(InGameGUI.class);

  interface InGameAction
    {
    public void onAction(Object... param);
    }
  
  abstract class InGameActionListener implements InGameAction
    {
    abstract public void onAction(Object... param);
    }
    
  static class InGameButton
    {
    private String name;
    private Sprite[] buttons;
    private Rectangle area;
    private InGameAction action;
    private boolean over;
    private boolean enabled;
    
    public InGameButton(String name, Sprite normal, Sprite over, int x, int y)
      {
      buttons=new Sprite[2];
      buttons[0]=normal;
      buttons[1]=over;
      
      area=new Rectangle(x,y,buttons[0].getWidth(),buttons[0].getHeight());
      this.over=false;
      this.action=null;
      this.enabled=true;
      this.name=name;
      }
    
    public String getName()
      {
      return name;
      }
    
    public void setEnabled(boolean enabled)
      {
      this.enabled=enabled;
      }

    public void draw(GameScreen screen)
      {
      if(!enabled) return;
      Sprite button;
      
      if(over)
        {
        button=buttons[1];
        }
      else
        {
        button=buttons[0];
        }
        
      screen.drawInScreen(button,(int)area.getX(),(int)area.getY());
      }
    
    public void addActionListener(InGameAction action)
      {
      this.action=action;
      }

    public boolean onMouseOver(Point2D point)
      {
      if(!enabled) return false;
      if(area.contains(point))
        {
        over=true;
        }
      else
        {
        over=false;
        }
      
      return false;
      }
    
    public boolean clicked(Point2D point)
      {
      if(!enabled) return false;
      if(area.contains(point))
        {
        action.onAction();
        return true;
        }
      
      return false;
      }    
    }

  static class InGameDroppableArea
    {
    private String name;
    private Rectangle area;
    private InGameAction action;
    private boolean enabled;
    
    public InGameDroppableArea(String name,int x, int y, int width, int height)
      {
      this.name=name;
      area=new Rectangle(x,y,width,height);
      this.action=null;
      this.enabled=true;
      }
    
    public String getName()
      {
      return name;
      }
    
    public int getx()
      {
      return (int)area.getX();
      }
    
    public int gety()
      {
      return (int)area.getY();
      }

    public void setEnabled(boolean enabled)
      {
      this.enabled=enabled;
      }
    
    public void addActionListener(InGameAction action)
      {
      this.action=action;
      }

    public void draw(GameScreen screen)
      {
      if(!enabled) return;
      Graphics g=screen.expose();
      g.setColor(Color.white);
      g.drawRect((int)area.getX(),(int)area.getY(),(int)area.getWidth(),(int)area.getHeight());      
      }
      
    public boolean isMouseOver(Point2D point)
      {
      if(!enabled) return false;
      if(area.contains(point))
        {
        return true;
        }
      
      return false;
      }
      
    public boolean released(Point2D point, Entity choosenEntity)
      {
      if(!enabled) return false;
      if(area.contains(point))
        {
        action.onAction(choosenEntity,this);
        return true;
        }
      
      return false;
      }    

    public boolean released(Point2D point, InGameDroppableArea choosenWidget)
      {
      if(!enabled) return false;
      if(area.contains(point))
        {
        action.onAction(choosenWidget,this);
        return true;
        }
      
      return false;
      }    
    }
    
  static class InGameList
    {
    private Rectangle area;
    private String[] list;
    private int choosen;
    private int over;
    private Sprite action_list;
    
    private Sprite render(double x, double y, double mouse_x, double mouse_y)
      {
      int width=70+6;
      int height=6+16*list.length;
      
      area= new Rectangle((int)x,(int)y,width,height);
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(width,height,Transparency.BITMASK);    
      Graphics g=image.getGraphics();

      g.setColor(Color.gray);
      g.fillRect(0,0,width,height);

      g.setColor(Color.black);
      g.drawRect(0,0,width-1,height-1);
      
      g.setColor(Color.yellow);
      int i=0;
      for(String item: list)
        {
        if((mouse_y-y)>16*i && (mouse_y-y)<16*(i+1))
          {
          g.setColor(Color.white);
          g.drawRect(0,16*i,width-1,16);
          g.drawString(item,3,13+16*i);
          g.setColor(Color.yellow);
          over=i;
          }
        else
          {
          g.drawString(item,3,13+16*i);
          }
          
        i++;
        }
      
      return new Sprite(image);
      }
    
    public InGameList(String[] list, double x, double y)
      {
      this.list=list;
      over=-1;
      action_list=render(x,y,-1,-1);      
      }
    
    public void draw(GameScreen screen)
      {
      Point2D translated=screen.translate(new Point((int)area.getX(),(int)area.getY()));      
      screen.draw(action_list,translated.getX(),translated.getY());
      }
    
    public boolean onMouseOver(Point2D point)
      {
      if(area.contains(point) && over!=(point.getY()-area.getY())/16)
        {
        action_list=render(area.getX(),area.getY(),point.getX(),point.getY());      
        return true;
        }
      
      return false;
      }
    
    public boolean clicked(Point2D point)
      {
      if(area.contains(point))
        {
        choosen=(int)((point.getY()-area.getY())/16);
        return true;
        }
      
      return false;
      }
    
    public String choosen()
      {
      return list[choosen];
      }
    }
  
  private InGameList widget;
  private java.util.List<InGameButton> buttons;  
  private java.util.List<InGameDroppableArea> droppableAreas;
  private Entity widgetAssociatedEntity;
  
  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;

  private Map<Integer, Object> pressed;
  
  private Sprite inGameInventory;
  private Sprite inGameDevelPoint;
  private Sprite slot;
  
  private RPSlot inspectedSlot;
  private java.util.List<InGameDroppableArea> inspectedDroppableAreas;
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
    
    buttons=new java.util.LinkedList<InGameButton>();
    droppableAreas=new java.util.LinkedList<InGameDroppableArea>();
    inspectedDroppableAreas=new java.util.LinkedList<InGameDroppableArea>();
    
    buildGUI();
    }
  
  private void buildGUI()
    {
    SpriteStore st=SpriteStore.get();

    InGameButton button=new InGameButton("exit",st.getSprite("data/exit.png"), st.getSprite("data/exit_pressed.png"), 320,360);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction(Object... param)
        {
        InGameGUI.this.client.requestLogout();
        }
      });
    button.setEnabled(false);
    buttons.add(button);
    
    button=new InGameButton("back",st.getSprite("data/back.png"), st.getSprite("data/back_pressed.png"), 220,360);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction(Object... param)
        {
        for(InGameButton button: buttons)    
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
    
    InGameActionListener dropToInventory=new InGameActionListener()
      {
      public void onAction(Object... param)
        {
        int playerid=client.getPlayer().getID().getObjectID();
        if(param[0] instanceof Entity) 
          {
          RPAction action=new RPAction();
          action.put("type","equip");
          action.put("target",((Entity)param[0]).getID().getObjectID());
          action.put("slot",((InGameDroppableArea)param[1]).getName());
          action.put("baseobject",playerid);
          InGameGUI.this.client.send(action);
          }
        else if(param[0] instanceof InGameDroppableArea)
          {
          RPAction action=new RPAction();
          
          action.put("type","moveequip");
          action.put("targetslot",((InGameDroppableArea)param[1]).getName());
          action.put("targetobject",playerid);
          action.put("sourceslot",((InGameDroppableArea)param[0]).getName());
          action.put("sourceobject",playerid);
          InGameGUI.this.client.send(action);
          }
        }
      };
    
    InGameDroppableArea area=null;
    
    area=new InGameDroppableArea("lhand",515,68,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);
    
//    area=new InGameDroppableArea("head",558,14,32,32);
//    area.addActionListener(dropToInventory);
//    droppableAreas.add(area);
    
    area=new InGameDroppableArea("armor",558,56,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);
    
//    area=new InGameDroppableArea("legs",558,98,32,32);
//    area.addActionListener(dropToInventory);
//    droppableAreas.add(area);
//    
//    area=new InGameDroppableArea("feet",558,141,32,32);
//    area.addActionListener(dropToInventory);
//    droppableAreas.add(area);
    
    area=new InGameDroppableArea("rhand",601,68,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);    

    area=new InGameDroppableArea("bag",601,122,32,32);
    area.addActionListener(dropToInventory);
    droppableAreas.add(area);    
    
    slot=st.getSprite("data/slot.png");

    InGameActionListener transferFromContainer=new InGameActionListener()
      {
      public void onAction(Object... param)
        {
        /**TODO: FIXME: BUG: Code this correctly.*/
        if(param[0] instanceof Entity)
          {
          RPAction action=new RPAction();
          action.put("type","equip");
          action.put("target",((Entity)param[0]).getID().getObjectID());
          action.put("slot",((InGameDroppableArea)param[1]).getName());
          InGameGUI.this.client.send(action);
          }
        else if(param[0] instanceof InGameDroppableArea)
          {
          RPAction action=new RPAction();
          action.put("type","moveequip");
          action.put("targetslot",((InGameDroppableArea)param[1]).getName());
          action.put("sourceslot",((InGameDroppableArea)param[0]).getName());
          InGameGUI.this.client.send(action);
          }
        }
      };

    area=new InGameDroppableArea("left_001",6,414,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    area=new InGameDroppableArea("left_002",6,369,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    area=new InGameDroppableArea("left_003",6,324,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    area=new InGameDroppableArea("left_004",6,279,32,32);
    area.addActionListener(transferFromContainer);
    inspectedDroppableAreas.add(area);
    droppableAreas.add(area);    

    for(InGameDroppableArea disabledArea:inspectedDroppableAreas)
      {
      disabledArea.setEnabled(false);
      }
    }
  
  private InGameDroppableArea getDroppableArea(String name)
    {
    for(InGameDroppableArea item: droppableAreas)
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
  private InGameDroppableArea choosenWidget;
    
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
    
    for(InGameButton button: buttons)    
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
    for(InGameButton button: buttons)    
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
        widget=new InGameList(actions,screenPoint.getX(),screenPoint.getY());      
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
        for(InGameDroppableArea item: droppableAreas)    
          {
          if(item.isMouseOver(e.getPoint()))
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
      for(InGameDroppableArea item: droppableAreas)    
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
      for(InGameDroppableArea item: droppableAreas)    
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

      for(InGameButton button: buttons)    
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
    for(InGameDroppableArea area:inspectedDroppableAreas)
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

    for(InGameDroppableArea item: droppableAreas)    
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
           InGameDroppableArea dropArea=getDroppableArea(slotName);
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
          InGameDroppableArea area=getDroppableArea("left_00"+i);                    
          screen.drawInScreen(slot,area.getx()-4,area.gety()-4);
          screen.drawInScreen(gameObjects.spriteType(object),area.getx(),area.gety());
          i++;
          }

        if(inspectedEntity.distance(player)>2.5*2.5)
          {
          for(InGameDroppableArea area:inspectedDroppableAreas)
            {
            area.setEnabled(false);
            }
            
          inspectedEntity=null;
          inspectedSlot=null;
          }
        }
      }
    
    for(InGameDroppableArea item: droppableAreas)
      {
      item.draw(screen);
      }
    
    for(InGameButton button: buttons)
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
