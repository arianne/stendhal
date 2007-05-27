package games.stendhal.client.entity;

import java.util.List;

import marauroa.common.game.RPAction;

public class CarrotGrower extends GrainField {
	/**
	 * Create a carrot/vegatable grower.
	 */
	public CarrotGrower()  {
		super(1, 1);
	}


	@Override
	protected void buildOfferedActions(List<String> list) {

		super.buildOfferedActions(list);
		if (list.contains(ActionType.HARVEST.getRepresentation())) {
			list.remove(ActionType.HARVEST.getRepresentation());
		}
		list.add(ActionType.PICK.getRepresentation());
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.PICK;
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at=handleAction(action);
		switch (at) {
			case PICK:
				RPAction rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();
				rpaction.put("target", id);
				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}
	}

}
