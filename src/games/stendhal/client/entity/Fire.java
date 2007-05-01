package games.stendhal.client.entity;

import java.util.List;

/**
 *
 */
public class Fire extends AnimatedStateEntity {

	@Override
	protected void buildOfferedActions(List<String> list) {
		list.clear();
	}

	@Override
	public ActionType defaultAction() {
		return null;
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new Fire2DView(this);
	}
}
