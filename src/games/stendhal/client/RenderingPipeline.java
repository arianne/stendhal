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
package games.stendhal.client;

public class RenderingPipeline 
  {
  private static RenderingPipeline renderPipeline;
  private StaticGameLayers gameLayers;
  private GameObjects gameObjects;
  
  private RenderingPipeline()
    {    
    }
  
  public void addGameLayer(StaticGameLayers layer)
    {
    gameLayers=layer;
    }

  public void addGameObjects(GameObjects objects)
    {
    gameObjects=objects;
    }
  
  public static RenderingPipeline get()
    {
    if(renderPipeline==null)
      {
      renderPipeline=new RenderingPipeline();
      }
    
    return renderPipeline;
    }
    
  public void draw(GameScreen screen)
    {
    String set=gameLayers.getRPZoneLayerSet();
    gameLayers.draw(screen,set+"_0_floor");
    gameLayers.draw(screen,set+"_1_terrain");
    gameLayers.draw(screen,set+"_2_object");
    gameObjects.draw(screen);
    gameLayers.draw(screen,set+"_3_roof");
    gameObjects.drawText(screen);
    }  
  }
