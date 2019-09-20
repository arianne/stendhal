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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * splits an image
 *
 * @author hendrik
 */
public class ImageSplit {
    private final String imageFilename;
    private final String targetFolder;
    private final String prefix;
    private final int tileSizeSource;
    private final int tileSizeTarget;
    private final int offsetX;
    private final int offsetY;
    private final int cutOff;

    /**
     * creates a new ImageSplit
     *
     * @param imageFilename image to split
     * @param targetFolder  target folder to store the split image ilfes
     * @param prefix prefix for file names e.g. zoom level
     * @param tileSizeTarget size in px for the target files
     * @param offsetX offset for the x-coordinate in the target filenames
     * @param offsetY offset for the y-coordinate in the target filenames
     * @param cutOff number of pixels to cut of at each side of the original image
     */
    public ImageSplit(String imageFilename, String targetFolder, String prefix, int tileSizeSource, int tileSizeTarget, int offsetX, int offsetY, int cutOff) {
        this.imageFilename = imageFilename;
        this.targetFolder = targetFolder;
        this.prefix = prefix;
        this.tileSizeSource = tileSizeSource;
        this.tileSizeTarget = tileSizeTarget;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.cutOff = cutOff;
    }

    /**
     * splits the large image into smaller ones
     * @throws IOException
     */
    public void split() throws IOException {
        System.out.println(prefix);
        BufferedImage img = ImageIO.read(new File(imageFilename));
        Color bgColor = new Color(255, 255, 255);
        for (int x = 0; x < divRoundingUp(img.getWidth(), tileSizeSource); x++) {
            for (int y = 0; y < divRoundingUp(img.getHeight(), tileSizeSource); y++) {
                BufferedImage target = new BufferedImage(tileSizeTarget, tileSizeTarget, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = target.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics.setColor(bgColor);
                graphics.fillRect(0, 0, tileSizeTarget, tileSizeTarget);
                graphics.drawImage(img, 0, 0, tileSizeTarget, tileSizeTarget,
                    x * tileSizeSource + cutOff, y * tileSizeSource + cutOff,
                    (x+1) * tileSizeSource + cutOff, (y+1) * tileSizeSource + cutOff,
                bgColor, null);
                OutputStream out = new FileOutputStream(targetFolder + "/" + prefix + (x + offsetX) + "-" + (y + offsetY) + ".png");
                ImageIO.write(target, "png", out);
                out.close();
                graphics.dispose();
            }
        }
    }

    /**
     * an integer division which rounds up
     *
     * @param i number to divide
     * @param j number to divide by
     * @return result
     */
    public static int divRoundingUp(int i, int j) {
        int res = i / j;
        if (i % j > 0) {
            res++;
        }
        return res;
    }

    /**
     * entrance point
     *
     * @param args command line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new ImageSplit("/tmp/world.png", "/tmp/map", "0-", 8192, 256, 0, 0, 64).split();
        new ImageSplit("/tmp/world.png", "/tmp/map", "1-", 4096, 256, 0, 0, 64).split();
        new ImageSplit("/tmp/world.png", "/tmp/map", "2-", 2048, 256, 0, 0, 64).split();
        new ImageSplit("/tmp/world.png", "/tmp/map", "3-", 1024, 256, 0, 0, 64).split();
        new ImageSplit("/tmp/world.png", "/tmp/map", "4-",  512, 256, 0, 0, 64).split();
        new ImageSplit("/tmp/world-large.png", "/tmp/map", "5-", 1024, 256, 0, 0, 256).split();
        new ImageSplit("/tmp/world-large.png", "/tmp/map", "6-", 512, 256, 0, 0, 256).split();
        new ImageSplit("/tmp/world-large.png", "/tmp/map", "7-", 256, 256, 0, 0, 256).split();
    }
}
