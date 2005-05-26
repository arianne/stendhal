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
  interface InGameAction
    {
    public void onAction();
    }
  
  abstract class InGameActionListener implements InGameAction
    {
    abstract public void onAction();
    }
    
  static class InGameButton
    {
    private Sprite[] buttons;
    private Rectangle area;
    private InGameAction action;
    private boolean over;
    private boolean enabled;
    
    public InGameButton(Sprite normal, Sprite over, int x, int y)
      {
      buttons=new Sprite[2];
      buttons[0]=normal;
      buttons[1]=over;
      
      area=new Rectangle(x,y,buttons[0].getWidth(),buttons[0].getHeight());
      this.over=false;
      this.action=null;
      this.enabled=true;
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
  private Entity widgetAssociatedEntity;
  
  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;
  
  private Sprite inGameInventory;
  private Sprite inGameDevelPoint;
  
  public InGameGUI(StendhalClient client)
    {
    this.client=client;
    gameObjects=client.getGameObjects();
    screen=GameScreen.get();
    
    SpriteStore st=SpriteStore.get();
    
    buttons=new java.util.LinkedList<InGameButton>();
    InGameButton button=null;
    button=new InGameButton(st.getSprite("data/atk_up.gif"), st.getSprite("data/atk_up_pressed.gif"), 530,84);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction()
        {
        RPAction improve=new RPAction();
        improve.put("type","improve");
        improve.put("stat","atk");
        InGameGUI.this.client.send(improve);
        }
      });
    buttons.add(button);
    
    button=new InGameButton(st.getSprite("data/def_up.gif"), st.getSprite("data/def_up_pressed.gif"), 530,84+14);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction()
        {
        RPAction improve=new RPAction();
        improve.put("type","improve");
        improve.put("stat","def");
        InGameGUI.this.client.send(improve);
        }
      });
    buttons.add(button);

    button=new InGameButton(st.getSprite("data/hp_up.gif"), st.getSprite("data/hp_up_pressed.gif"), 530,84+28);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction()
        {
        RPAction improve=new RPAction();
        improve.put("type","improve");
        improve.put("stat","hp");
        InGameGUI.this.client.send(improve);
        }
      });
    buttons.add(button);
    
    inGameInventory=SpriteStore.get().getSprite("data/equipmentGUI.gif");
    inGameDevelPoint=SpriteStore.get().getSprite("data/levelup.gif");
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
    
    for(InGameButton button: buttons)    
      {
      button.onMouseOver(e.getPoint());
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
        widget=null;
        return;
        }
      }

    for(InGameButton button: buttons)    
      {
      button.clicked(e.getPoint());
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
    screen.drawInScreen(inGameInventory,530,10);
    
    boolean hasDevel=true;//false;
    
    RPObject player=client.getPlayer();
    if(player!=null && player.has("devel") && player.getInt("devel")>0)
      {
      hasDevel=true;
      }
    
    for(InGameButton button: buttons)    
      {
      button.setEnabled(hasDevel);
      button.draw(screen);
      } 
    
    if(widget!=null)
      {
      widget.draw(screen);
      }
    }
  }
