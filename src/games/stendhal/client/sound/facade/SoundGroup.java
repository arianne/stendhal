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
package games.stendhal.client.sound.facade;


/**
 * a group of sounds
 *
 * @author hendrik
 */
public interface SoundGroup {

	/**
	 * Loads a sound
	 *
	 * @param name     name of sound
	 * @param fileURI  "audio:/"
	 * @param fileType Type.OGG
	 * @param enableStreaming should streaming be enabled?
	 * @return true, if the sound could be loaded; false otherwise.
	 */
	public boolean loadSound(String name, String fileURI, SoundFileType fileType, boolean enableStreaming);

	/**
	 * plays a sound
	 *
	 * @param soundName  name of sound
	 * @param layerLevel on which layer should the sound be played
	 * @param area       in which area is the sound hearable?
	 * @param fadeInDuration time the sound will fade in
	 * @param autoRepeat  should the sound be played in a loop?
	 * @param clone       should the sound be cloned for manipulation?
	 * @return a handle to the sound, so that it can be cancled in case of looped sounds
	 */
	public SoundHandle play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone);

	/**
	 * plays a sound
	 *
	 * @param soundName  name of sound
	 * @param volume     volumne
	 * @param layerLevel on which layer should the sound be played
	 * @param area       in which area is the sound hearable?
	 * @param fadeInDuration time the sound will fade in
	 * @param autoRepeat  should the sound be played in a loop?
	 * @param clone       should the sound be cloned for manipulation?
	 * @return a handle to the sound, so that it can be cancled in case of looped sounds
	 */
	public SoundHandle play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone);

	/**
	 * gets the current volumne
	 *
	 * @return volumne
	 */
	public float getVolume();

	/**
	 * changes the volumne
	 *
	 * @param volume volume
	 */
	public void changeVolume(float volume);

	/**
	 * enables streaming for this group.
	 */
	public void enableStreaming();

}
