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
package games.stendhal.client.gui.j2d;

import java.awt.Color;

import org.junit.BeforeClass;
import org.junit.Test;
/**
 * Justs tests that various inputs don't throw exceptions. Not that the Bubble would look correct.
 */
public class TextBoxFactoryTest {
	private static TextBoxFactory factory;

	@BeforeClass
	public static void setup() {
		factory = new TextBoxFactory();
	}

	/**
	 * Short messages.
	 */
	@Test
	public void testBasic() {
		factory.createTextBox("short", 240, Color.black, Color.black, false);
		factory.createTextBox("dumdidum blah", 240, Color.black, Color.black, false);
	}

	/**
	 * Basic long word tests.
	 */
	@Test
	public void testLongWords() {
		factory.createTextBox("someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway",
				240, Color.black, Color.black, false);
		factory.createTextBox("prefix someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway",
				240, Color.black, Color.black, false);
		factory.createTextBox("someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway postfix",
				240, Color.black, Color.black, false);
		factory.createTextBox("prefix someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway postfix",
				240, Color.black, Color.black, false);
	}

	/**
	 * Pathological outputs that have at least at some point of time crashed the tokenizer.
	 * Add new finds here, unless they can be reasonably categorized otherwise.
	 */
	@Test
	public void testCrashes() {
		// Old /listquests output
		String msg = "[01:30] Open Quests: DailyMonsterQuestMeetHackimSevenCherubsStuffForVulcanusSuntanCreamForZaraToysCollector\nCompleted Quests: ArmorForDagobertCampfireHerbsForCarmenIntroducePlayersLearnAboutKarmaLearnAboutOrbsMeetHayunnMeetIoPlinksToyReverseArrowZooFood";
		factory.createTextBox(msg, 240, Color.black, Color.black, false);
		// Long strings in /alterquest
		msg = "[13:13] Admin kiheru changed your state of the quest 'ultimate_collector' from '29292992929dddddddddddddddddddddddddd' to '29292992929dddddddddddddddddddddddddd'";
		factory.createTextBox(msg, 240, Color.black, Color.black, false);
	}
}
