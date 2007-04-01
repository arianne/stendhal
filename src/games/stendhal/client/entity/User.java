package games.stendhal.client.entity;

import java.awt.Color;
import java.util.List;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.WorldObjects;
import games.stendhal.common.Direction;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;


public class User extends Player {

	public User(RPObject object) throws AttributeNotFoundException {
		super(object);
		
	}

	@Override
    public void onMove(int x, int y, Direction direction, double speed) {
	   super.onMove(x, y, direction, speed);
			WorldObjects.firePlayerMoved(this);
    }

	@Override
    protected void buildOfferedActions(List<String> list) {
	   
	    super.buildOfferedActions(list);
	    list.remove(ActionType.ADD_BUDDY.getRepresentation());
	   	list.add(ActionType.SET_OUTFIT.getRepresentation());
		
	   	if (list.contains(ActionType.ATTACK.getRepresentation())) {
				list.remove(ActionType.ATTACK.getRepresentation());
			}
		
	   	if (this.rpObject.has("sheep")) {
				list.add(ActionType.LEAVE_SHEEP.getRepresentation());
			}
		}
	  
	@Override
	public void onAction(ActionType at, String... params) {

		// ActionType at =handleAction(action);
		RPAction rpaction;
		switch (at) {
			case SET_OUTFIT:
				StendhalUI.get().chooseOutfit();
				break;

			case LEAVE_SHEEP:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				rpaction.put("target", "-1");
				at.send(rpaction);
				playSound("sheep-chat-2", 15, 50);
				break;
			default:
				super.onAction(at, params);
				break;
		}

	}

	
    //TODO: verify if OnAway is still to be usable or not
	@Override
    protected void onAway(String message) {
	    
	    super.onAway(message);
	    StendhalUI.get().addEventLine(
		        (message != null) ? "You have been marked as being away."
		                : "You are no longer marked as being away.", Color.orange);
    }

	@Override
    public void onEnter(int _x, int _y) {
		WorldObjects.firePlayerMoved(this);
    }

    }


