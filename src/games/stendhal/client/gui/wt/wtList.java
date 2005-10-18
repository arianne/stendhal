package games.stendhal.client.gui.wt;

import games.stendhal.client.*;
import games.stendhal.client.entity.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class wtList
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
  
  public wtList(String[] list, double x, double y)
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

