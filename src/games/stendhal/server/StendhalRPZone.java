package games.stendhal.server;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.common.net.*;
import marauroa.server.game.*;

import games.stendhal.common.*;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.*;
import java.io.*;


public class StendhalRPZone extends MarauroaRPZone 
  {
  private List<TransferContent> contents;
  private String entryPoint;
  private CollisionDetection collisionMap;
  private int width;
  private int height;
  
  public StendhalRPZone(String name)
    {
    super(name);
    
    contents=new LinkedList<TransferContent>();
    collisionMap=new CollisionDetection();
    }
  
  public void setEntryPoint(String entryPoint)
    {
    this.entryPoint=entryPoint;
    }
  
  public void placeObjectAtEntryPoint(RPObject object)
    {
    String[] components=entryPoint.split(",");
    object.put("x",components[0]);
    object.put("y",components[1]);
    }
  
  public void addLayer(String name, String filename) throws IOException
    {
    Logger.trace("StendhalRPZone::addLayer",">");
    TransferContent content=new TransferContent();
    content.name=name;
    content.cacheable=true;
    content.timestamp=(int)new File(filename).lastModified();
    content.data=getBytesFromFile(filename);
    
    contents.add(content);
    
    collisionMap.addLayer(new FileReader(filename));
    Logger.trace("StendhalRPZone::addLayer","<");
    }

  public int getWidth()
    {
    return collisionMap.getWidth();
    }
  
  public int getHeight()
    {
    return collisionMap.getHeight();
    }
  
  
  public List<TransferContent> getContents()
    {
    return contents;
    }
  
  private static Rectangle2D getCollisionArea(String type, double x, double y)
    {
    Rectangle2D rect=new Rectangle.Double();
    getCollisionArea(rect,type,x,y);
    return rect;
    }

  private static void getCollisionArea(Rectangle2D rect,String type, double x, double y)
    {
    if(type.equals("player"))
      {
      rect.setRect(x+0.5,y+1.3,0.87,0.6);
      }
    else
      {
      rect.setRect(x,y,1,2);
      }
    }

  public boolean collides(RPObject object, double x, double y) throws AttributeNotFoundException  
    {
    Rectangle2D area=getCollisionArea(object.get("type"),x,y);
    
    if(collisionMap.collides(area)==false)
      {
      Rectangle2D otherarea=new Rectangle.Double();
      for(RPObject other: objects.values())
        {
        if(!other.getID().equals(object.getID()))
          {
          getCollisionArea(otherarea,other.get("type"),other.getDouble("x"),other.getDouble("y"));
          if(area.intersects(otherarea))
            {
            return true;
            }
          }
        }
      
      return false;
      }   
    else
      {
      return true;
      }    
    }

  private static byte[] getBytesFromFile(String file) throws IOException 
    {
    InputStream is = new FileInputStream(file);
    
    long length = new File(file).length();
    byte[] bytes = new byte[(int)length];
    
    // Read in the bytes
    int offset = 0;
    int numRead;
    while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
      {
      offset += numRead;
      }
    
    if(offset < bytes.length) 
      {
      throw new IOException("Could not completely read file "+file);
      }
    
    // Close the input stream and return bytes
    is.close();
    return bytes;
    }
  }
