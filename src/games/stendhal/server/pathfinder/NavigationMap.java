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
package games.stendhal.server.pathfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.util.List;
import java.util.ArrayList;

/**
 * Stores all Navigation points. These are used to create streets.
 * @author Lo�c
 * @author mtotz
 */
public class NavigationMap
{
  private int height;
  private int width;
  private List<NavNode> navNodes;

  public NavigationMap()
  {
    navNodes = new ArrayList<NavNode>();
  }
  
  /** reads the navigation points from a default stendhal map file */
  public void setNavigationPoints(Reader reader) throws IOException
  {
    BufferedReader file=new BufferedReader(reader);
    String text=file.readLine();
    String[] size=text.split(" ");
    width=Integer.parseInt(size[0]);
    height=Integer.parseInt(size[1]);

    int j = 0;
    
    while ((text=file.readLine()) != null)
    {
      if (text.trim().equals(""))
      {
        break;
      }
      
      String[] items = text.split(",");
      for (String item: items)
      {
        if (Integer.parseInt(item) == 2401)
        {
          navNodes.add(new NavNode(j % width, j / width));
        }
        j++;
      }
    }
    
    // got all nav points
    // now calculate all neighbours
    for (NavNode node : navNodes)
    {
      for (NavNode innerNode : navNodes)
      {
        if (node.x != innerNode.x || node.y != innerNode.y)
        {
          node.checkNorthNode(innerNode);
          node.checkSouthhNode(innerNode);
          node.checkWesthNode(innerNode);
          node.checkEasthNode(innerNode);
        }
      }
    }
  }
  
  /** returns true when tile position is a street (between 2 navpoints )*/
  public boolean isStreet(int x, int y)
  {
    // TODO: speed this up
    for (NavNode node : navNodes)
    {
      // same column?
      if (node.x == x)
      {
        int dir = node.y - y;
        
        if (dir == 0)
          return true; // exactly the nav point
        
        if (dir < 0 && node.east != null)
          return true;

        if (dir > 0 && node.west != null)
          return true;
      }
      
      // same row?
      if (node.y == y)
      {
        int dir = node.x - x;
        
        if (dir == 0)
          return true; // exactly the nav point
        
        if (dir < 0 && node.south != null)
          return true;

        if (dir > 0 && node.north != null)
          return true;
      }
      
    }
    return false;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  /** a single navigation node */
  private class NavNode
  {
    public int x;
    public int y;
    
    /** north node or null */
    public NavNode north;
    /** south node or null */
    public NavNode south;
    /** east node or null */
    public NavNode east;
    /** west node or null */
    public NavNode west;
    
    /** creates a new nav-node */
    public NavNode(int x, int y)
    {
      this.x = x;
      this.y = y;
    }
    
    /** checks if the node is north and closer than the current one */
    public void checkNorthNode(NavNode otherNode)
    {
      // check if the nodes are on the same column
      if (this.x != otherNode.x)
        return; // not the same column
      
      // check if the node is north of us
      if (this.y - otherNode.y < 0)
        return; // the node is south of us
      
      // check if the node is closer than the current one
      if (this.north != null && (otherNode.y > this.north.y))
      {
        this.north = otherNode;
        // so we are the other nodes best south node
        otherNode.south = this;
      }
      else
      {
        this.north = otherNode;
      }
    }
    
    /** checks if the node is south and closer than the current one */
    public void checkSouthhNode(NavNode otherNode)
    {
      // check if the nodes are on the same column
      if (this.x != otherNode.x)
        return; // not the same column
      
      // check if the node is south of us
      if (this.y - otherNode.y > 0)
        return; // the node is north of us

      // check if the node is closer than the current one
      if (this.south != null && (otherNode.y < this.south.y))
      {
        this.south = otherNode;
        // so we are the other nodes best north node
        otherNode.north = this;
      }
      else
      {
        this.south = otherNode;
      }
    }

    /** checks if the node is west and closer than the current one */
    public void checkWesthNode(NavNode otherNode)
    {
      // check if the nodes are on the same row
      if (this.y != otherNode.y)
        return; // not the same row
      
      // check if the node is west of us
      if (this.x - otherNode.x > 0)
        return; // the node is east of us

      // check if the node is closer than the current one
      if (this.west != null && (otherNode.x > this.west.x))
      {
        this.west = otherNode;
        // so we are the other nodes best east node
        otherNode.east = this;
      }
      else
      {
        this.west = otherNode;
      }
    }

    /** checks if the node is east and closer than the current one */
    public void checkEasthNode(NavNode otherNode)
    {
      // check if the nodes are on the same row
      if (this.y != otherNode.y)
        return; // not the same row
      
      // check if the node is east of us
      if (this.x - otherNode.x < 0)
        return; // the node is west of us

      // check if the node is closer than the current one
      if (this.east != null && (otherNode.x < this.east.x))
      {
        this.east = otherNode;
        // so we are the other nodes best east node
        otherNode.west = this;
      }
      else
      {
        this.east = otherNode;
      }
    }
    /** returns a string representation */
    public String toString()
    {
      StringBuilder buf = new StringBuilder();

      buf.append("[(").append(x).append(',').append(y).append(") ");
      buf.append(north == null ? "-" : "n");
      buf.append(east  == null ? "-" : "e");
      buf.append(south == null ? "-" : "s");
      buf.append(west  == null ? "-" : "w");
      return buf.toString();
    }
  }
}
