package games.stendhal.client.gui;

import java.awt.geom.*;
import java.awt.event.*;
import java.awt.*;
import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.entity.*;
import games.stendhal.client.*;


public class InGameGUI implements MouseListener
  {
  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;
  
  public InGameGUI(StendhalClient client)
    {
    this.client=client;
    this.gameObjects=client.getGameObjects();
    this.screen=GameScreen.get();
    }
    
  public void mouseClicked(MouseEvent e) 
    {
    Point2D point=screen.translate(e.getPoint());
    System.out.println(point);
    
    GameEntity entity=gameObjects.at(point.getX(),point.getY());
    if(entity!=null)
      {
      if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
        {
        entity.onDoubleClick(client);
        }
      else if(e.getButton()==MouseEvent.BUTTON1)
        {
        entity.onClick(client);
        }
      if(e.getButton()==MouseEvent.BUTTON3)
        {
        entity.onLeftClick(client);
        }

      System.out.println (entity.getClass());
      }

    if(entity==null && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
      {
      System.out.println ("Moving to "+point);
      RPAction action=new RPAction();
      action.put("type","moveto");
      action.put("x",point.getX()-0.9);
      action.put("y",point.getY()-1.6);
      client.send(action);
      } 

    boolean attacking=client.getPlayer().has("target");
    if(entity!=null && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==1 && attacking)
      {
      System.out.println ("Stop attacking to "+point);
      RPAction action=new RPAction();
      action.put("type","stop");
      client.send(action);
      }
    else if(entity!=null && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==1)
      {
      System.out.println ("Attacking to "+point);
      RPAction action=new RPAction();
      action.put("type","attack");
      int id=entity.getID().getObjectID();
      action.put("target",id);      
      client.send(action);
      } 
    }

  public void mousePressed(MouseEvent e) 
    {
    }

  public void mouseReleased(MouseEvent e) 
    {
    }

  public void mouseEntered(MouseEvent e) 
    {
    }

  public void mouseExited(MouseEvent e) 
    {
    }    
  }
