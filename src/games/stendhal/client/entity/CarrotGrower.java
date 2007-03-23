package games.stendhal.client.entity;

import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class CarrotGrower extends GrainField {

	public CarrotGrower(RPObject object) throws AttributeNotFoundException {
		super(object);

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
	public void onAction(ActionType at, String... params) {
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
