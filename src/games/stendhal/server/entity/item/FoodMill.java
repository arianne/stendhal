/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;

import java.util.Map;

import marauroa.common.game.RPObject;

public class FoodMill extends Item implements UseListener {

    public FoodMill(final String name, final String clazz,
            final String subclass, final Map<String, String> attributes) {
        super(name, clazz, subclass, attributes);
    }

    public FoodMill(final FoodMill item) {
        super(item);
    }

    public boolean onUsed(final RPEntity user) {
        if (isContained()) {
            final String slotName = getContainerSlot().getName();
            if (slotName.endsWith("hand")) {
                String otherhand;
                if ("rhand".equals(slotName)) {
                    otherhand = "lhand";
                } else {
                    otherhand = "rhand";
                }
                final RPObject first = user.getSlot(otherhand).getFirst();
                if (first != null) {
                    if ("sugar mill".equals(getName())) {
                        /**
                         * the player needs to equip at least a sugar cane in his other hand
                         * and have an empty sack in his inventory
                         */
                        if ("cane".equals(first.get("name"))) {
                            if (user.isEquipped("empty sack")) {
                                final Item item = SingletonRepository.getEntityManager().getItem("sugar");
                                user.drop("cane");
                                user.drop("empty sack");
                                user.equipOrPutOnGround(item);
                            } else {
                                user.sendPrivateText("You don't have an empty sack with you");
                            }
                        } else {
                            user.sendPrivateText("You need to have at least a sugar cane in your other hand");
                        }
                    } else {
                        /**
                         * the player needs to equip at least a sugar cane in his other hand
                         * and have an empty flask in his inventory
                         */
                        if ("apple".equals(first.get("name"))) {
                            if (user.isEquipped("flask")) {
                                final Item item = SingletonRepository.getEntityManager().getItem("apple juice");
                                user.drop("apple");
                                user.drop("flask");
                                user.equipOrPutOnGround(item);
                            } else {
                                user.sendPrivateText("You don't have an empty flask with you");
                            }
                        } else {
                            user.sendPrivateText("You need to have at least an apple in your other hand");
                        }
                    }
                } else {
                    user.sendPrivateText("Your other hand looks empty.");
                }
            } else {
                user.sendPrivateText("You should first equip the " + getName() + " in either hand in order to use it.");
            }
            return true;
        } else {
            user.sendPrivateText("You should be carrying the " + getName() + " in order to use it.");
            return false;
        }
    }
}
