/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotations for the StendhalDeveloper.
 *
 * At the class level it indicates to include the marked class
 * and to sort it into the specified category.
 *
 * If at least one constructor is marked, only marked constructors
 * are used, unmarked ones are ignored.
 *
 * At the parameter level it can be used to specify a default value.
 *
 * @author hendrik
 */
@Documented
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE})
public @interface Dev {

	/** the category of this building block */
	Category category() default Category.IGNORE;

	/** an optional default value */
	String defaultValue() default "";

	/** a short label */
	String label() default "";

	/**
	 * a category for building block
	 *
	 * @author hendrik
	 */
	public static enum Category {
		/** chatting, emoting and private messages */
		CHAT,
		/** environment such as java system properties */
		ENVIRONMENT,
		/** location in the world, zones */
		LOCATION,
		/** logic true, false, and, or, not */
		LOGIC,
		/** do not show this building block in the designer */
		IGNORE,
		/** player outfits */
		OUTFIT,
		/** quest slot memory */
		QUEST_SLOT,
		/** items looted by the player in the past */
		ITEMS_LOOTED,
		/** items owned by the player right now*/
		ITEMS_OWNED,
		/** item producer */
		ITEMS_PRODUCER,
		/** stats of the player, e. g. level, karma */
		STATS,
		/** kills of creatures */
		KILLS,
		/** time */
		TIME,
		/** none of the above */
		OTHER;
	}
}
