package games.stendhal.client.gui;

import java.awt.geom.*;
import java.awt.event.*;
import java.awt.*;
import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.entity.*;
import games.stendhal.client.*;


public class InGameGUI implements MouseListener, MouseMotionListener
  {
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
  private Entity widgetAssociatedEntity;
  
  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;
  
  public InGameGUI(StendhalClient client)
    {
    this.client=client;
    this.gameObjects=client.getGameObjects();
    this.screen=GameScreen.get();
    }
    
  public void mouseDragged(MouseEvent e) 
    {
    }
    
  public void mouseMoved(MouseEvent e)  
    {
    if(widget!=null)
      {
      widget.onMouseOver(e.getPoint());
      }    
    }

  public void mouseClicked(MouseEvent e) 
    {
    Point2D screenPoint=e.getPoint();
        
    if(widget!=null && widget.clicked(screenPoint))
      {
      if(gameObjects.has(widgetAssociatedEntity))
        {
        widgetAssociatedEntity.onAction(widget.choosen(), client);
        }
      }

    widget=null;
    
    Point2D point=screen.translate(screenPoint);
    System.out.println(point);    
    
    Entity entity=gameObjects.at(point.getX(),point.getY());
    if(entity!=null)
      {
      if(e.getButton()==MouseEvent.BUTTON1)
        {        
        String action=entity.defaultAction();
        entity.onAction(action, client);
        }
      else if(e.getButton()==MouseEvent.BUTTON3)
        {
        String[] actions=entity.offeredActions();
        widget=new InGameList(actions,screenPoint.getX(),screenPoint.getY());      
        widgetAssociatedEntity=entity;  
        }
      }
//      if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
//        {
//        entity.onDoubleClick(client);
//        }
//      else if(e.getButton()==MouseEvent.BUTTON1)
//        {
//        entity.onClick(client);
//        }
//      else if(e.getButton()==MouseEvent.BUTTON3)
//        {
//        entity.onLeftClick(client);
//        }
//
//      System.out.println (entity.getClass());
//      }
//
//    if(entity==null && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
//      {
//      System.out.println ("Moving to "+point);
//      RPAction action=new RPAction();
//      action.put("type","moveto");
//      action.put("x",point.getX()-0.9);
//      action.put("y",point.getY()-1.6);
//      client.send(action);
//      } 
//
//    boolean attacking=client.getPlayer().has("target");
//    if(entity!=null && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==1 && attacking)
//      {
//      System.out.println ("Stop attacking to "+point);
//      RPAction action=new RPAction();
//      action.put("type","stop");
//      client.send(action);
//      }
//    else if(entity!=null && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==1)
//      {
//      System.out.println ("Attacking to "+point);
//      RPAction action=new RPAction();
//      action.put("type","attack");
//      int id=entity.getID().getObjectID();
//      action.put("target",id);      
//      client.send(action);
//      } 
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

  public void draw(GameScreen screen)
    {
    if(widget!=null)
      {
      widget.draw(screen);
      }
    }
  }
