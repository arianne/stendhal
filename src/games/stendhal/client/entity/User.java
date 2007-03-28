package games.stendhal.client.entity;

import java.util.List;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.WorldObjects;
import games.stendhal.common.Direction;
import marauroa.common.game.AttributeNotFoundException;
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
	  


    }


