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
package games.stendhal.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.util.Collection;
import java.util.ArrayList;

import games.stendhal.server.Path;
/**
 *
 * @author Loïc
 */
public class NavigationPoints {

  public Collection<Path.Node> navPoints;
  private int height;
  private int width;

  public void setNavigationPoints(Reader reader) throws IOException
    {
    BufferedReader file=new BufferedReader(reader);
    String text=file.readLine();
    String[] size=text.split(" ");
    width=Integer.parseInt(size[0]);
    height=Integer.parseInt(size[1]);
    
    navPoints = new ArrayList<Path.Node>();
    
    int j=0;
    
    while((text=file.readLine())!=null)
      {
      if(text.trim().equals(""))
        {
        break;
        }
        
      String[] items=text.split(",");
      for(String item: items)
        {
		  if(Integer.parseInt(item)==2401)
		  {
			navPoints.add(new Path.Node(j % width, j / width));
		  }
        }
	  ++j;
      }
    }

  public int getHeight()
    {
    return height;
	}

  public int getWidth()
    {
    return width;
    }
}
