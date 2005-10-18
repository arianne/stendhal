package games.stendhal.client.gui.wt;

import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class wtButton 
  {
  private String name;
  private Sprite[] buttons;
  private Rectangle area;
  private wtEvent action;
  private boolean over;
  private boolean enabled;
  
  public wtButton(String name, Sprite normal, Sprite over, int x, int y)
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
  
  public void addActionListener(wtEvent action)
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
