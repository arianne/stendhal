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
    TransferContent content=new TransferContent();
    content.name=name;
    content.cacheable=true;
    content.timestamp=0;
    content.data=getBytesFromFile(filename);
    
    contents.add(content);
    
    collisionMap.addLayer(new FileReader(filename));
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
  
  public boolean collides(Rectangle2D object)  
    {
    /** TODO: Think about porting to RPObject instead of rectangle. */
    return collisionMap.collides(object);
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
