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
package games.stendhal.client;

import java.awt.*;

/**
 * A sprite to be displayed on the screen. Note that a sprite
 * contains no state information, i.e. its just the image and 
 * not the location. This allows us to use a single sprite in
 * lots of different places without having to store multiple 
 * copies of the image.
 * 
 * @author Kevin Glass
 */
public class Sprite
  {
	/** The image to be drawn for this sprite */
	protected Image image;
	
	/**
	 * Create a new sprite based on an image
	 * 
	 * @param image The image that is this sprite
	 */
    public Sprite(Image image) {
		this.image = image;
	}
   
   public Graphics getGraphics() {
    return image.getGraphics();
  }
   
  public Sprite copy() {
    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Image image = gc.createCompatibleImage(getWidth(),getHeight(),Transparency.BITMASK);
    draw(image.getGraphics(),0,0);
    return new Sprite(image);
  }
	
	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null);
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null);
	}
	
	/**
	 * Draw the sprite onto the graphics context provided
	 * 
	 * @param g The graphics context on which to draw the sprite
	 * @param x The x location at which to draw the sprite
	 * @param y The y location at which to draw the sprite
	 */
	public void draw(Graphics g,int x,int y) {
		g.drawImage(image,x,y,null);
	}

    public void draw(Graphics g, int destx, int desty, int x,int y) {
        //g.drawImage(image,destx,desty,image.getWidth(null),image.getHeight(null),x,y,x+image.getWidth(null),y+image.getHeight(null),null);
        g.drawImage(image,destx,desty,w,h,x,y,x+w,y+h,null);
    }
}