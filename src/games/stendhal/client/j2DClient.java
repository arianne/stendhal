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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.net.*;
import java.io.*;

import marauroa.client.*;
import marauroa.client.net.*;
import marauroa.common.net.*;
import marauroa.common.game.*;

import javax.swing.*;

public class j2DClient extends Canvas 
  {
  private GameScreen screen;
        
  private BufferStrategy strategy;
  private boolean gameRunning=true;
	
  private StendhalClient client;

  public j2DClient() 
    {
	// create a frame to contain our game
	JFrame container = new JFrame("Stendhal Java 2D");
		
	// get hold the content of the frame and set up the resolution of the game
	JPanel panel = (JPanel) container.getContentPane();
	panel.setPreferredSize(new Dimension(640,480));
	panel.setLayout(null);
		
	// setup our canvas size and put it into the content of the frame
	setBounds(0,0,640,480);
	panel.add(this);
		
	// Tell AWT not to bother repainting our canvas since we're
	// going to do that our self in accelerated mode
	setIgnoreRepaint(true);
		
	// finally make the window visible 
	container.pack();
	container.setResizable(false);
	container.setVisible(true);
	
	// add a listener to respond to the user closing the window. If they
	// do we'd like to exit the game
	container.addWindowListener(new WindowAdapter() 
	  {
	  public void windowClosing(WindowEvent e) 
	    {
        gameRunning=false;
		}
	  });
		
    client=new StendhalClient();
   
    // add a key input system (defined below) to our canvas
	// so we can respond to key pressed
	addKeyListener(new StendhalKeyInputHandler(client));
		
	// request the focus so key events come to us
	requestFocus();

	// create the buffering strategy which will allow AWT
	// to manage our accelerated graphics
	createBufferStrategy(2);
	strategy = getBufferStrategy();
		
	GameScreen.createScreen(strategy,640,480);
    screen=GameScreen.get();
		
	// Start the main game loop, note: this method will not
	// return until the game has finished running. Hence we are
	// using the actual main thread to run the game.
	gameLoop();
	}
	
  public void gameLoop() 
    {
	long lastLoopTime = System.currentTimeMillis();
    int fps=0;
        
    try
      {
      client.connect("127.0.0.1",32160);
      }
    catch(SocketException e)
      {
      return;
      }
      
    client.login("miguel","password");
          
    StaticGameLayers staticLayers=client.getStaticGameLayers();
    GameObjects gameObjects=client.getGameObjects();

    long oldTime=System.nanoTime();

    screen.place(-100,-100);
        
    // keep looping round til the game ends
    while (gameRunning) 
      {
	  fps++;
	  // work out how long its been since the last update, this
	  // will be used to calculate how far the entities should
	  // move this loop
	  long delta = System.currentTimeMillis() - lastLoopTime;
	  lastLoopTime = System.currentTimeMillis();
			
      gameObjects.move(delta);
      
      staticLayers.draw(screen);
      gameObjects.draw(screen);
           
      Graphics2D g=screen.expose();
      g.setColor(Color.white);
      String message="Test of Stendhal running under Java";
	  g.drawString(message,(640-g.getFontMetrics().stringWidth(message))/2,200);
			
	  screen.nextFrame();
      moveScreen(client.getPlayer(),staticLayers);
			
      client.loop(0);

  	  if(System.nanoTime()-oldTime>1000000000)
	    {
	    oldTime=System.nanoTime();
	    System.out.println("FPS: "+fps);
	    fps=0;
        
        gameRunning=client.shouldContinueGame();
        }	    
   	  }

    client.logout();
    System.exit(0);
	}
  
  private void moveScreen(RPObject object, StaticGameLayers gameLayers)
    {
    // TODO: Fix me. It shouldn't follow pass layer end. 
    try
      {
      if(object==null)
        {
        return;
        }
        
      double x=object.getDouble("x");
      double y=object.getDouble("y");
      double dx=object.getDouble("dx");
      double dy=object.getDouble("dy");
      
      GameScreen screen=GameScreen.get();
      double screenx=screen.getX();
      double screeny=screen.getY();
      double screenw=screen.getWidth();
      double screenh=screen.getHeight();
      double sdx=screen.getdx();
      double sdy=screen.getdy();
      
      double layerw=gameLayers.getWidth();
      double layerh=gameLayers.getHeight();
      
      if((screenx+screenw/2)-x<2)
        {
        sdx+=0.1;
        }
        
      if((screenx+screenw/2)-x>-2)
        {
        sdx-=0.1;
        }
      
      if((screeny+screenh/2)-y<2)
        {
        sdy+=0.1;
        }
        
      if((screeny+screenh/2)-y>-2)
        {
        sdy-=0.1;
        }
      
      if(dx==0)
        {
        sdx=0;
        }

      if(dy==0)
        {
        sdy=0;
        }
      
      screen.move(sdx,sdy);
      }
    catch(AttributeNotFoundException e)    
      {
      }    
    }

  public static void main(String argv[]) 
    {
    new j2DClient();
	}
  }