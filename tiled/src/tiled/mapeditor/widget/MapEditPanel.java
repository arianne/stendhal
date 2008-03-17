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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import tiled.core.Map;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.builder.Builder;
import tiled.mapeditor.dialog.PropertiesDialog;
import tiled.mapeditor.util.MapModifyListener;
import tiled.util.Util;
import tiled.view.MapView;

/**
 * This Panel contains the map editor itself. The map is an abstract (drawable)
 * container. This panel just manages the communication with the window toolkit.
 * 
 * @author Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */
public class MapEditPanel extends JPanel implements MouseListener, MouseMotionListener, Scrollable {
	private static final long serialVersionUID = 1L;

	/** the map view. */
	private MapView mapView;
	/** the minimap panel. */
	private MiniMapViewer miniMapViewer;

	/** list of modify listeners. */
	private List<MapModifyListener> listeners;
	/** base map editor. */
	private MapEditor mapEditor;
	/** last drawn cursor rectangle. */
	private Rectangle lastDrawnCursor;
	/** dragging op in progress? */
	private boolean dragInProgress;
	/** point where the drag started. */
	private Point dragStartPoint;
	/** */
	private DragType dragType;

	/** constructor. */
	public MapEditPanel(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/** sets the map view. Note that this clears all selected tiles */
	public void setMapView(MapView mapView) {
		this.mapView = mapView;
		miniMapViewer.setView(mapView);
		revalidate();
	}

	/** returns the current map view (may be null). */
	public MapView getMapView() {
		return mapView;
	}

	/** sets the minimap panel. */
	public void setMinimapPanel(MiniMapViewer miniMapViewer) {
		this.miniMapViewer = miniMapViewer;
	}

	/** registers a listener to be notified when the map content changes. */
	public void registerMapModifyListener(MapModifyListener mapModifyListener) {
		if (!listeners.contains(mapModifyListener)) {
			listeners.add(mapModifyListener);
		}
	}

	/** removed a registered listener. */
	public void removeMapModifyListener(MapModifyListener mapModifyListener) {
		listeners.remove(mapModifyListener);
	}

	/** Draws the map. */
	public void paintComponent(Graphics g) {
		if (mapView != null) {
			mapView.draw(g);
			if (miniMapViewer != null) {
				miniMapViewer.repaint();
			}

			Point p = getMousePosition();
			Builder builder = mapEditor.currentBuilder;
			if (p != null && builder != null) {
				Rectangle cursor = mapView.tileToScreenRect(builder.getBounds());
				Point tilePoint = mapView.tileToScreenCoords(mapView.screenToTileCoords(p));
				cursor.translate(tilePoint.x, tilePoint.y);
				g.setColor(Color.WHITE);
				g.drawRect(cursor.x, cursor.y, cursor.width - 1, cursor.height - 1);
				lastDrawnCursor = cursor;
			}

			if (p != null && dragInProgress && dragType == DragType.SELECT && dragStartPoint != null) {
				g.setColor(Color.BLUE);
				Rectangle rect = Util.getRectangle(p, dragStartPoint);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			}

			List<Point> points = mapEditor.getSelectedTiles();

			g.setColor(Color.YELLOW);
			mapView.drawTilesHighlight(g, points);
		}
	}

	/** repaints a portion of the map. The rectangle is in tile coordinates */
	public void repaintRegion(Rectangle affectedRegion) {
		if (mapView != null) {
			repaint(mapView.tileToScreenRect(affectedRegion));
			if (miniMapViewer != null) {
				miniMapViewer.repaint();
			}
		}
	}

	/** returns the prefered size of the panel. */
	public Dimension getPreferredSize() {
		if (mapView != null) {
			return mapView.getSize();
		}
		return new Dimension(100, 100);
	}

	/** repaints the last cursor position .*/
	private void repaintLastCursorPosition() {
		if (lastDrawnCursor != null) {
			repaint(lastDrawnCursor);
		}
	}

	/** converts screen to tile coords. */
	private Point getTilePosition(Point p) {
		if (p == null || mapView == null) {
			return null;
		}

		Container parent = getParent();
		if (parent != null && parent instanceof JScrollPane) {
			JScrollPane pane = (JScrollPane) parent;
			Point other = pane.getViewport().getViewPosition();
			p.translate(other.x, other.y);
		}

		return mapView.screenToTileCoords(p);
	}

	/** updates the affected region .*/
	private void updateModifiedRegion(Rectangle affectedRegion) {
		if (affectedRegion != null) {
			mapView.updateMinimapImage(affectedRegion);
			repaintRegion(affectedRegion);
		}
	}

	/** draws at the point p (in screen coords) the current brush .*/
	private void drawTo(Point p, boolean dragged) {
		Point tile = getTilePosition(p);
		Builder builder = mapEditor.currentBuilder;

		if (tile != null && builder != null) {
			Rectangle affectedRegion = null;
			if (dragged) {
				if (builder.isStarted()) {
					affectedRegion = builder.moveBuilder(tile);
				} else {
					affectedRegion = builder.startBuilder(tile);
				}
			} else {
				affectedRegion = builder.startBuilder(tile);
				builder.finishBuilder(null);
			}

			updateModifiedRegion(affectedRegion);
		}
	}

	/** finishes the dragging operation. */
	private void finishDrag(Point point) {
		if (!dragInProgress) {
			return;
		}

		switch (dragType) {
		case DRAW:
			Point tile = getTilePosition(point);
			Builder builder = mapEditor.currentBuilder;

			if (tile != null && builder != null) {
				updateModifiedRegion(builder.finishBuilder(tile));
			}
			break;
		case SELECT:
			repaint();
			Rectangle rect = Util.getRectangle(point, dragStartPoint);
			List<Point> tiles = mapView.getSelectedTiles(rect, mapEditor.currentLayer);
			mapEditor.setSelectedTiles(tiles);
			break;
		default:
			break;

		}

		dragInProgress = false;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			drawTo(e.getPoint(), false);
			mapEditor.clearSelectedTiles();
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// show properties dialog
			Map map = mapEditor.getCurrentMap();
			Point p = getTilePosition(e.getPoint());
			Properties props = map.getPropertiesLayer().getProps(p.x, p.y);
			PropertiesDialog lpd = new PropertiesDialog(mapEditor.appFrame, props);
			lpd.setTitle("Tile " + p.x + "x" + p.y + " Properties");
			lpd.getProps();
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		finishDrag(e.getPoint());
	}

	public void mouseEntered(MouseEvent e) {
		repaintLastCursorPosition();
	}

	public void mouseExited(MouseEvent e) {
		repaintLastCursorPosition();
	}

	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
			if (dragInProgress
					&& ((!e.isShiftDown() && dragType == DragType.SELECT) || (e.isShiftDown() && dragType == DragType.DRAW))) {
				finishDrag(e.getPoint());
			}

			if (e.isShiftDown()) {
				// Select
				if (!dragInProgress) {
					dragStartPoint = e.getPoint();
				}
				dragInProgress = true;
				dragType = DragType.SELECT;
				repaint();
			} else {
				// Draw
				dragInProgress = true;
				dragType = DragType.DRAW;
				drawTo(e.getPoint(), true);
				repaintLastCursorPosition();
			}
		}

	}

	public void mouseMoved(MouseEvent e) {
		finishDrag(e.getPoint());

		if (mapView == null) {
			return;
		}

		Point p = getMousePosition();
		if (p != null) {
			Builder builder = mapEditor.currentBuilder;
			if (builder != null) {
				Rectangle cursor = mapView.tileToScreenRect(builder.getBounds());
				Point tilePoint = mapView.tileToScreenCoords(mapView.screenToTileCoords(p));
				cursor.translate(tilePoint.x, tilePoint.y);

				repaintLastCursorPosition();
				repaint(cursor);
			}
		}
	}

	/** enum indicating the current drag operation. */
	public enum DragType {
		DRAW,
		SELECT;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getMaximumSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return orientation < 0 ? visibleRect.height : visibleRect.width;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		Point scale = mapView.tileToScreenCoords(new Point(1, 1));
		return orientation < 0 ? scale.y : scale.x;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * notifies the panel that the mapview size was changed.
	 */
	public void notifyZoom() {
		setSize(getPreferredSize());
		repaint();
	}

}
