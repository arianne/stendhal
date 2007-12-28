/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sprite {

	private List<KeyFrame> keys;
	KeyFrame currentKey = null;
	private int totalFrames = 0, borderWidth = 0, fpl = 0, totalKeys = -1, transparent = 0;

	private float currentFrame = 0;
	private Rectangle frameSize;
	private Image sprite = null;
	private boolean bPlaying = true;

	public Sprite() {
		frameSize = new Rectangle();
		keys = new ArrayList<KeyFrame>();
	}

	public Sprite(Image image, int fpl, int border, int totalFrames) {
		setImage(image);
		this.fpl = fpl;
		borderWidth = border;
		this.totalFrames = totalFrames;

		// given this information, extrapolate the rest...
		frameSize = new Rectangle(0, 0, 0, 0);
		frameSize.width = image.getWidth(null) / (fpl + borderWidth * fpl);
		frameSize.height = (int) (image.getHeight(null) / (Math.ceil(totalFrames / fpl) + Math.ceil(totalFrames / fpl)
				* borderWidth));
		keys = new ArrayList<KeyFrame>();
	}

	public void setImage(Image i) {
		sprite = i;
	}

	public Image getImage() {
		return sprite;
	}

	public void setFrameSize(int w, int h) {
		frameSize.width = w;
		frameSize.height = h;
	}

	public void setTotalFrames(int f) {
		totalFrames = f;
	}

	public void setBorderWidth(int b) {
		borderWidth = b;
	}

	public void setFpl(int f) {
		fpl = f;
	}

	public void setCurrentFrame(int c) {
		currentFrame = c;
	}

	public void setTotalKeys(int t) {
		totalKeys = t;
	}

	public void setTransparentColor(int t) {
		transparent = t;
	}

	public int getTransparentColor() {
		return transparent;
	}

	public Rectangle getFrameSize() {
		return (frameSize);
	}

	public int getTotalFrames() {
		return (totalFrames);
	}

	public int getBorderWidth() {
		return (borderWidth);
	}

	public int getCurrentFrame() {
		return ((int) currentFrame);
	}

	public KeyFrame getCurrentKey() {
		return currentKey;
	}

	public int getFPL() {
		return fpl;
	}

	public int getTotalKeys() {
		return keys.size();
	}

	public void setKeyFrameTo(String name) {
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			KeyFrame k = (KeyFrame) itr.next();
			if (k.equalsIgnoreCase(name)) {
				currentKey = k;
				break;
			}
		}
	}

	public void addKey(KeyFrame k) {
		keys.add(k);
	}

	public void removeKey(String name) {
		// TODO: this function
	}

	public void createKey(String name, int start, int end, long flags) {
		KeyFrame kf = new KeyFrame();
		kf.setName(name);
		kf.setFlags(flags);
		kf.setStartFinish(start, end);
		addKey(kf);
	}

	public void iterateFrame() {

		if (currentKey != null) {
			if (bPlaying) {
				currentFrame += currentKey.getFrameRate();
			}

			if ((int) currentFrame > currentKey.getFinishFrame()) {
				if ((currentKey.getFlags() & KeyFrame.KEY_LOOP) == KeyFrame.KEY_LOOP) {
					currentFrame = currentKey.getStartFrame();
				} else if ((currentKey.getFlags() & KeyFrame.KEY_REVERSE) == KeyFrame.KEY_REVERSE) {
					currentKey.setFrameRate(-currentKey.getFrameRate());
				} else if ((currentKey.getFlags() & KeyFrame.KEY_AUTO) == KeyFrame.KEY_AUTO) {
					// TODO: need to iterate to the next key
					if (currentKey != null) {
						currentFrame = currentKey.getStartFrame();
					}
				} else {
					currentFrame = currentKey.getFinishFrame();
					bPlaying = false;
				}
			} else if ((int) currentFrame < currentKey.getStartFrame()) {
				if ((currentKey.getFlags() & KeyFrame.KEY_LOOP) == KeyFrame.KEY_LOOP) {
					currentFrame = currentKey.getFinishFrame();
				} else if ((currentKey.getFlags() & KeyFrame.KEY_REVERSE) == KeyFrame.KEY_REVERSE) {
					currentKey.setFrameRate(-currentKey.getFrameRate());
				} else {
					bPlaying = false;
				}
			}

		}
	}

	public void keySetFrame(int c) {
		setCurrentFrame(currentKey.getStartFrame() + c);
	}

	public void play() {
		bPlaying = true;
	}

	public void stop() {
		bPlaying = false;
	}

	public void keyStepBack(int amt) {
		if (currentFrame - amt < currentKey.getStartFrame()) {
			setCurrentFrame(currentKey.getStartFrame());
		} else {
			setCurrentFrame((int) (currentFrame - amt));
		}
	}

	public void keyStepForward(int amt) {
		if (currentFrame + amt > currentKey.getFinishFrame()) {
			setCurrentFrame(currentKey.getFinishFrame());
		} else {
			setCurrentFrame((int) (currentFrame + amt));
		}
	}

	public KeyFrame getKey(String keyName) {
		Iterator<KeyFrame> itr = keys.iterator();
		while (itr.hasNext()) {
			KeyFrame k = itr.next();
			if (k.equalsIgnoreCase(keyName)) {
				return k;
			}
		}
		return null;
	}

	public KeyFrame getKey(int i) {
		return keys.get(i);
	}

	public String[] getKeys() throws Exception {
		Iterator<KeyFrame> itr = keys.iterator();

		String[] s = new String[getTotalKeys() + 1];
		int i = 0;
		while (itr.hasNext()) {
			KeyFrame k = itr.next();
			s[i++] = k.getName();
		}
		return s;
	}

	public void draw(Graphics g) {
		int x = 0, y = 0;

		if (frameSize.height > 0 && frameSize.width > 0) {
			y = (((int) currentFrame) / fpl) * (frameSize.height + borderWidth);
			x = (((int) currentFrame) % fpl) * (frameSize.width + borderWidth);

			g.drawImage(sprite, 0, 0, frameSize.width, frameSize.height, x, y, frameSize.width + x, frameSize.height
					+ y, null);
		}
	}

	public void drawAll(Graphics g) {
		g.drawImage(sprite, 0, 0, null);
	}

	public String toString() {
		String s = null;
		s = "Frame: (" + frameSize.width + "x" + frameSize.height + ")\nBorder: " + borderWidth + "\nFPL: " + fpl
				+ "\nTotal Frames: " + totalFrames + "\nTotal keys: " + totalKeys;
		return s;
	}

}
