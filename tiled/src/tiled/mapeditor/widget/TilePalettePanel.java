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
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.mapeditor.widget;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;

import tiled.core.StatefulTile;
import tiled.core.Tile;
import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.PropertiesDialog;
import tiled.mapeditor.util.TileSelectionEvent;
import tiled.mapeditor.util.TileSelectionListener;
import tiled.util.Util;

public class TilePalettePanel extends JPanel implements MouseInputListener {
	private static final long serialVersionUID = 364058467891505985L;

	private static final int MIN_TILES_PER_ROW = 10;

	/** the tileset for this panel. */
	private TileSet tileset;
	/** a list of listeners to be notified when the selected tile changes. */
	private EventListenerList tileSelectionListeners;
	/** the currently selected tiles. */
	private List<StatefulTile> selectedTiles;
	/** indicator that the user is drawing a frame to select multiple tiles. */
	private boolean dragInProgress;
	/** the point where the drag started. */
	private Point dragStartPoint;
	/** the current endpoint of the drag operation. */
	private Point currentDragPoint;

	/** */
	private MapEditor mapEditor;

	/** some cached tile properties. */
	private int twidth;
	private int theight;
	private int tilesPerRow;

	public TilePalettePanel(MapEditor mapEditor, TileSet set) {
		this.tileset = set;
		this.mapEditor = mapEditor;
		tileSelectionListeners = new EventListenerList();
		selectedTiles = new ArrayList<StatefulTile>();
		refreshTileProperties();

		addMouseListener(this);
		addMouseMotionListener(this);

		setToolTipText("text");
	}

	/**
	 * Adds tile selection listener. The listener will be notified when the user
	 * selects a tile.
	 */
	public void addTileSelectionListener(TileSelectionListener l) {
		tileSelectionListeners.add(TileSelectionListener.class, l);
	}

	/**
	 * Removes tile selection listener.
	 */
	public void removeTileSelectionListener(TileSelectionListener l) {
		tileSelectionListeners.remove(TileSelectionListener.class, l);
	}

	protected void fireTileSelectionEvent(List<StatefulTile> selectedTiles) {
		TileSelectionListener[] listeners = tileSelectionListeners.getListeners(TileSelectionListener.class);
		if (listeners.length == 0) {
			return;
		}

		TileSelectionEvent event = new TileSelectionEvent(this, new ArrayList<StatefulTile>(selectedTiles));

		for (TileSelectionListener listener : listeners) {
			listener.tileSelected(event);
		}
	}

	/** calculates some tile properties. */
	private void refreshTileProperties() {
		twidth = tileset.getStandardWidth() + 1;
		theight = tileset.getTileHeightMax() + 1;

		tilesPerRow = tileset.getPreferredTilesPerRow();
		if (tilesPerRow == 0) {
			tilesPerRow = (getWidth() / twidth) - 1;
			if (tilesPerRow <= MIN_TILES_PER_ROW) {
				tilesPerRow = MIN_TILES_PER_ROW;
			}
		}
	}

	/** returns the tile at the given position (or null if there is no tile). */
	public Tile getTileAtPoint(int x, int y) {
		Tile ret = null;

		int size = tileset.size();

		int tilex = x / twidth;
		int tiley = y / theight;

		if (tilex < tilesPerRow) {
			int tileNum = tiley * tilesPerRow + tilex;
			if (tileNum < size) {
				return tileset.getTile(tileNum);
			}
		}

		return ret;
	}

	/** paints the component. */
	@Override
	public void paint(Graphics g) {
		paintBackground(g);

		if (tileset == null) {
			return;
		}

		int gx = 0;
		int gy = 0;

		if (tileset != null) {
			// Draw the tiles
			int width = getWidth() - twidth;

			if (tilesPerRow > 0) {
				width = tilesPerRow * twidth - 1;
			}

			int tileAt = 0;

			for (Tile tile : tileset) {
				if (tile != null) {
					int x = gx;
					int y = gy + (theight - tile.getHeight());
					tile.drawRaw(g, x, y, 1.0);
					Properties props = tile.getProperties();
					if (props != null && props.size() > 0) {
						int w = tile.getWidth();
						int size = w / 4;
						Polygon p = new Polygon();
						p.addPoint(x + w - 1, y);
						p.addPoint(x + w - 1 - size, y);
						p.addPoint(x + w - 1, y + size);

						g.setColor(Color.YELLOW);
						g.fillPolygon(p);
						g.setColor(Color.BLACK);
						g.drawPolygon(p);

					}

				}
				gx += twidth;
				if (gx > width) {
					gy += theight;
					gx = 0;
				}
				tileAt++;
			}

			// draw selected tiles
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
			g2d.setColor(Color.YELLOW);

			for (StatefulTile tile : selectedTiles) {
				int id = tile.tile.getId();
				int x = id % tilesPerRow;
				int y = id / tilesPerRow;
				g2d.fillRect(x * twidth - 1, y * theight, tile.tile.getWidth() + 1, tile.tile.getHeight() + 1);
			}

			// drag selection rectangle
			if (dragInProgress) {
				Rectangle rect = Util.getRectangle(dragStartPoint, currentDragPoint);

				g.setColor(Color.WHITE);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
				g.drawRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
			}
		}
	}

	/**
	 * Draws checkerboard background.
	 */
	private void paintBackground(Graphics g) {
		Rectangle clip = g.getClipBounds();
		int side = 10;

		int startX = clip.x / side;
		int startY = clip.y / side;
		int endX = (clip.x + clip.width) / side + 1;
		int endY = (clip.y + clip.height) / side + 1;

		// Fill with white background
		g.setColor(Color.WHITE);
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		// Draw darker squares
		g.setColor(Color.LIGHT_GRAY);
		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				if ((y + x) % 2 == 1) {
					g.fillRect(x * side, y * side, side, side);
				}
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (tileset == null) {
			return new Dimension(10, 100);
		}

		int size = tileset.size();
		int height = (size / tilesPerRow) + 1;

		return new Dimension(tilesPerRow * twidth, height * theight);
	}

	// MouseInputListener interface
	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		Point point = getMousePosition();
		refreshSelectedTiles(new Rectangle(point.x, point.y, 1, 1));
		fireTileSelectionEvent(selectedTiles);
		repaint();
		Point p = e.getPoint();
		if (e.getButton() == MouseEvent.BUTTON3 && p != null) {
			Tile tile = getTileAtPoint(p.x, p.y);
			Properties props = tile.getProperties();
			PropertiesDialog lpd = new PropertiesDialog(mapEditor.appFrame, props);
			lpd.setTitle("Tile " + p.x + "x" + p.y + " Properties");
			lpd.getProps();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			doDrag(e);
		}

		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			finishDrag(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		doDrag(e);
	}

	public void mouseMoved(MouseEvent e) {
		finishDrag(e);
	}

	/** starts the drag operation. */
	private void doDrag(MouseEvent e) {
		Point p = getMousePosition();
		if (p == null) {
			return;
		}

		if (dragInProgress) {
			currentDragPoint = p;
		} else {
			dragInProgress = true;
			dragStartPoint = getMousePosition();
			currentDragPoint = dragStartPoint;
		}
		refreshSelectedTiles(Util.getRectangle(dragStartPoint, currentDragPoint));
		fireTileSelectionEvent(selectedTiles);
		repaint();
	}

	/** finishes the drag operation. */
	private void finishDrag(MouseEvent e) {
		if (dragInProgress) {
			refreshSelectedTiles(Util.getRectangle(dragStartPoint, currentDragPoint));
		}
		dragInProgress = false;
		repaint();
	}

	/** selects all tiles within the selection rectangle. */
	private void refreshSelectedTiles(Rectangle rect) {
		int tempTwidth = tileset.getStandardWidth() + 1;
		int tempTheight = tileset.getTileHeightMax() + 1;

		int maxx = rect.x + rect.width;
		maxx += (maxx % tempTwidth > 0) ? tempTwidth - (maxx % tempTwidth) : 0;
		int maxy = rect.y + rect.height;
		maxy += (maxy % tempTheight > 0) ? tempTheight - (maxy % tempTheight) : 0;

		List<StatefulTile> statefulTiles = new ArrayList<StatefulTile>();

		for (int x = rect.x, brushx = 0; x < maxx; x += tempTwidth, brushx++) {
			for (int y = rect.y, brushy = 0; y < maxy; y += tempTheight, brushy++) {
				Tile tile = getTileAtPoint(x, y);
				if (tile != null) {
					statefulTiles.add(new StatefulTile(new Point(x / tempTwidth, y / tempTwidth), 0, tile));
				}
			}
		}
		selectedTiles = statefulTiles;
	}

	/** returns tooltip text. */
	@Override
	public String getToolTipText(MouseEvent e) {
		Tile tile = getTileAtPoint(e.getPoint().x, e.getPoint().y);
		if (tile == null) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		buf.append("<html>Tile: ").append(tile.getId()).append("<br>");
		Properties props = tile.getProperties();
		if (props != null) {
			for (Object key : props.keySet()) {
				buf.append(key).append(" = ").append(props.get(key)).append("<br>");
			}
		}
		return buf.toString();
	}
}
