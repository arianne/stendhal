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

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class Sign extends Entity 
  {
  private final static int TEXT_PERSISTENCE_TIME=5000;
  private String text;
  private Sprite textImage;
  private long delta;
  
  public Sign(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    GameScreen screen=GameScreen.get();

    
    if(changes.has("text"))
      {
      text=changes.get("text");

      Graphics g2d=screen.expose();
      
      String[] lines=text.split("\\|");

      int lineLengthPixels=0;
      for(String line: lines)
        {
        int val=g2d.getFontMetrics().stringWidth(line);
        if(val>lineLengthPixels)
          {
          lineLengthPixels=val;
          }
        }
      
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      int width=lineLengthPixels+4;
      int height=16*lines.length;
      
      Image image = gc.createCompatibleImage(width,height,Transparency.BITMASK);
      
      Graphics g=image.getGraphics();
      g.setColor(Color.white);
      g.fillRect(0,0,width,height);
      g.setColor(Color.black);
      g.drawRect(0,0,width-1,height-1);
            
      int j=0;
      for(String line: lines)
        {
        g.setColor(Color.black);
        g.drawString(line,2,11+j*16);
        j++;
        }
        
      textImage=new Sprite(image);      
      }
    }
    
  public String defaultAction()
    {
    return "Look";
    }

  public String[] offeredActions()
    {
    String[] list={"Look"};
    return list;
    }

  public void onAction(String action, StendhalClient client)
    {
    if(action.equals("Look"))
      {
      gameObjects.addText(this, textImage);
      StendhalClient.get().addEventLine("You read \""+text.replace("|","\n")+"\"",Color.green);
      }
    }
  }
