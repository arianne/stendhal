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

  private Sprite textImage;
  private long textImagesTime;
  
  public Player(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    name="";
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
  

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);

    /** Choose the proper animation */
    if(stopped && changes.has("dir"))
      {
      int value=changes.getInt("dir");
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
    
    /** Attack code */  
    if(changes.has("target") || object.has("target"))
      {      
      int risk=(changes.has("risk")?changes.getInt("risk"):0);
      int damage=(changes.has("damage")?changes.getInt("damage"):0);
      int target=(changes.has("target")?changes.getInt("target"):object.getInt("target"));
      
      gameObjects.attack(this,new RPObject.ID(target,changes.get("zoneid")),risk,damage);
      
      if(risk>0)
        {
        StendhalClient.get().addEventLine(name+" striked and damaged with "+damage+" points to "+target,Color.RED);
        }
      else
        {
        StendhalClient.get().addEventLine(name+" missed striking to "+target,Color.RED);
        }
      }
  
    /** Create player name */
    if(changes.has("name"))
      {
      name=changes.get("name");
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();

      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(g2d.getFontMetrics().stringWidth(name),16,Transparency.BITMASK);    
      Graphics g=image.getGraphics();
      g.setColor(Color.white);
      g.drawString(name,0,10);
      nameImage=new Sprite(image);      
      }

    /** Add text lines */
    if(changes.has("text"))    
      {
      String text=changes.get("text");
      StendhalClient.get().addEventLine("<"+name+">: "+text);
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();

      int lineLengthPixels=g2d.getFontMetrics().stringWidth(text);
      int numLines=(lineLengthPixels/240)+1;
      
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(((lineLengthPixels<240)?lineLengthPixels:240)+6,16*numLines,Transparency.BITMASK);
      
      Graphics g=image.getGraphics();
      g.setColor(Color.white);
      g.fillRoundRect(0,0,((lineLengthPixels<240)?lineLengthPixels:240)+6,16*numLines,3,3);

      g.setColor(Color.black);
      int lineLength=text.length()/numLines;
      for(int i=0;i<numLines;i++)
        {
        String line=text.substring(i*lineLength,(i+1)*lineLength);
        g.drawString(line,3,i*16+12);
        }
        
      textImage=new Sprite(image);      
      textImagesTime=System.currentTimeMillis();
      }  
    }
    
  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyRemoved(object,changes);
    if(changes.has("target"))
      {     
      gameObjects.attackStop(this,new RPObject.ID(object.getInt("target"),object.get("zoneid")));
      }
    }
    
  public void draw(GameScreen screen)
    {
    if(nameImage!=null) 
      {
      screen.draw(nameImage,x,y-0.3);
      }
      
    if(textImage!=null) 
      {
      double ty=y+2.05;
      double tx=x+0.7-(textImage.getWidth()/(32.0f*2.0f));
      screen.draw(textImage,tx,ty);
      
      if(System.currentTimeMillis()-textImagesTime>TEXT_PERSISTENCE_TIME)
        {
        textImage=null;
        }
      }
      
    super.draw(screen);
    }
  }
