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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/**
 * This is a helper class to render coherent tiles based on the tileset. This
 * should be replaced by independent tiles as soon as possible .
 */
public class TileRenderer extends LayerRenderer {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(TileRenderer.class);

	private TileStore tiles;

	private int[] map;

//	private int width;

//	private int height;

	private int frame;

	private long delta;

	public TileRenderer(TileStore tiles) {
    super();
		this.tiles = tiles;
		map = null;
		frame = 0;
		animatedTiles = new HashMap<Integer, List<Integer>>();
		createAnimateTiles();
		delta = System.currentTimeMillis();
	}

	/** Sets the data that will be rendered */
	public void setMapData(Reader reader) throws IOException {
		Log4J.startMethod(logger, "setMapData");

		BufferedReader file = new BufferedReader(reader);
		String text;

		text = file.readLine();
		String[] size = text.split(" ");
		width = Integer.parseInt(size[0]);
		height = Integer.parseInt(size[1]);

		map = new int[width * height];

		int j = 0;

		while ((text = file.readLine()) != null) {
			if (text.trim().equals("")) {
				break;
			}

			String[] items = text.split(",");
			for (String item : items) {
				map[j] = Integer.parseInt(item);
				j++;
			}
		}

		Log4J.finishMethod(logger, "setMapData");
	}

  
	/** Returns the widht in world units */
//	public int getWidth() {
//		return width;
//	}

	/** Returns the height in world units */
//	public int getHeight() {
//		return height;
//	}

	private int get(int x, int y) {
		return map[y * width + x];
	}

	private Map<Integer, List<Integer>> animatedTiles;

	private void addAnimatedTile(int tile, int[] tiles) {
		List<Integer> list = new LinkedList<Integer>();
		for (int num : tiles) {
			list.add(num);
		}

		animatedTiles.put(tile, list);
	}

	private void createAnimateTiles() {
		// Outside_0 = 0 - 478
		// Outside_1 = 480 - 858
		// Dungeon_0 = 860 - 1438
		// Dungeon_1 = 1440 - 1818
		// Interior_0 = 1820 - 2388
		// Navigation = 2400 - 2400
		// Objects = 2400 - 2600
		// Collision = 2600 - 2602
		// Buildings_0 = 2602 - 2982
		// Outside_2 = 2982 - 3562
		// Interior_1 = 3562 - 3942

		// Double white daisy
		addAnimatedTile(22, new int[] { 22, 52, 82, 112, 112, 112, 112, 112,
				112, 112 });
		addAnimatedTile(52,
				new int[] { 52, 82, 112, 22, 22, 22, 22, 22, 22, 22 });
		addAnimatedTile(82,
				new int[] { 82, 112, 22, 52, 52, 52, 52, 52, 52, 52 });
		addAnimatedTile(112, new int[] { 112, 22, 52, 82, 82, 82, 82, 82, 82,
				82 });

		// Single white daisy
		addAnimatedTile(23, new int[] { 23, 53, 83, 113, 113, 113, 113, 113,
				113, 113 });
		addAnimatedTile(53,
				new int[] { 53, 83, 113, 23, 23, 23, 23, 23, 23, 23 });
		addAnimatedTile(83,
				new int[] { 83, 113, 23, 53, 53, 53, 53, 53, 53, 53 });
		addAnimatedTile(113, new int[] { 113, 23, 53, 83, 83, 83, 83, 83, 83,
				83 });

		// Double yellow daisy
		addAnimatedTile(24, new int[] { 24, 54, 84, 114, 114, 114, 114, 114,
				114, 114 });
		addAnimatedTile(54,
				new int[] { 54, 84, 114, 24, 24, 24, 24, 24, 24, 24 });
		addAnimatedTile(84,
				new int[] { 84, 114, 24, 54, 54, 54, 54, 54, 54, 54 });
		addAnimatedTile(114, new int[] { 114, 24, 54, 84, 84, 84, 84, 84, 84,
				84 });

		// Single yellow daisy
		addAnimatedTile(25, new int[] { 25, 55, 85, 115, 115, 115, 115, 115,
				115, 115 });
		addAnimatedTile(55,
				new int[] { 55, 85, 115, 25, 25, 25, 25, 25, 25, 25 });
		addAnimatedTile(85,
				new int[] { 85, 115, 25, 55, 55, 55, 55, 55, 55, 55 });
		addAnimatedTile(115, new int[] { 115, 25, 55, 85, 85, 85, 85, 85, 85,
				85 });

		// Double red daisy
		addAnimatedTile(26, new int[] { 26, 56, 86, 116, 116, 116, 116, 116,
				116, 116 });
		addAnimatedTile(56, new int[] { 56, 86, 116, 26, 26, 26, 26, 26, 26,
				26, 26, 26 });
		addAnimatedTile(86,
				new int[] { 86, 116, 26, 56, 56, 56, 56, 56, 56, 56 });
		addAnimatedTile(116, new int[] { 116, 26, 56, 86, 86, 86, 86, 86, 86,
				86 });

		// Single red daisy
		addAnimatedTile(27, new int[] { 27, 57, 87, 117, 117, 117, 117, 117,
				117, 117 });
		addAnimatedTile(57,
				new int[] { 57, 87, 117, 27, 27, 27, 27, 27, 27, 27 });
		addAnimatedTile(87,
				new int[] { 87, 117, 27, 57, 57, 57, 57, 57, 57, 57 });
		addAnimatedTile(117, new int[] { 117, 27, 57, 87, 87, 87, 87, 87, 87,
				87 });

		// Double blue daisy
		addAnimatedTile(28, new int[] { 28, 58, 88, 118, 118, 118, 118, 118,
				118, 118 });
		addAnimatedTile(58,
				new int[] { 58, 88, 118, 28, 28, 28, 28, 28, 28, 28 });
		addAnimatedTile(88,
				new int[] { 88, 118, 28, 58, 58, 58, 58, 58, 58, 58 });
		addAnimatedTile(118, new int[] { 118, 28, 58, 88, 88, 88, 88, 88, 88,
				88 });

		// Single blue daisy
		addAnimatedTile(29, new int[] { 29, 59, 89, 119, 119, 119, 119, 119,
				119, 119 });
		addAnimatedTile(59,
				new int[] { 59, 89, 119, 29, 29, 29, 29, 29, 29, 29 });
		addAnimatedTile(89,
				new int[] { 89, 119, 29, 59, 59, 59, 59, 59, 59, 59 });
		addAnimatedTile(119, new int[] { 119, 29, 59, 89, 89, 89, 89, 89, 89,
				89 });

		// Fire
		addAnimatedTile(1167, new int[] { 1167, 1258 });
		addAnimatedTile(1258, new int[] { 1167, 1258 });
		
		// Green Water, Top Left
		addAnimatedTile(3083, new int[] { 3083, 3086, 3089, 3086 });
		addAnimatedTile(3086, new int[] { 3083, 3086, 3089, 3086 });
		addAnimatedTile(3089, new int[] { 3083, 3086, 3089, 3086 });

		// Green Water, Top
		addAnimatedTile(3084, new int[] { 3084, 3087, 3090, 3087 });
		addAnimatedTile(3087, new int[] { 3084, 3087, 3090, 3087 });
		addAnimatedTile(3090, new int[] { 3084, 3087, 3090, 3087 });

		// Green Water, Top Right
		addAnimatedTile(3085, new int[] { 3085, 3088, 3091, 3088 });
		addAnimatedTile(3088, new int[] { 3085, 3088, 3091, 3088 });
		addAnimatedTile(3091, new int[] { 3085, 3088, 3091, 3088 });

		// Green Water, Left
		addAnimatedTile(3113, new int[] { 3113, 3116, 3119, 3116 });
		addAnimatedTile(3116, new int[] { 3113, 3116, 3119, 3116 });
		addAnimatedTile(3119, new int[] { 3113, 3116, 3119, 3116 });

		// Green Water, pond
		addAnimatedTile(3114, new int[] { 3114, 3117, 3120, 3117 });
		addAnimatedTile(3117, new int[] { 3114, 3117, 3120, 3117 });
		addAnimatedTile(3120, new int[] { 3114, 3117, 3120, 3117 });

		// Green Water, Right
		addAnimatedTile(3115, new int[] { 3115, 3118, 3121, 3118 });
		addAnimatedTile(3118, new int[] { 3115, 3118, 3121, 3118 });
		addAnimatedTile(3121, new int[] { 3115, 3118, 3121, 3118 });

		// Green Water, Bottom Left
		addAnimatedTile(3143, new int[] { 3143, 3146, 3149, 3146 });
		addAnimatedTile(3146, new int[] { 3143, 3146, 3149, 3146 });
		addAnimatedTile(3149, new int[] { 3143, 3146, 3149, 3146 });

		// Green Water, Bottom
		addAnimatedTile(3144, new int[] { 3144, 3147, 3150, 3147 });
		addAnimatedTile(3147, new int[] { 3144, 3147, 3150, 3147 });
		addAnimatedTile(3150, new int[] { 3144, 3147, 3150, 3147 });

		// Green Water, Bottom Right
		addAnimatedTile(3145, new int[] { 3145, 3148, 3151, 3148 });
		addAnimatedTile(3148, new int[] { 3145, 3148, 3151, 3148 });
		addAnimatedTile(3151, new int[] { 3145, 3148, 3151, 3148 });

		// Green Water, Top Left Corner
		addAnimatedTile(3263, new int[] { 3263, 3265, 3267, 3265 });
		addAnimatedTile(3265, new int[] { 3263, 3265, 3267, 3265 });
		addAnimatedTile(3267, new int[] { 3263, 3265, 3267, 3265 });

		// Green Water, Top Right Corner
		addAnimatedTile(3264, new int[] { 3264, 3266, 3268, 3266 });
		addAnimatedTile(3266, new int[] { 3264, 3266, 3268, 3266 });
		addAnimatedTile(3268, new int[] { 3264, 3266, 3268, 3266 });

		// Green Water, Bottom Left Corner
		addAnimatedTile(3293, new int[] { 3293, 3295, 3297, 3295 });
		addAnimatedTile(3295, new int[] { 3293, 3295, 3297, 3295 });
		addAnimatedTile(3297, new int[] { 3293, 3295, 3297, 3295 });

		// Green Water, Bottom Right Corner
		addAnimatedTile(3294, new int[] { 3294, 3296, 3298, 3296 });
		addAnimatedTile(3296, new int[] { 3294, 3296, 3298, 3296 });
		addAnimatedTile(3298, new int[] { 3294, 3296, 3298, 3296 });

		// Green Water, Vertical Canal
		addAnimatedTile(3323, new int[] { 3323, 3324, 3325, 3324 });
		addAnimatedTile(3324, new int[] { 3323, 3324, 3325, 3324 });
		addAnimatedTile(3325, new int[] { 3323, 3324, 3325, 3324 });

		// Green Water, Horizontal Canal
		addAnimatedTile(3353, new int[] { 3353, 3354, 3355, 3354 });
		addAnimatedTile(3354, new int[] { 3353, 3354, 3355, 3354 });
		addAnimatedTile(3355, new int[] { 3353, 3354, 3355, 3354 });

		// Light Water, Center
		addAnimatedTile(3269, new int[] { 3269, 3270, 3271, 3270 });
		addAnimatedTile(3270, new int[] { 3269, 3270, 3271, 3270 });
		addAnimatedTile(3271, new int[] { 3269, 3270, 3271, 3270 });

		// Waterfall start
		addAnimatedTile(145, new int[] { 145, 175, 205, 235 });
		addAnimatedTile(175, new int[] { 175, 205, 235, 145 });
		addAnimatedTile(205, new int[] { 205, 235, 145, 175 });
		addAnimatedTile(235, new int[] { 235, 145, 175, 205 });

		// Waterfall middle
		addAnimatedTile(146, new int[] { 146, 176, 206, 236 });
		addAnimatedTile(176, new int[] { 176, 206, 236, 146 });
		addAnimatedTile(206, new int[] { 206, 236, 146, 176 });
		addAnimatedTile(236, new int[] { 236, 146, 176, 206 });

		// Waterfall end
		addAnimatedTile(147, new int[] { 147, 177, 207, 237 });
		addAnimatedTile(177, new int[] { 177, 207, 237, 147 });
		addAnimatedTile(207, new int[] { 207, 237, 147, 177 });
		addAnimatedTile(237, new int[] { 237, 147, 177, 207 });

		// Waterfall end left
		addAnimatedTile(148, new int[] { 148, 178, 208, 238 });
		addAnimatedTile(178, new int[] { 178, 208, 238, 148 });
		addAnimatedTile(208, new int[] { 208, 238, 148, 178 });
		addAnimatedTile(238, new int[] { 238, 148, 178, 208 });

		// Waterfall end right
		addAnimatedTile(149, new int[] { 149, 179, 209, 239 });
		addAnimatedTile(179, new int[] { 179, 209, 239, 149 });
		addAnimatedTile(209, new int[] { 209, 239, 149, 179 });
		addAnimatedTile(239, new int[] { 239, 149, 179, 209 });

		// Waterfall golden start
		addAnimatedTile(265, new int[] { 265, 295, 325, 355 });
		addAnimatedTile(295, new int[] { 295, 325, 355, 265 });
		addAnimatedTile(325, new int[] { 325, 355, 265, 295 });
		addAnimatedTile(355, new int[] { 355, 265, 295, 325 });

		// Waterfall golden middle
		addAnimatedTile(266, new int[] { 266, 296, 326, 356 });
		addAnimatedTile(296, new int[] { 296, 326, 356, 266 });
		addAnimatedTile(326, new int[] { 326, 356, 266, 296 });
		addAnimatedTile(356, new int[] { 296, 326, 356, 266 });

		// Waterfall golden end
		addAnimatedTile(267, new int[] { 267, 297, 327, 357 });
		addAnimatedTile(297, new int[] { 297, 327, 357, 267 });
		addAnimatedTile(327, new int[] { 327, 357, 267, 297 });
		addAnimatedTile(357, new int[] { 357, 267, 297, 327 });

		// Waterfall golden end left
		addAnimatedTile(268, new int[] { 268, 298, 328, 358 });
		addAnimatedTile(298, new int[] { 298, 328, 358, 268 });
		addAnimatedTile(328, new int[] { 328, 358, 268, 298 });
		addAnimatedTile(358, new int[] { 358, 268, 298, 328 });

		// Waterfall golden end right
		addAnimatedTile(269, new int[] { 269, 299, 329, 359 });
		addAnimatedTile(299, new int[] { 299, 329, 359, 269 });
		addAnimatedTile(329, new int[] { 329, 359, 269, 299 });
		addAnimatedTile(359, new int[] { 359, 269, 299, 329 });

		// Golden Teleport
		addAnimatedTile(1443, new int[] { 1443, 1444, 1445 });
		addAnimatedTile(1444, new int[] { 1444, 1445, 1443 });
		addAnimatedTile(1445, new int[] { 1445, 1443, 1444 });

		// White Teleport
		addAnimatedTile(1473, new int[] { 1473, 1474, 1475 });
		addAnimatedTile(1474, new int[] { 1474, 1475, 1473 });
		addAnimatedTile(1475, new int[] { 1475, 1473, 1474 });

		// Gray Teleport
		addAnimatedTile(1503, new int[] { 1503, 1504, 1505 });
		addAnimatedTile(1504, new int[] { 1504, 1505, 1503 });
		addAnimatedTile(1505, new int[] { 1505, 1503, 1504 });

		// Red Teleport
		addAnimatedTile(1533, new int[] { 1533, 1534, 1535 });
		addAnimatedTile(1534, new int[] { 1534, 1535, 1533 });
		addAnimatedTile(1535, new int[] { 1535, 1533, 1534 });

		// Green Teleport
		addAnimatedTile(1563, new int[] { 1563, 1564, 1565 });
		addAnimatedTile(1564, new int[] { 1564, 1565, 1563 });
		addAnimatedTile(1565, new int[] { 1565, 1563, 1564 });

		// Blue Teleport
		addAnimatedTile(1593, new int[] { 1593, 1594, 1595 });
		addAnimatedTile(1594, new int[] { 1594, 1595, 1593 });
		addAnimatedTile(1595, new int[] { 1595, 1593, 1594 });

		// Interior_1 = 3562 - 394

		// Blacksmith fire (small), Top
		addAnimatedTile(3660, new int[] { 3660, 3660, 3661, 3661, 3662, 3662,
				3661, 3661 });
		addAnimatedTile(3661, new int[] { 3660, 3660, 3661, 3661, 3662, 3662,
				3661, 3661 });
		addAnimatedTile(3662, new int[] { 3660, 3660, 3661, 3661, 3662, 3662,
				3661, 3661 });

		// Blacksmith fire (small), Bottom
		addAnimatedTile(3690, new int[] { 3690, 3690, 3691, 3691, 3692, 3692,
				3691, 3691 });
		addAnimatedTile(3691, new int[] { 3690, 3690, 3691, 3691, 3692, 3692,
				3691, 3691 });
		addAnimatedTile(3692, new int[] { 3690, 3690, 3691, 3691, 3692, 3692,
				3691, 3691 });

		// Blacksmith fire (large), Top Left
		addAnimatedTile(3657, new int[] { 3657, 3657, 3747, 3747, 3837, 3837,
				3747, 3747 });
		addAnimatedTile(3747, new int[] { 3657, 3657, 3747, 3747, 3837, 3837,
				3747, 3747 });
		addAnimatedTile(3837, new int[] { 3657, 3657, 3747, 3747, 3837, 3837,
				3747, 3747 });

		// Blacksmith fire (large), Top
		addAnimatedTile(3658, new int[] { 3658, 3658, 3748, 3748, 3838, 3838,
				3748, 3748 });
		addAnimatedTile(3748, new int[] { 3658, 3658, 3748, 3748, 3838, 3838,
				3748, 3748 });
		addAnimatedTile(3838, new int[] { 3658, 3658, 3748, 3748, 3838, 3838,
				3748, 3748 });

		// Blacksmith fire (large), Top Right
		addAnimatedTile(3659, new int[] { 3659, 3659, 3749, 3749, 3839, 3839,
				3749, 3749 });
		addAnimatedTile(3749, new int[] { 3659, 3659, 3749, 3749, 3839, 3839,
				3749, 3749 });
		addAnimatedTile(3839, new int[] { 3659, 3659, 3749, 3749, 3839, 3839,
				3749, 3749 });

		// Blacksmith fire (large), Left
		addAnimatedTile(3687, new int[] { 3687, 3687, 3777, 3777, 3867, 3867,
				3777, 3777 });
		addAnimatedTile(3777, new int[] { 3687, 3687, 3777, 3777, 3867, 3867,
				3777, 3777 });
		addAnimatedTile(3867, new int[] { 3687, 3687, 3777, 3777, 3867, 3867,
				3777, 3777 });

		// Blacksmith fire (large), Center
		addAnimatedTile(3688, new int[] { 3688, 3688, 3778, 3778, 3868, 3868,
				3778, 3778 });
		addAnimatedTile(3778, new int[] { 3688, 3688, 3778, 3778, 3868, 3868,
				3778, 3778 });
		addAnimatedTile(3868, new int[] { 3688, 3688, 3778, 3778, 3868, 3868,
				3778, 3778 });

		// Blacksmith fire (large), Right
		addAnimatedTile(3689, new int[] { 3689, 3689, 3779, 3779, 3869, 3869,
				3779, 3779 });
		addAnimatedTile(3779, new int[] { 3689, 3689, 3779, 3779, 3869, 3869,
				3779, 3779 });
		addAnimatedTile(3869, new int[] { 3689, 3689, 3779, 3779, 3869, 3869,
				3779, 3779 });

		// Blacksmith fire (large), Bottom Left
		addAnimatedTile(3717, new int[] { 3717, 3717, 3807, 3807, 3897, 3897,
				3807, 3807 });
		addAnimatedTile(3807, new int[] { 3717, 3717, 3807, 3807, 3897, 3897,
				3807, 3807 });
		addAnimatedTile(3897, new int[] { 3717, 3717, 3807, 3807, 3897, 3897,
				3807, 3807 });

		// Blacksmith fire (large), Bottom
		addAnimatedTile(3718, new int[] { 3718, 3718, 3808, 3808, 3898, 3898,
				3808, 3808 });
		addAnimatedTile(3808, new int[] { 3718, 3718, 3808, 3808, 3898, 3898,
				3808, 3808 });
		addAnimatedTile(3898, new int[] { 3718, 3718, 3808, 3808, 3898, 3898,
				3808, 3808 });

		// Blacksmith fire (large), Bottom Right
		addAnimatedTile(3719, new int[] { 3719, 3719, 3809, 3809, 3899, 3899,
				3809, 3809 });
		addAnimatedTile(3809, new int[] { 3719, 3719, 3809, 3809, 3899, 3899,
				3809, 3809 });
		addAnimatedTile(3899, new int[] { 3719, 3719, 3809, 3809, 3899, 3899,
				3809, 3809 });

		// Flame Brazier
		addAnimatedTile(1567, new int[] { 1567, 1597, 1627, 1597 });
		addAnimatedTile(1597, new int[] { 1567, 1597, 1627, 1597 });
		addAnimatedTile(1527, new int[] { 1567, 1597, 1627, 1597 });

	}

	/**
	 * Render the data to screen. We assume that Gamescreen will clip. The data
	 * doesnt change, so we could cache it and get a boost in performance
	 */
	public void draw(GameScreen screen) {
		if (System.currentTimeMillis() - delta > 200) {
			delta = System.currentTimeMillis();
			frame++;
		}

		int x = (int) screen.getX();
		int y = (int) screen.getY();
		int w = (int) screen.getWidth();
		int h = (int) screen.getHeight();

		for (int j = y - 1; j < y + h + 1; j++) {
			for (int i = x - 1; i < x + w + 1; i++) {
				if (j >= 0 && j < getHeight() && i >= 0 && i < getWidth()) {
					int value = get(i, j) - 1;

					if (animatedTiles.containsKey(value)) {
						List<Integer> list = (animatedTiles.get(value));
						value = list.get(frame % list.size());
					}

					if (value >= 0) {
						screen.draw(tiles.getTile(value), i, j);
					}
				}
			}
		}
	}
}