package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;

import java.util.List;

public class UseableRing2DView extends Ring2DView {

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
