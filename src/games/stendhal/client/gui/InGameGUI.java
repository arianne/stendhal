package games.stendhal.client.gui;

import java.awt.geom.*;
import java.awt.event.*;
import java.awt.*;

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.entity.*;
import games.stendhal.client.*;
import games.stendhal.common.*;

import java.util.*;


public class InGameGUI implements MouseListener, MouseMotionListener, KeyListener
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

  private Map<Integer, Object> pressed;
  
  private Sprite inGameInventory;
  private Sprite inGameDevelPoint;
  
  public InGameGUI(StendhalClient client)
    {
    this.client=client;
    gameObjects=client.getGameObjects();
    screen=GameScreen.get();
    
    pressed=new HashMap<Integer, Object>();
    
    SpriteStore st=SpriteStore.get();
    
    buttons=new java.util.LinkedList<InGameButton>();
    InGameButton button=null;
    button=new InGameButton("atk",st.getSprite("data/atk_up.gif"), st.getSprite("data/atk_up_pressed.gif"), 530,84);
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
    button.setEnabled(false);
    buttons.add(button);
    
    button=new InGameButton("def",st.getSprite("data/def_up.gif"), st.getSprite("data/def_up_pressed.gif"), 530,84+14);
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
    button.setEnabled(false);
    buttons.add(button);

    button=new InGameButton("hp",st.getSprite("data/hp_up.gif"), st.getSprite("data/hp_up_pressed.gif"), 530,84+28);
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
    button.setEnabled(false);
    buttons.add(button);


    button=new InGameButton("exit",st.getSprite("data/exit.gif"), st.getSprite("data/exit_pressed.gif"), 320,360);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction()
        {
        InGameGUI.this.client.requestLogout();
        }
      });
    button.setEnabled(false);
    buttons.add(button);
    
    button=new InGameButton("back",st.getSprite("data/back.gif"), st.getSprite("data/back_pressed.gif"), 220,360);
    button.addActionListener(new InGameActionListener()
      {
      public void onAction()
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
    
    inGameInventory=SpriteStore.get().getSprite("data/equipmentGUI.gif");
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

  public void onKeyPressed(KeyEvent e)  
    {
    RPAction action;
    
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

  public void draw(GameScreen screen)
    {
    screen.drawInScreen(inGameInventory,530,10);
    
    RPObject player=client.getPlayer();
    if(player!=null)
      {
      screen.drawInScreen(screen.createString("HP : "+player.get("hp")+"/"+player.get("base_hp"),Color.white),550, 144);
      screen.drawInScreen(screen.createString("ATK: "+player.get("atk"),Color.white),550, 164);
      screen.drawInScreen(screen.createString("DEF: "+player.get("def"),Color.white),550, 184);
      screen.drawInScreen(screen.createString("XP : "+player.get("xp"),Color.white),550, 204);
      
      if(player.has("devel") && player.getInt("devel")>0)
        {
        screen.drawInScreen(screen.createString("Devel: "+player.get("devel"),Color.yellow),550, 224);
        
        for(InGameButton button: buttons)    
          {
          if(button.getName().equals("hp") || button.getName().equals("atk") || button.getName().equals("def"))
            {
            button.setEnabled(true);
            }
          }
        }
      else
        {
        for(InGameButton button: buttons)    
          {
          if(button.getName().equals("hp") || button.getName().equals("atk") || button.getName().equals("def"))
            {
            button.setEnabled(false);
            }
          }
        }
      }
    
    for(InGameButton button: buttons)    
      {
      button.draw(screen);
      } 
    
    if(widget!=null)
      {
      widget.draw(screen);
      }
    }
  }
