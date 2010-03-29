package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.util.List;

public class TradeCenterSign2DView extends Sign2DView {

	/* (non-Javadoc)
	 * @see games.stendhal.client.gui.j2d.entity.Entity2DView#buildActions(java.util.List)
	 */
	@Override
	protected void buildActions(List<String> list) {
		list.add(ActionType.USE.getRepresentation());
		super.buildActions(list);
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.gui.j2d.entity.Entity2DView#onAction(games.stendhal.client.entity.ActionType)
	 */
	@Override
	public void onAction(ActionType at) {
		switch (at) {
		case USE:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;
		default:
			super.onAction(at);
			break;
		}
	}
	

	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	public StendhalCursor getCursor() {
		return StendhalCursor.LOOK;
	}
}
