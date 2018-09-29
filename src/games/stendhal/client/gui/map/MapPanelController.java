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
package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import games.stendhal.client.GameObjects;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalClient.ZoneChangeListener;
import games.stendhal.client.Zone;
import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.FlyOverArea;
import games.stendhal.client.entity.HousePortal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.WalkBlocker;
import games.stendhal.client.entity.Wall;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.common.CollisionDetection;

/**
 * Controller object for the map panel.
 */
public class MapPanelController implements GameObjects.GameObjectListener, PositionChangeListener, ZoneChangeListener {
	private static final boolean supermanMode = (System.getProperty("stendhal.superman") != null);
	private final JComponent container;
	private final MapPanel panel;
	private final InformationPanel infoPanel;
	final Map<IEntity, MapObject> mapObjects = new ConcurrentHashMap<IEntity, MapObject>();
	private double x, y;

	/**
	 * <code>true</code> if the map should be repainted, <code>false</code>
	 * otherwise.
	 */
	private volatile boolean needsRefresh;

	/**
	 * Create a MapPanelController.
	 *
	 * @param client client object
	 */
	public MapPanelController(final StendhalClient client) {
		container = new MapContainer();
		container.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));

		panel = new MapPanel(this, client);
		container.add(panel);

		infoPanel = new InformationPanel();
		infoPanel.setBackground(Color.BLACK);
		container.add(infoPanel, SLayout.EXPAND_X);

		client.getGameObjects().addGameObjectListener(this);
	}

	/**
	 * Mark the map contents changed, so that the component should be redrawn.
	 *
	 * @param needed
	 */
	void setNeedsRefresh(boolean needed) {
		needsRefresh = needed;
	}

	/**
	 * Get the map panel component.
	 *
	 * @return component
	 */
	public JComponent getComponent() {
		return container;
	}

	/**
	 * Add an entity to the map, if it should be displayed to the user. This
	 * method is thread safe.
	 *
	 * @param entity the added entity
	 */
	@Override
	public void addEntity(final IEntity entity) {
		MapObject object = null;

		if (entity instanceof Player) {
			object = new PlayerMapObject(entity);
		} else if (entity instanceof Portal) {
			final Portal portal = (Portal) entity;

			if (!portal.isHidden()) {
				mapObjects.put(entity, new PortalMapObject(entity));
			}
		} else if (entity instanceof HousePortal) {
			object = new PortalMapObject(entity);
		} else if (entity instanceof WalkBlocker) {
			object = new WalkBlockerMapObject(entity);
		} else if (entity instanceof FlyOverArea) {
			object = new FlyOverAreaMapObject(entity);
		} else if (entity instanceof Wall) {
			object = new WallMapObject(entity);
		} else if (entity instanceof DomesticAnimal) {
			// Only own pets and sheep are drawn but this is checked in the map object so the user status is always up to date
			object = new DomesticAnimalMapObject((DomesticAnimal) entity);
		} else if (supermanMode && User.isAdmin()) {
			if (entity instanceof RPEntity) {
				object = new RPEntityMapObject(entity);
			} else {
				object = new MovingMapObject(entity);
			}
		}

		if (object != null) {
			mapObjects.put(entity, object);

			// changes to objects that should trigger a refresh
			if (object instanceof MovingMapObject) {
				entity.addChangeListener(new EntityChangeListener<IEntity>() {
					@Override
					public void entityChanged(final IEntity entity, final Object property) {
						if ((property == IEntity.PROP_POSITION)
								|| (property == RPEntity.PROP_GHOSTMODE)
								|| (property == RPEntity.PROP_GROUP_MEMBERSHIP)) {
							needsRefresh = true;
						}
					}
				});
			}
			needsRefresh = true;
		}
	}


	/**
	 * Remove an entity from the map entity list.
	 *
	 * @param entity the entity to be removed
	 */
	@Override
	public void removeEntity(final IEntity entity) {
		if (mapObjects.remove(entity) != null) {
			needsRefresh = true;
		}
	}

	/**
	 * Request redrawing the map screen if the needed.
	 */
	public void refresh() {
		if (needsRefresh) {
			panel.repaint();
		}
	}

	/**
	 * Update the map with new data.
	 *
	 * @param cd
	 *            The collision map.
	 * @param pd
	 *      	  The protection map.
	 * @param zone
	 *            The zone name.
	 * @param dangerLevel zone danger level
	 */
	private void update(final CollisionDetection cd, final CollisionDetection pd,
			final String zone, final double dangerLevel) {
		// Panel will do the relevant part in EDT.
		panel.update(cd, pd);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				infoPanel.setZoneName(zone);
				infoPanel.setDangerLevel(dangerLevel);
			}
		});
	}

	/**
	 * The player's position changed.
	 *
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	@Override
	public void positionChanged(final double x, final double y) {
		/*
		 * The client gets occasionally spurious events.
		 * Suppress repainting unless the position actually changed
		 */
		if ((this.x != x) || (this.y != y)) {
			this.x = x;
			this.y = y;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					panel.positionChanged(x, y);
					/*
					 * Set the refresh flag after the map offset has been
					 * actually updated. The position listener for moving map
					 * objects sets it, but it happens in the game loop thread
					 * and may be unset before the map panel has actually got
					 * the correct map offset.
					 */
					setNeedsRefresh(true);
				}
			});
		}
	}

	@Override
	public void onZoneChange(Zone zone) {
	}

	@Override
	public void onZoneChangeCompleted(Zone zone) {
		update(zone.getCollision(), zone.getProtection(), zone.getReadableName(), zone.getDangerLevel());
	}

	@Override
	public void onZoneUpdate(Zone zone) {
		update(zone.getCollision(), zone.getProtection(), zone.getReadableName(), zone.getDangerLevel());
	}

	/**
	 * A container with black background to hold the map and related widgets.
	 */
	private static class MapContainer extends JComponent {
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}
