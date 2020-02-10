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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.RPObject;

public class FoodMill extends Item {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(RPEntity.class);

	/** The item to be processed */
	private String input;
	/** The required container for processing */
	private String container;
	/** The resulting processed item */
	private String output;
	/** Items that do not require a "container". */
	private final List<String> containerNotRequired = new ArrayList<String>() {{
		add("scroll eraser");
		add("rotary cutter");
	}};

    public FoodMill(final String name, final String clazz,
            final String subclass, final Map<String, String> attributes) {
        super(name, clazz, subclass, attributes);
        init();
    }

    public FoodMill(final FoodMill item) {
        super(item);
        init();
    }

    /** Sets up the input, output and container based on item name */
    private void init() {
    	final String tool = getName();

    	if ("sugar mill".equals(tool)) {
    		input = "sugar cane";
    		container = "empty sack";
    		output = "sugar";
    	} else if ("scroll eraser".equals(tool)) {
    		input = "marked scroll";
    		output = "empty scroll";
    	} else if ("rotary cutter".equals(tool)) {
    		input = "pelt";
    		output = "leather thread";
    	} else {
    		input = "apple";
    		container = "bottle";
    		output = "apple juice";
    	}
    }

    @Override
	public boolean onUsed(final RPEntity user) {
    	final String tool = getName();
    	final boolean containerRequired = !containerNotRequired.contains(tool);

    	/* Items/Tools not listed in "containerNotRequired" must have a "container" defined. */
    	if (containerRequired && container == null) {
    		logger.error("Input \"" + input + "\" requires a container, but container value is null.");
    		return false;
    	}

    	/* is the mill equipped at all? */
    	if (!isContained()) {
    		user.sendPrivateText("You should be carrying the " + tool + " in order to use it.");
    		return false;
    	}

    	final String slotName = getContainerSlot().getName();

    	/* is it in a hand? */
    	if (!slotName.endsWith("hand")) {
    		user.sendPrivateText("You should hold the " + tool + " in either hand in order to use it.");
    		return false;
    	}

    	final String otherhand = getOtherHand(slotName);

    	final RPObject first = user.getSlot(otherhand).getFirst();

    	/* is anything in the other hand? */
    	if (first == null) {
    		user.sendPrivateText("Your other hand looks empty.");
    		return false;
    	}

    	/*
    	 * the player needs to equip at least the input in his other hand
    	 * and have the correct container in his inventory
    	 */
    	if (!input.equals(first.get("name"))) {
    		user.sendPrivateText("You need to have at least " + Grammar.a_noun(input) + " in your other hand");
    		return false;
    	}

    	if (containerRequired && !user.isEquipped(container)) {
    		user.sendPrivateText("You don't have " + Grammar.a_noun(container) + " with you");
    		return false;
    	}

        /* all is okay, lets process this item */
    	final Item item = SingletonRepository.getEntityManager().getItem(output);

    	if (first instanceof StackableItem) {
			StackableItem dropOneOfMe = (StackableItem) first;
			dropOneOfMe.removeOne();
		} else {
			user.drop((Item) first);
		}

    	if (containerRequired) {
    		user.drop(container);
    	}

    	if ("rotary cutter".equals(tool)) {
    		final StackableItem stackable = (StackableItem) item;
    		stackable.setQuantity(5);

    		user.equipOrPutOnGround(stackable);
    	} else {
    		user.equipOrPutOnGround(item);
    	}

    	return true;
    }


    /**
     * @param handSlot should be rhand or lhand
     * @return the opposite hand to handSlot
     */
    private String getOtherHand(final String handSlot) {
        if ("rhand".equals(handSlot)) {
            return "lhand";
        } else {
            return "rhand";
        }
    }

}
