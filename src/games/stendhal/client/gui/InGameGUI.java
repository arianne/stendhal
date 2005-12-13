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
import games.stendhal.client.gui.wt.core.*;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Direction;

import java.awt.event.*;
import java.awt.geom.Point2D;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class InGameGUI implements MouseListener, KeyListener //,MouseMotionListener
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(InGameGUI.class);

  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;

  /** a nicer way of handling the keyboard */
  private Map<Integer, Object> pressed;

  /** the main frame */
  private Frame frame;
  /** this is the ground */
  private Panel ground;
  /** settings panel */
  private SettingsPanel settings;
  /** the dialog "really quit?" */
  private Panel quitDialog;

  /** player can inspect entities to check what they contains. This variable stores
   *  the actual inspected entity. */
  private Entity inspectedEntity;
  /** this is the slot of the above entity that is being inspecting right now. */
  private RPSlot inspectedSlot;

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
    
    buildGUI();
    }
  
  private void buildGUI()
    {
    // create the frame
    frame = new Frame(screen);
    // register native event handler
    screen.getComponent().addMouseListener(frame);
    screen.getComponent().addMouseMotionListener(frame);
    // create ground
    ground = new GroundContainer(screen,gameObjects);
    frame.addChild(ground);
    // the settings panel creates all other
    settings = new SettingsPanel(ground, gameObjects);
    ground.addChild(settings);
    }
  

  /** the user has pressed and released the mouse button */
  public void mouseClicked(MouseEvent e) 
    {
    Point2D screenPoint=e.getPoint();
    
    // get clicked entity
    Point2D point=screen.translate(screenPoint);
    Entity entity=gameObjects.at(point.getX(),point.getY());
    // for the clicked entity....
    if(entity!=null)
      {
      if(e.getButton()==MouseEvent.BUTTON1 && isCtrlDown)
        {        
        java.util.List<String> actions=java.util.Arrays.asList(entity.offeredActions());
        if(actions.contains("Attack"))
          {
          entity.onAction(client, "Attack");
          }
        else if(actions.contains("Inspect"))
          {
          entity.onAction(client, "Inspect");
          }
        else if(actions.contains("Use"))
          {
          entity.onAction(client, "Use");
          }
        }
      else if(e.getButton()==MouseEvent.BUTTON1 && isShiftDown)
        {        
        entity.onAction(client, "Look");
        }
      else if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
        {        
        // ... do the default action
        String action=entity.defaultAction();
        entity.onAction(client, action);
        }
      else if(e.getButton()==MouseEvent.BUTTON3)
        {
        // ... show context menu (aka command list)
        String[] actions=entity.offeredActions();
        if (actions.length > 0)
          {
          frame.setContextMenu(new CommandList(entity.getType(),actions,(int) screenPoint.getX(),(int) screenPoint.getY(),100,100,client,entity));
          }
        }
      }
    }

  /** the user has pressed the mouse button */
  public void mousePressed(MouseEvent e) 
    {
    }
  

  /** the user has released the mouse button */
  public void mouseReleased(MouseEvent e) 
    {
    }

  public void mouseEntered(MouseEvent e) 
    {
    }

  public void mouseExited(MouseEvent e) 
    {
    }    
  
  private boolean isCtrlDown;
  private boolean isShiftDown;
  private boolean isAltDown;

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
      /* If Ctrl+L we set the Game log dialog visible */
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
    action.put("type","stop");

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
          client.send(action);
          }
        break;
      }
    }
    
  public void keyPressed(KeyEvent e) 
    {
    isAltDown=e.isAltDown();
    isCtrlDown=e.isControlDown();
    isShiftDown=e.isShiftDown();
    
    if(!pressed.containsKey(Integer.valueOf(e.getKeyCode())))
      {
      onKeyPressed(e);
      pressed.put(Integer.valueOf(e.getKeyCode()),null);
      }      
    }
      
  public void keyReleased(KeyEvent e) 
    {
    isAltDown=e.isAltDown();
    isCtrlDown=e.isControlDown();
    isShiftDown=e.isShiftDown();
    
    onKeyReleased(e);
    pressed.remove(Integer.valueOf(e.getKeyCode()));
    }

  public void keyTyped(KeyEvent e) 
    {
    if (e.getKeyChar() == 27) 
      {
      // On ESC key we stop the player and 
      RPAction rpaction=new RPAction();
      rpaction.put("type","stop");
      rpaction.put("attack","");
      client.send(rpaction);

      // quit messagebox already showing? 
      if (quitDialog == null)
        {
        // no, so show it
        quitDialog = new MessageBox("quit",220,220,200,"quit stendhal?",MessageBox.ButtonCombination.YES_NO);
        quitDialog.registerClickListener(new ClickListener()
              {
              public void onClick(String name, boolean pressed)
                {
                quitDialog = null; // remove field as the messagebox is closed now
                if (pressed && name.equals(MessageBox.ButtonEnum.YES.getName()))
                  {
                  // Yes-Button clicked...logut and quit.
                  client.requestLogout();
                  }
                };        
              });
        frame.addChild(quitDialog);
        }
      else
        {
        // yes message box is there already. remove it 
        frame.removeChild(quitDialog);
        quitDialog = null;
        }
      }
    }
  
  /** This methods inspects an entity by enabling all the droppable areas.
   * To stop inspecting this method is called with entity=null */
  public void inspect(Entity entity, RPSlot slot)
    {
    if (entity == null || slot == null || ground == null)
    {
      return;
    }
    
    inspectedEntity=entity;
    inspectedSlot=slot;
    
    EntityContainer container = new EntityContainer(gameObjects,entity.getType(),2,2);
    container.setSlot(entity,slot.getName());
    ground.addChild(container);
    
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
      // create the map if there is none yet
      StaticGameLayers gl = client.getStaticGameLayers();
      if (gl.changedArea())
      {
        CollisionDetection cd = gl.getCollisionDetection();
        if (cd != null)
        {
          gl.resetChangedArea();
          settings.updateMinimap(cd, screen.expose().getDeviceConfiguration(), gl.getArea());
        }
      }
  
      RPObject player = client.getPlayer();
      settings.setPlayer(player);
      
      System.out.println (player);
  
      frame.draw(screen.expose());
    
    }
  }
