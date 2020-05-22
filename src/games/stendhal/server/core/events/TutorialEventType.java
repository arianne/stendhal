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
package games.stendhal.server.core.events;

/**
 * Event types used in the tutorial.
 *
 * @author hendrik
 */
public enum TutorialEventType {

	FIRST_LOGIN(
			"Hi, welcome to Stendhal. You can move around using the arrow keys or by clicking the mouse."),
	FIRST_MOVE(
			"You can talk to Hayunn Naratha by saying \"hi\"."),
	RETURN_GUARDHOUSE(
			"Talk to Hayunn Naratha again by saying \"hi\"."),
	VISIT_SEMOS_CITY(
			"You can get a map of Semos from Monogenes. Start by saying \"hi\". Or you can go down the steps to the dungeons and fight some creatures."),
	VISIT_SEMOS_DUNGEON(
			"Remember to eat regularly while you fight creatures. Double click on any cheese, meat or other food you have."),
	VISIT_SEMOS_DUNGEON_2(
			"Be careful. If you walk deeper and deeper, the creatures will get more powerful. You can run back to Semos to get healed by Carmen."),
	VISIT_SEMOS_TAVERN(
			"You can trade with an NPC by saying \"hi\" then asking for their \"offer\". If you want to buy a flask, say \"buy flask\"."),
	VISIT_SEMOS_PLAINS(
			"Eating regularly is essential to restore your health. If you are short of food, visit the farm east and then north of here."),
	FIRST_ATTACKED(
			"That creature with the yellow circle is attacking you! Click on it to fight back."),
	FIRST_KILL(
			"Click on items in corpses to transfer them to your bag."),
    FIRST_PLAYER_KILL(
			"You have been marked with the red skull of a player killer. You may find that people are wary of you now. To get it removed, you may speak to Io Flotto in Semos temple."),
	FIRST_POISONED(
			"You've just been poisoned. If you didn't drink poison, it was probably a poisonous creature attacking you. Kill poisonous creatures quickly, as you lose more HP each time you are poisoned."),
	FIRST_PLAYER(
			"Have you noticed that this name is printed in white? It is another real human player."),
	FIRST_DEATH(
			"Oh, you have just died. But fortunately death is not permanent in this world."),
	FIRST_PRIVATE_MESSAGE(
			"You received a private message. To reply, Use #/msg #name #message."),
	FIRST_EQUIPPED(
			"You just got given something! Check your bag and hands."),
	TIMED_HELP(
			"You can find a manual with many pictures and a beginner's guide in the Menu (see upper right hand corner of the Stendhal window)."),
	TIMED_NAKED(
			"Oh, aren't you feeling cold? Right click on yourself and choose \"Set Outfit\" to get dressed."),
	TIMED_PASSWORD(
			"Remember to keep your password completely secret, never tell it to another friend, player, or even admin."),
	TIMED_OUTFIT(
			"Do you like your outfit? If not, you can change it. Right click on yourself and choose \"Set Outfit\" to experiment with new hair, face, clothes and body."),
	TIMED_RULES(
			"Thank you for continuing to play Stendhal. Now that you have played for some time, it's important that you read the rules, please type #/rules and they will open in a browser.");
	private String message;

	/**
	 * Creates a new TutorialEventType.
	 *
	 * @param message
	 *            human readable message
	 */
	private TutorialEventType(final String message) {
		this.message = message;
	}

	/**
	 * Gets the descriptive message.
	 *
	 * @return message
	 */
	String getMessage() {
		return message;
	}
}
