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

package games.stendhal.client.sound;

/**
 * Hardcoded ambient sounds
 * 
 * @author Jane Hunt
 */
class AmbientStore {

	private static final String BUSHBIRD_MIX_1 = "bushbird-mix-1";
	private static final String BLACKBIRD_MIX = "blackbird-mix";
	public static final String BLACKBIRD_1 = "blackbird-1";

	/**
	 * create an Ambient sound by its name
	 * 
	 * @param name
	 *            the String representing the sound's name
	 * @return a new instance of Ambient sound or
	 *         <p>
	 *         <b> null </b> if name is not found
	 */
	public static AmbientSound getAmbient(String name) {
		AmbientSound ambient = null;

		if (name.equals("wind-tree-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addLoop("wind-loop-1", 25, 0);
			ambient.addLoop("wind-loop-1", 25, 500);
			ambient.addLoop("wind-loop-2", 50, 0);
			ambient.addCycle("treecreak-1", 25000, 10, 30, 100);
		} else if (name.equals("water-beach-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle("water-splash-1", 20000, 10, 35, 75);
			ambient.addCycle("water-splash-2", 60000, 10, 50, 80);
			ambient.addCycle("water-wave-1", 60000, 10, 30, 80);
		} else if (name.equals("water-flow-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addLoop("water-flow-1", 50, 0);
		} else if (name.equals("meadow-larks-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle("lark-1", 120000, 10, 40, 80);
			ambient.addCycle("lark-2", 120000, 10, 40, 80);
		} else if (name.equals("blackbirds-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle(AmbientStore.BLACKBIRD_1, 120000, 30, 80, 80);
			ambient.addCycle(AmbientStore.BLACKBIRD_MIX, 120000, 40, 80, 80);
		} else if (name.equals("bushbirds-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle(AmbientStore.BUSHBIRD_MIX_1, 45000, 50, 90, 75);
			ambient.addCycle(AmbientStore.BUSHBIRD_MIX_1, 45000, 40, 80, 75);
		} else if (name.equals("chicken-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle("chicken-mix", 45000, 10, 30, 90);
		} else if (name.equals("single-frog-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle("frog-1", 30000, 10, 40, 80);
		} else if (name.equals("build-works-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle("smith-mix", 60000, 15, 50, 70);
		} else if (name.equals("tavern-noise-1")) {
			ambient = new AmbientSound(name, 100);
			ambient.addCycle("tavern-mix", 80000, 10, 50, 75);
			ambient.addCycle("tavern-mix", 80000, 10, 50, 75);
			ambient.addCycle("tavern-mix", 80000, 10, 50, 75);
		}

		return ambient;
	}
}
