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

/*
 * MapRenderer.java
 *
 * Created on 13. Oktober 2005, 18:24
 *
 */
package games.stendhal.tools;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;
import tiled.view.OrthogonalRenderer;

/**
 * Renders Stendhal maps from *.tmx into PNG files of the same base name. This class can be started
 * from the command line or through an ant task.
 *
 * @author mtotz
 */
public class MapRenderer extends Task {
	private String imagePath;

	/** list of *.tmx files to convert. */
	private final List<FileSet> filesets = new ArrayList<FileSet>();

	private double zoom;

	/** Layers that should be rendered on the map image. */
	private static final List<String> visibleLayers = Arrays.asList( "0_floor",
			"1_terrain", "2_object", "3_roof", "4_roof_add");

	/** converts the map files.
	 * @param tmxFile
	 * @throws Exception */
	public void convert(final String tmxFile) throws Exception {
		final File file = new File(tmxFile);

		final String filename = file.getAbsolutePath();
		final Map map = new TMXMapReader().readMap(filename);
		saveImageMap(map, tmxFile);
	}

	private void saveImageMap(final Map map, final String tmxFile) {
		final File file = new File(tmxFile);
		String filename = file.getAbsolutePath();
		Boolean isworld = file.getName().equals("world.tmx");

		for (final MapLayer layer : map) {
			if (isworld) {
				layer.setVisible(layer.getName().equals("Floor"));
			} else {
				layer.setVisible(visibleLayers.contains(layer.getName()));
			}
		}

		final String area = file.getParentFile().getName();
		String level = "int";
		final String fileContainer = file.getParentFile().getParent();

		if (isworld) {
			filename = filename.replaceAll("\\.tmx", ".png");
		} else {
			if (fileContainer.contains("Level ")) {
				level = fileContainer.split("Level ")[1];
			}

			if (level.equals("int") && area.equals("abstract")) {
				filename = imagePath + File.separator + level.replace("-", "sub_")
						+ "_" + file.getName().replaceAll("\\.tmx", ".png");
			} else {
				filename = imagePath + File.separator + level.replace("-", "sub_")
						+ "_" + area + "_"
						+ file.getName().replaceAll("\\.tmx", ".png");
			}
		}

		final OrthogonalRenderer myView = new OrthogonalRenderer(map);
		double realZoom;
		if (zoom > 0) {
			realZoom = zoom;
		} else if (level.equals("int") && !area.equals("abstract")) {
			realZoom = 0.25;
		} else {
			realZoom = 0.0625; // 1 / 16
		}
		final Dimension d = myView.getMapSize();
		BufferedImage i = null;
		int width = d.width;
		int height = d.height;
		double affineScale = 1.0;
		AffineTransform scaleMatrix = null;
		do {
			try {
				i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			} catch (OutOfMemoryError e) {
				System.err.println("Image too large; compensating...");
				realZoom *= 2;
				affineScale /= 2;
				width /= 2;
				height /= 2;
				scaleMatrix = AffineTransform.getScaleInstance(affineScale, affineScale);
			}
		} while (i == null);
		Graphics2D g = i.createGraphics();
		if (scaleMatrix != null) {
			g.setTransform(scaleMatrix);
		}
		g.setClip(0, 0, d.width, d.height);
		for (final MapLayer layer : map) {
			if ((layer instanceof TileLayer) && layer.isVisible()) {
				myView.paintTileLayer(g, (TileLayer) layer);
			}
		}
		g.dispose();

		i = scaleImage(i, realZoom);

		try {
			ImageIO.write(i, "png", new File(filename));
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scale an image. Downscaling is done using a multi-stage method.
	 *
	 * @param orig
	 * @param scale
	 * @return scaled image
	 */
	BufferedImage scaleImage(BufferedImage orig, double scale) {
		if (scale > 0.5) {
			int width = (int) (scale * orig.getWidth());
			int height = (int) (scale * orig.getHeight());
			BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = copy.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(orig, 0, 0, width, height, null);
			g.dispose();
			return copy;
		} else if (scale <= 0) {
			throw new IllegalArgumentException("Scale must be > 0, was " + scale);
		} else {
			int width = orig.getWidth() / 2;
			int height = orig.getHeight() / 2;
			BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = copy.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(orig, 0, 0, width, height, null);
			g.dispose();
			return scaleImage(copy, 2 * scale);
		}
	}

	/**
	 * Adds a set of files to copy.
	 *
	 * @param set
	 *            a set of files to copy
	 */
	public void addFileset(final FileSet set) {
		filesets.add(set);
	}

	/**
	 * The setter for the "stendPath" attribute.
	 * @param imagePath
	 */
	public void setImagePath(final String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * ants execute method.
	 */
	@Override
	public void execute() {
		try {
			for (final FileSet fileset : filesets) {
				final DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
				final String[] includedFiles = ds.getIncludedFiles();
				for (final String filename : includedFiles) {
					System.out.println(ds.getBasedir().getAbsolutePath() + File.separator + filename);
					convert(ds.getBasedir().getAbsolutePath() + File.separator + filename);
				}
			}
		} catch (final Exception e) {
			throw new BuildException(e);
		}
	}

	/**
	 * sets a zoom level
	 *
	 * @param zoom zoom
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public static void main(final String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage: java games.stendhal.tools.MapRenderer <tmx file>");
			return;
		}

		// do the job
		final MapRenderer renderet = new MapRenderer();
		renderet.imagePath = args[1];
		renderet.convert(args[0]);
	}

}
