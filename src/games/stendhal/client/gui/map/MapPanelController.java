package games.stendhal.client.gui.map;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.IEntity;
import games.stendhal.common.CollisionDetection;

import java.awt.GraphicsConfiguration;

public class MapPanelController {
	private static MapPanelController instance;
	private MapPanel panel;
	
	public MapPanelController(final StendhalClient client) {
		panel = new MapPanel(client);
		instance = this;
	}
	
	/**
	 * Get the instance
	 * @return the controller instance
	 */
	public static MapPanelController get() {
		return instance;
	}
	
	/**
	 * Get the map panel component
	 * @return component
	 */
	public MapPanel getComponent() {
		return panel;
	}
	
	/**
	 * Add an entity to the map entity list.
	 * The map may ignore entities that it does 
	 * not need to draw.
	 *  
	 * @param entity the entity to be added
	 */
	public void addEntity(final IEntity entity) {
		panel.addEntity(entity);
	}
	
	/**
	 * Remove an entity from the map entity list.
	 * 
	 * @param entity the entity to be removed
	 */
	public void removeEntity(final IEntity entity) {
		panel.removeEntity(entity);
	}
	
	/**
	 * Request redrawing the map screen if the needed.
	 */
	public void refresh() {
		panel.refresh();
	}
	
	/**
	 * Update the map with new data.
	 * 
	 * @param cd
	 *            The collision map.
	 * @param pd  
	 *      	  The protection map.
	 * @param gc
	 *            A graphics configuration.
	 * @param zone
	 *            The zone name.
	 */
	public void update(final CollisionDetection cd, final CollisionDetection pd, final GraphicsConfiguration gc,
			final String zone) {
		panel.update(cd, pd, gc, zone);
	}
}
