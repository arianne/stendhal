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

import games.stendhal.client.gui.*;

public class stendhal extends Thread
  {
  public static boolean doLogin=false;
  
  final public static boolean showCollisionDetection=false;
  final public static boolean showEveryoneAttackInfo=false;
  final public static boolean showEveryoneXPInfo=false;
  
  final public static String VERSION="0.25";
  
  public static void main(String args[]) 
    {
    StendhalClient client=StendhalClient.get();
    new StendhalFirstScreen(client);
    
    while(!doLogin)
      {
      try{Thread.sleep(200);}catch(Exception e){}
      }
    
    new j2DClient(client);
    }    
  }
