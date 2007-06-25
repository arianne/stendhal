package games.stendhal.client.entity;

import java.util.List;

import marauroa.common.game.RPAction;

public class Box extends Item {

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
		list.remove(ActionType.USE.getRepresentation());
		list.add(ActionType.OPEN.getRepresentation());

	}

	@Override
	public ActionType defaultAction() {
		return ActionType.OPEN;
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		switch (at) {
			case OPEN:
				RPAction rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();

				if (params.length > 0) {
					rpaction.put("baseobject", params[0]);
					rpaction.put("baseslot", params[1]);
					rpaction.put("baseitem", id);
				} else {
					rpaction.put("target", id);
				}

				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}

	}

}
