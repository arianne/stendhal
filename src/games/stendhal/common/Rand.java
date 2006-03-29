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

import java.util.Random;

public class Rand 
  {
  private static Random rand;
  
  static
    {
    rand=new Random();
    }
    
  public static int roll1D6()
    {
    return rand.nextInt(6)+1;
    }

  public static int roll1D20()
    {
    return rand.nextInt(20)+1;
    }

  public static int roll1D100()
    {
    return rand.nextInt(100)+1;
    }
  
  public static int rand(int max)
    {
    return rand.nextInt(max);
    }
  
  public static int rand(int n, int sd)
    {
    return (int)(rand.nextGaussian()*sd+n);
    }
  }
