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

/** The main class that create the screen and starts the arianne client. */
public class j2DClient extends Canvas 
  {
  private GameScreen screen;
        
  private boolean gameRunning=true;
	
  private StendhalClient client;
  
  private String host;
  private String username;
  private String password;

  public j2DClient(String host, String username, String password) 
    {
    this.host=host;
    this.username=username;
    this.password=password;
    
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
    BufferStrategy strategy;
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
      client.connect(host,32160);
      }
    catch(SocketException e)
      {
      return;
      }
      
    client.login(username,password);
          
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
      client.loop(0);

      moveScreen(client.getPlayer(),staticLayers,delta);
           
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
  
  private void moveScreen(RPObject object, StaticGameLayers gameLayers, long delta)
    {
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
      
      double dsx=screenx+screenw/2;
      double dsy=screeny+screenh/2;
      
      double layerw=gameLayers.getWidth();
      double layerh=gameLayers.getHeight();
      
      if(dsx-x<-2)
        {
        sdx+=0.1;
        }
      else if(dsx-x>-0.5 && dsx-x<0.5)
        {
        sdx/=1.1;
        }
      else if(dsx-x>2)
        {
        sdx-=0.1;
        }


      if(dsy-y<-2)
        {
        sdy+=0.1;
        }
      else if(dsy-y>-0.5 && dsy-y<0.5)
        {
        sdy/=1.1;
        }
      else if(dsy-y>2)
        {
        sdy-=0.1;
        }
      
      screen.move(sdx,sdy);
      }
    catch(AttributeNotFoundException e)    
      {
      }    
    }

  public static void main(String args[]) 
    {
    if(args.length>0)
      {
      int i=0;
      String username=null;
      String password=null;
      String host=null;
    
      while(i!=args.length)
        {
        if(args[i].equals("-u"))
          {
          username=args[i+1];
          }
        else if(args[i].equals("-p"))
          {
          password=args[i+1];
          }
        else if(args[i].equals("-h"))
          {
          host=args[i+1];
          }
        i++;
        }        
          
      if(username!=null && password!=null && host!=null)
        {
        new j2DClient(host,username,password);
        return;
        }
      }

    System.out.println("Stendhal j2DClient");
    System.out.println();
    System.out.println("  games.stendhal.j2DClient -u username -p pass -h host -c character");
    System.out.println();
    System.out.println("Required parameters");
    System.out.println("* -h\tHost that is running Marauroa server");
    System.out.println("* -u\tUsername to log into Marauroa server");
    System.out.println("* -p\tPassword to log into Marauroa server");
	}
  }