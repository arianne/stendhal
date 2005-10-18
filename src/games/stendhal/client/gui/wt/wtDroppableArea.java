package games.stendhal.client.gui.wt;

import games.stendhal.client.*;
import games.stendhal.client.entity.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class wtDroppableArea
  {
  private String name;
  private Rectangle area;
  private wtEvent action;
  private boolean enabled;
  
  public wtDroppableArea(String name,int x, int y, int width, int height)
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
  
  public void addActionListener(wtEvent action)
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

  public boolean released(Point2D point, wtDroppableArea choosenWidget)
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
  
