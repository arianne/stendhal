/***************************************************************************
 *               (C) Copyright 2011-2019 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * extracts a defined area from a set of images and stores the results in another folder with the same name as the original files.
 *
 * @author hendrik
 */
public class ExtractGameArea {

	/**
	 * entry point
	 *
	 * @param args
	 * @throws IOException in case of an input output error
	 */
	public static void main(String[] args) throws IOException {

		for (File file : new File("/tmp/stendhal/").listFiles()) {
			new ExtractGameArea().extract("/tmp/stendhal/" + file.getName(), "/tmp/out/" + file.getName());
		}

	}

	private void extract(String input, String output) throws IOException {
		BufferedImage image = ImageIO.read(new File(input));

		int x = 140;
		int y = 22;
		int width=640;
		int height=480;

		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		target.createGraphics().drawImage(image, 0, 0, width, height, x, y, x+width, y+height, null);

		ImageIO.write(target, "png", new File(output));
	}
}
