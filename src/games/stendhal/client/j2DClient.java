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
		
	// add a key input system (defined below) to our canvas
	// so we can respond to key pressed
	addKeyListener(new KeyInputHandler());
		
	// request the focus so key events come to us
	requestFocus();

	// create the buffering strategy which will allow AWT
	// to manage our accelerated graphics
	createBufferStrategy(2);
	strategy = getBufferStrategy();
		
	GameScreen.createScreen(strategy,640,480);
    screen=GameScreen.get();
		
	// initialise the entities in our game so there's something to see at startup
	initialise();
		
	// Start the main game loop, note: this method will not
	// return until the game has finished running. Hence we are
	// using the actual main thread to run the game.
	gameLoop();
	}
	
  private void initialise() 
    {
    client=new StendhalClient();
    }
	
  public void gameLoop() 
    {
	long lastLoopTime = System.currentTimeMillis();
    int fps=0;
        
    StaticGameLayers staticLayers;
    StaticGameObjects staticObjects;
    RPObject player=null;    
        
    try
      {
      client.connect("127.0.0.1",32160);
      }
    catch(SocketException e)
      {
      return;
      }
      
    client.login("miguel","password");
          
    staticLayers=client.getStaticGameLayers();
    staticObjects=client.getStaticGameObjects();

    long oldTime=System.nanoTime();
    //screen.move(0.01,0.01);
    screen.place(20,20);
        
    // keep looping round til the game ends
    while (gameRunning) 
      {
	  fps++;
	  // work out how long its been since the last update, this
	  // will be used to calculate how far the entities should
	  // move this loop
	  long delta = System.currentTimeMillis() - lastLoopTime;
	  lastLoopTime = System.currentTimeMillis();
			
      staticObjects.move(delta);
      staticLayers.draw(screen);
      staticObjects.draw(screen);
           
      Graphics2D g=screen.expose();
      g.setColor(Color.white);
      String message="Test of Stendhal running under Java";
	  g.drawString(message,(640-g.getFontMetrics().stringWidth(message))/2,200);
			
	  screen.nextFrame();
			
      client.loop(0);

  	  if(System.nanoTime()-oldTime>1000000000)
	    {
	    oldTime=System.nanoTime();
	    System.out.println("FPS: "+fps);
	    fps=0;
	    }
   	  }

    client.logout();
    System.exit(0);
	}
	
  private class KeyInputHandler extends KeyAdapter 
    {
 	public void keyPressed(KeyEvent e) 
 	  {
 	  }
 	  
    public void keyReleased(KeyEvent e) 
      {
      }

    public void keyTyped(KeyEvent e) 
      {
	  // if we hit escape, then quit the game
	  if (e.getKeyChar() == 27) 
	    {
        gameRunning=false;
        }
      }
	}
	
	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
  public static void main(String argv[]) 
    {
    new j2DClient();
	}
  }