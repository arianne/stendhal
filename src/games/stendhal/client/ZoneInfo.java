/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.awt.Color;
import java.awt.Composite;

import games.stendhal.client.sound.facade.Time;
import games.stendhal.client.sound.manager.SoundManagerNG.Sound;
import games.stendhal.common.constants.SoundLayer;

/**
 * General information about the current zone.
 */
public class ZoneInfo {
	/** Singleton instance. */
	private static final ZoneInfo instance = new ZoneInfo();

	/** Blend mode for coloring the zone, or <code>null</code>. */
	private Composite colorBlend;
	/** Blend mode for coloring effect layers, or <code>null</code>. */
	private Composite effectBlend;
	/** Color for the current zone, or <code>null</code>. */
	private Color color;

	/** Music instance played globally. */
	private static Sound globalMusic = null;
	private static final Time fadeTime = new Time(1, Time.Unit.SEC);


	/**
	 * Create a new ZoneInfo.
	 */
	private ZoneInfo() {
	}

	/**
	 * Get the ZoneInfo instance.
	 *
	 * @return zone info
	 */
	public static ZoneInfo get() {
		return instance;
	}

	/**
	 * Call when zone changes. Clears zone dependent data.
	 */
	void zoneChanged() {
		colorBlend = null;
		effectBlend = null;
		color = null;
	}

	/**
	 * Set the color blend method.
	 *
	 * @param method
	 */
	void setColorMethod(Composite method) {
		colorBlend = method;
	}

	/**
	 * Get the color blend method. Mode for applying the zone color to tile sets
	 * and entity sprites.
	 *
	 * @return blend mode
	 */
	public Composite getColorMethod() {
		return colorBlend;
	}

	/**
	 * Get the effect layer blend mode. Mode for blending the effect layers to
	 * the tiles underneath.
	 *
	 * @param blend
	 */
	void setEffectBlend(Composite blend) {
		effectBlend = blend;
	}

	/**
	 * Get the effect layer blend mode. Mode for blending the effect layers to
	 * the tiles underneath.
	 *
	 * @return blend mode
	 */
	Composite getEffectBlend() {
		return effectBlend;
	}

	/**
	 * Set zone specific color.
	 *
	 * @param rgb
	 */
	void setZoneColor(int rgb) {
		this.color = new Color(rgb);
	}

	/**
	 * Get the zone specific color. This should be applied to tile sets and
	 * most entities using the zone blend method.
	 *
	 * @return zone color
	 */
	public Color getZoneColor() {
		return color;
	}

	/**
	 * Loops a sound with uniform volume that continues playing across zone changes.
	 *
	 * Any currently playing instance will be stopped before starting a new one. Also, calling without
	 * parameters will stop current instance if playing.
	 *
	 * @param name
	 *   Sound file basename. If value is `null` music will be stopped.
	 * @param volume
	 *   Volume level between 0.0 and 1.0.
	 */
	public void setSingleGlobalizedMusic(final String musicName, final float volume) {
		if (ZoneInfo.globalMusic != null && ZoneInfo.globalMusic.getName().equals(musicName)) {
			// music is already playing so don't restart
			return;
		}
		if (ZoneInfo.globalMusic != null) {
			// stop previously playing
			ClientSingletonRepository.getSound().stop(ZoneInfo.globalMusic, ZoneInfo.fadeTime);
		}
		ZoneInfo.globalMusic = null;
		if (musicName != null) {
			ZoneInfo.globalMusic = (Sound) ClientSingletonRepository.getSound()
					.getGroup(SoundLayer.BACKGROUND_MUSIC.groupName)
					.play(musicName, volume, 0, null, ZoneInfo.fadeTime, true, false);
		}
	}
}
