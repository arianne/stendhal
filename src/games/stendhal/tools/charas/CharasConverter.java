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
package games.stendhal.tools.charas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * <p>Utility to convert images saved from Charas to be suitable for Stendhal.
 * Basically this means making the background color transparent and scaling the
 * image using the scale2x algorithm.</p>
 *
 * <p>Usage:
 * <ol>
 * 	<li>command line mode: "java tools.CharasConverter source.png destination.png"</li>
 *  <li>graphical mode: start without any parameters to use the file selectors</li>
 * </ol>
 * </p>
 *
 * @author johnnnny
 */
public class CharasConverter {

	public static void main(String[] args) {
		CharasConverter converter = new CharasConverter();
		if (args.length == 0) {
			JFileChooser chooser = new JFileChooser(new File("."));
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname != null && (pathname.isDirectory()
							|| pathname.getAbsolutePath().toLowerCase()
								.endsWith(".png"));
				}

				@Override
				public String getDescription() {
					return "PNG Image Files (*.png)";
				}
			};
			while (true) {
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileFilter(filter);
				chooser.setDialogTitle("Select source image file");
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String srcFilename = chooser.getSelectedFile().getAbsolutePath();
					chooser.setDialogTitle("Select destination image file");
					chooser.setDialogType(JFileChooser.SAVE_DIALOG);
					returnVal = chooser.showSaveDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String dstFilename = chooser.getSelectedFile().getAbsolutePath();
						try {
							converter.convertImage(srcFilename, dstFilename);
							returnVal = JOptionPane.showOptionDialog(null,
									"Converted image saved to: " + dstFilename
									+ ".\nDo you want to convert another image?",
									"CharasConverter", JOptionPane.YES_NO_OPTION,
									JOptionPane.INFORMATION_MESSAGE, null, null, null);
							if (returnVal != JOptionPane.YES_OPTION) {
								break;
							}
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null,
									"Unable to convert image: " + e.getMessage(),
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						break;
					}
				} else {
					break;
				}
			}
		} else if (args.length == 2) {
			try {
				converter.convertImage(args[0], args[1]);
			} catch (IOException e) {
				System.err.println("Error: unable to convert image: " + e.getMessage());
			}
		} else {
			System.err.println("Usage: CharasConverter source.png destination.png");
		}
	}

	/**
	 * Convert a single image file to destination file.
	 *
	 * @param srcFilename
	 *            full path to source image file
	 * @param dstFilename
	 *            full path to destination image file
	 * @throws IOException
	 *             if loading or saving fails
	 */
	protected void convertImage(String srcFilename, String dstFilename) throws IOException {
		BufferedImage originalImage = loadImage(srcFilename);
		BufferedImage outputImage = getImageWithTransparency(originalImage, 0, 0);
		outputImage = getScaled2x(outputImage);
		savePNGImage(dstFilename, outputImage);
	}

	/**
	 * Get a copy of the image with a single color transparent. The color made
	 * to be transparent is specified by pixel.
	 *
	 * @param image
	 *            source image
	 * @param px
	 *            X index of the pixel for transparency color
	 * @param py
	 *            Y index of the pixel for transparency color
	 * @return Copy of the image with one color changed to transparent
	 */
	protected BufferedImage getImageWithTransparency(BufferedImage image, int px, int py) {
		int transparentRgb = image.getRGB(px, py);
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				if (rgb != transparentRgb) {
					result.setRGB(x, y, rgb);
				}
			}
		}
		return result;
	}

	/**
	 * Saves an image as PNG.
	 *
	 * @param filename
	 *            destination filename
	 * @param image
	 *            imagedata
	 * @throws IOException
	 *             if writing fails
	 */
	protected void savePNGImage(String filename, BufferedImage image) throws IOException {
		File file = new File(filename);
		ImageIO.write(image, "png", file);
	}

	/**
	 * Loads an image.
	 *
	 * @param filename the name of the file from which the image file
	 * @return loaded image
	 * @throws IOException if reading the file fails
	 */
	protected BufferedImage loadImage(String filename) throws IOException {
		File file = new File(filename);
		BufferedImage result = ImageIO.read(file);

		return result;
	}

	/**
	 * Scale an image to double size using the scale2x algorithm.
	 *
	 * @param srcImage
	 *            source image data
	 * @return scaled image
	 */
	protected BufferedImage getScaled2x(BufferedImage srcImage) {
		if (srcImage == null) {
			return null;
		}
		return ImageScale2x.getScaledImage(srcImage);
	}

}
