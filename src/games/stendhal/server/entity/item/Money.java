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

public class Money extends StackableItem
  {
  private int quantity;
  
  public Money(String name, String clazz, List<String> slots, Map<String, String> attributes)
    {
    super("money","money", "gold", slots, attributes);
    update();
    }

  public Money(int quantity)
    {
    super("money","money", "gold", new LinkedList<String>(), null);
    put("quantity",quantity);

    this.quantity=quantity;
    }

  public boolean isStackable(Stackable other)
    {
    return (other.getClass() == Money.class);
    }
  }
