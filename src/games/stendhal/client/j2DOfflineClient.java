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
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.net.*;
import java.io.*;

import marauroa.client.*;
import marauroa.client.net.*;
import marauroa.common.net.*;
import marauroa.common.game.*;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic. 
 * 
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * 
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 * 
 * @author Kevin Glass
 */
public class j2DOfflineClient extends Canvas {
	/** The stragey that allows us to use accelerate page flipping */
    private GameScreen screen;
        
    private BufferStrategy strategy;
	private boolean gameRunning=true;
    private JTextField enterText;
	
    private boolean leftPressed=false, rightPressed=false, upPressed=false, downPressed=false;
    private JPanel panel;
	/**
	 * Construct our game and set it running.
	 */
    public j2DOfflineClient() {
		// create a frame to contain our game
		JFrame container = new JFrame("Stendhal Java 2D");
		
		// get hold the content of the frame and set up the resolution of the game
		panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(640,480));
		panel.setLayout(null);
    enterText = new JTextField("TestoFieldo");
    enterText.setSize(new Dimension(50,50));
    enterText.setBounds(10,10,60,60);
    enterText.setOpaque(false);
		
		// setup our canvas size and put it into the content of the frame
		setBounds(0,0,640,480);
		panel.add(this);
	  
    panel.add(enterText);
    	
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
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
		
		// initialise the entities in our game so there's something
		// to see at startup
		initialise();
		
		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		gameLoop();
	}
	
	/**
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 */
    private void initialise() 
      {
      }
	
	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
        int fps=0;
        
        StaticGameLayers staticLayers;
        GameObjects gameObjects;
        RPObject player=null;    
        
        staticLayers=new StaticGameLayers();
        gameObjects=new GameObjects();
        
        try
          {
          staticLayers.addLayer(new BufferedReader(new FileReader("maps/city_layer0.txt")),"city_0");
          staticLayers.addLayer(new BufferedReader(new FileReader("maps/city_layer1.txt")),"city_1");
          }
        catch(java.io.IOException e)          
          {
          e.printStackTrace();
          System.exit(0);
          }        
        
        staticLayers.setRPZoneLayersSet("city");

        for(int i=0;i<5;i++)
          {
          RPObject object=new RPObject(new RPObject.ID(1+i,"village"));
          object.put("type","pot");
          object.put("x",9+i);
          object.put("y",11);
          try
            {
            gameObjects.add(object);
            }
          catch(AttributeNotFoundException e)
            {
            }
          }        

        player=new RPObject(new RPObject.ID(10,"village"));
        player.put("type","player");
        player.put("name","testing");
        player.put("x",20);
        player.put("y",29);
        player.put("dx",0.80);
        player.put("dy",0);
        try
          {
          gameObjects.add(player);
          }
        catch(AttributeNotFoundException e)
          {
          }

        long oldTime=System.nanoTime();
        //screen.move(0.01,0.01);
        screen.place(20,20);
        
        // keep looping round til the game ends
		while (gameRunning) {
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
		  
      enterText.paint(g);
     	
			screen.nextFrame();
			
			if(System.nanoTime()-oldTime>1000000000)
			  {
			  oldTime=System.nanoTime();
			  System.out.println("FPS: "+fps);
			  fps=0;
			  }
		}

		System.exit(0);
	}
	
	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right 
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through 
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 * 
	 * @author Kevin Glass
	 */
	private class KeyInputHandler extends KeyAdapter {
		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed 
		 */
		public void keyPressed(KeyEvent e) {
        } 
		
		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released 
		 */
		public void keyReleased(KeyEvent e) {
        }

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed. 
		 */
		public void keyTyped(KeyEvent e) {
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
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
	public static void main(String argv[]) {
        new j2DOfflineClient();
	}
}
