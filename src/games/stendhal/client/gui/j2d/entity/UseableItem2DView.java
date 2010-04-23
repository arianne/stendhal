package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.util.List;

/**
 * The 2D view of a useable item.
 */
class UseableItem2DView extends StackableItem2DView {

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

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.ITEM_USE;
	}
}
