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
package games.stendhal.server.entity.item;

import marauroa.common.game.*;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class Food extends StackableItem
  {
  private int quantity;
  
  public Food(String name, String clazz, String subclass, List<String> slots, Map<String, String> attributes)
    {
    super(name,clazz, subclass, slots, attributes);
    }
  
  public int getAmount()
    {
    return getInt("amount");
    }

  public boolean isStackable(Stackable other)
    {
    return (other.getClass() == Food.class);
    }
  }