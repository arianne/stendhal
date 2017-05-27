/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

/**
 * A property for image events. Entities have no place to store the image name
 * (and can not have either, as many events at a time are possible), so we need
 * a way to carry the data to the entity view.
 */
public class ImageEventProperty extends Property {
	private final String image;

	/**
	 * Create a new ImageEventProperty.
	 *
	 * @param image image name
	 */
	public ImageEventProperty(String image) {
		this.image = image;
	}

	/**
	 * Get the image name the event handler should use.
	 *
	 * @return image name
	 */
	public String getImageName() {
		return image;
	}
}
