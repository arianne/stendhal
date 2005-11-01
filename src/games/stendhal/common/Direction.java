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
package games.stendhal.common;


public enum Direction 
  {
  STOP(0),
  UP(1),
  RIGHT(2),
  DOWN(3),
  LEFT(4);
  
  private final int val;
  
  public static Direction build(int val)
    {
    if(val==1) 
      {
      return UP;
      }
    else if(val==2) 
      {
      return RIGHT;
      }
    else if(val==3) 
      {
      return DOWN;
      }
    else if(val==4) 
      {
      return LEFT;
      }
    else
      {
      return STOP;
      }
    }
  
  public int getdx()
    {
    if(val==2) return 1;
    if(val==4) return -1;
    return 0;
    }
  
  public int getdy()
    {
    if(val==1) return -1;
    if(val==3) return 1;
    return 0;
    }
    
  public static Direction rand()
    {
    return build(Rand.rand(4)+1);
    }
  
  Direction(int val)
    {
    this.val=val;
    }
   
  public int get()
    {
    return val;
    }
  }
