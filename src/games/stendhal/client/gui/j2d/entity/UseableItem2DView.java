package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.UseableItem;

import java.util.List;

import org.apache.log4j.Logger;
import marauroa.common.game.RPAction;

/**
 * The 2D view of a useable item.
 */
public class UseableItem2DView extends StackableItem2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger.getLogger(UseableItem2DView.class);

	/**
	 * Create a 2D view of a useable item.
	 * 
	 * @param item
	 *            The entity to render.
	 */
	public UseableItem2DView(final UseableItem item) {
		super(item);
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 * 
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.USE.getRepresentation());

		super.buildActions(list);
	}
}
