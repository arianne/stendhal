/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * SettingsPanel.java
 *
 * Created on 26. Oktober 2005, 20:12
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.GameObjects;
import games.stendhal.common.CollisionDetection;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPObject;

/**
 * The panel where you can adjust your settings
 *
 * @author mtotz
 */
public class SettingsPanel extends Panel implements ClickListener, CloseListener
{
  /** width of this panel */
  private static final int WIDTH = 200;
  
  /** the minimap panel */
  private Minimap minimap;
  
  /** buffered collision detection layer for minimap */
  private CollisionDetection cd;
  /** buffered GraphicsConfiguration for minimap */
  private GraphicsConfiguration gc;
  /** buffered zone name for minimap */
  private String zone;
  
  /** buffered gameObjects for character panel */
  private GameObjects gameObjects;
  
  /** the Character panel */
  private Character character;
  /** the frame */
  private Frame frame;
  /** the player */
  private RPObject player;
  /** map of the buttons (for faster access) )*/
  private Map<String,Button> buttonMap;
  /** is the minimap enabled (shown) */
  private boolean minimapEnabled;
  /** is the character panel enabled (shown) */
  private boolean characterEnabled;
  


  /** Creates a new instance of OptionsPanel */
  public SettingsPanel(Frame frame, GameObjects gameObjects)
  {
    super("Settings", (frame.getWidth()-WIDTH)/2, 50, WIDTH, 200 );
    setFrame(true);
    setTitleBar(true);
    setMinimizeable(true);
    setCloseable(false);
    minimapEnabled = true;
    characterEnabled = true;

    character = new Character(gameObjects);
    frame.addChild(character);
    
    
    buttonMap = new HashMap<String,Button>();
    buttonMap.put("minimap",new Button("minimap", 150, 30, "Enable Minimap"));
    buttonMap.put("character",new Button("character", 150, 30, "Enable Character"));
    
    int y = 10;
    for (Button button : buttonMap.values())
    {
      button.moveTo(10, y);
      y += 40;
      button.registerClickListener(this);
      addChild(button);
    }

    this.gameObjects = gameObjects;
    this.frame = frame;
  }
  
  /** updates the minimap */
  public void updateMinimap(CollisionDetection cd, GraphicsConfiguration gc, String zone)
  {
    this.cd = cd;
    this.gc = gc;
    this.zone = zone;
    
    // close the old minimap if there is one
    if (minimap != null)
    {
      minimap.close();
      minimap = null;
    }
    if (minimapEnabled)
    {
      // add a new one
      minimap = new Minimap(cd, gc, zone);
      minimap.registerCloseListener(this);
      frame.addChild(minimap);
    }
  }
  
  /** updates the minimap */
  public void setPlayer(RPObject player)
  {
    this.player = player;
    if (character != null)
    {
      character.setPlayer(player);
    }
  }

  /** draw the panel */
  public Graphics draw(Graphics g)
  {
    if (minimap != null && player != null)
    {
      minimap.setPlayerPos(player.getDouble("x"), player.getDouble("y"));
    }
    
    return super.draw(g);
  }

  /** a button was clicked */
  public void onClick(String name, boolean pressed)
  {
    // check minimap
    if (name.equals("minimap"))
    {
      // minimap disabled?
      if (minimapEnabled && !pressed)
      {
        minimap.close();
        minimap = null;
      }
      else if (!minimapEnabled && pressed)
      {
        // minimap enabled
        minimap = new Minimap(cd, gc, zone);
        minimap.registerCloseListener(this);
        frame.addChild(minimap);
      }
      minimapEnabled = pressed;
      // be sure to update the button
      buttonMap.get(name).setPressed(pressed);
      return;
    }
    
    // check character pamel
    if (name.equals("character"))
    {
      // minimap disabled?
      if (characterEnabled && !pressed)
      {
        character.close();
        character = null;
      }
      else if (!characterEnabled && pressed)
      {
        // minimap enabled
        character = new Character(gameObjects);
        character.registerCloseListener(this);
        character.setPlayer(player);
        frame.addChild(character);
      }
      characterEnabled = pressed;
      // be sure to update the button
      buttonMap.get(name).setPressed(pressed);
      return;
    }
    
  }

  /** a window is closed */
  public void onClose(String name)
  {
    // pseudo-release the button
    
    // minimap is named after its zone
    if (name.equals(zone))
    {
      onClick("minimap", false);
    }
    if (name.equals(player.get("name")))
    {
      onClick("character", false);
    }
  }
  
  
}
