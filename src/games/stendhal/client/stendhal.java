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

import games.stendhal.client.gui.StendhalFirstScreen;
import games.stendhal.client.gui.j2DClient;
import marauroa.common.Log4J;

public class stendhal extends Thread
  {
  public static boolean doLogin=false;
  
  public static final String[] SERVERS_LIST=
    {
    "stendhal.game-server.cc",
    "stendhal.ombres.ambre.net",
    "localhost"    
    };
  
  public static final String STENDHAL_FOLDER = "stendhal/";  
  public static final String VERSION="0.42";
  

  public static final boolean SHOW_COLLISION_DETECTION  = false;
  public static final boolean SHOW_EVERYONE_ATTACK_INFO = false;
  public static final boolean FILTER_ATTACK_MESSAGES = true;
  public static final int FPS_LIMIT = 20;

  public static void main(String args[]) 
    {
    
    Log4J.init("games/stendhal/log4j.properties");
    
    StendhalClient client=StendhalClient.get();
    new StendhalFirstScreen(client);
    
    while(!doLogin)
      {
      try{Thread.sleep(200);}catch(Exception e){}
      }
    
    new j2DClient(client);
    }    
  }
