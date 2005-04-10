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
package games.stendhal.client.entity;

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;
import java.awt.geom.*;

/** A Player entity */
public class Player extends AnimatedGameEntity 
  {
  private final static int TEXT_PERSISTENCE_TIME=10000;
  private String name;
  private Sprite nameImage;
  private java.util.List<Sprite> textImages;
  private java.util.List<Long> textImagesTimes;
  private long wasTextWritten;
  
  private Rectangle2D area;
  private Rectangle2D drawedArea;

  
  public Player(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    name="";
    textImages=new java.util.LinkedList<Sprite>();
    textImagesTimes=new java.util.LinkedList<Long>();
    nameImage=null;

    area=new Rectangle.Double(x+0.5,y+1.3,0.87,0.6);
    drawedArea=new Rectangle.Double(x,y,1,2);
    }
  
  public Rectangle2D getArea()
    {
    return area;
    }

  public Rectangle2D getDrawedArea()
    {
    return drawedArea;
    }
    
  protected void buildAnimations(String type)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("move_up", store.getAnimatedSprite(translate(type),0,4,64,48));      
    sprites.put("move_right", store.getAnimatedSprite(translate(type),1,4,64,48));      
    sprites.put("move_down", store.getAnimatedSprite(translate(type),2,4,64,48));      
    sprites.put("move_left", store.getAnimatedSprite(translate(type),3,4,64,48));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }
  

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    if(changes.has("target"))
      {     
      gameObjects.attackStop(this,new RPObject.ID(object.getInt("target"),object.get("zoneid")));
      }
    }
    
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    super.modify(object);
    
    area.setRect(x+0.5,y+1.3,0.87,0.6);
    drawedArea.setRect(x,y,1,2);
    
    if(stopped && object.has("dir"))
      {
      int value=object.getInt("dir");
      switch(value)
        {
        case 0:
          animation="move_left";
          break;
        case 1:
          animation="move_right";
          break;
        case 2:
          animation="move_up";
          break;
        case 3:
          animation="move_down";
          break;
        }
      }
      
    if(object.has("target"))
      {      
      System.out.println("Attacking RPObject: "+ object.get("target"));
      gameObjects.attack(this,new RPObject.ID(object.getInt("target"),object.get("zoneid")),object.getInt("risk"),object.getInt("damage"));
      
      if(object.getInt("risk")>0)
        {
        StendhalClient.get().addEventLine(name+" striked and damaged with "+object.get("damage")+" points to "+object.get("target"));
        System.out.println ("RPObject striked and damaged with "+object.get("damage")+" points");
        }
      else
        {
        StendhalClient.get().addEventLine(name+" missed striking to "+object.get("target"));
        System.out.println ("RPObject missed");
        }
      }
      
    if(name!=null && !name.equals(object.get("name")))
      {
      name=object.get("name");
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();

      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(g2d.getFontMetrics().stringWidth(name),16,Transparency.BITMASK);    
      Graphics g=image.getGraphics();
      g.setColor(Color.white);
      g.drawString(name,0,10);
      nameImage=new Sprite(image);      
      }

    if(object.has("text") && !object.get("text").equals(""))    
      {
      String text=object.get("text");
      StendhalClient.get().addEventLine("<"+name+">: "+text);
      
      //TODO: A hack to render properly the text... I think it is not a good idea to 
      // modify the RPObject itself... but...
      object.put("text","");
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();

      int lineLengthPixels=g2d.getFontMetrics().stringWidth(text);
      int numLines=(lineLengthPixels/240)+1;
      
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(((lineLengthPixels<240)?lineLengthPixels:240),16*numLines,Transparency.BITMASK);
      
      Graphics g=image.getGraphics();
      g.setColor(Color.yellow);
      int lineLength=text.length()/numLines;
      for(int i=0;i<numLines;i++)
        {
        String line=text.substring(i*lineLength,(i+1)*lineLength);
        g.drawString(line,0,i*16+10);
        }
        
      textImages.add(new Sprite(image));      
      textImagesTimes.add(new Long(System.currentTimeMillis()));
      }  
    }

  public void draw(GameScreen screen)
    {
    if(nameImage!=null) screen.draw(nameImage,x,y-0.3);
    if(textImages!=null) 
      {
      double ty=y+2.05;
      
      for(Sprite textImage: textImages)
        {        
        double tx=x+0.7-(textImage.getWidth()/(32.0f*2.0f));
        screen.draw(textImage,tx,ty);
        ty=ty+textImage.getHeight()/32.0f;
        }
      
      if(textImages.size()>0 && (System.currentTimeMillis()-textImagesTimes.get(0)>TEXT_PERSISTENCE_TIME))
        {
        textImages.remove(0);
        textImagesTimes.remove(0); 
        }
      }
      
    super.draw(screen);
    }
  }
