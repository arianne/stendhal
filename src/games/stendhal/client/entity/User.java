package games.stendhal.client.entity;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.WorldObjects;
import games.stendhal.common.Direction;
import games.stendhal.common.Grammar;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;


public class User extends Player {

	static User _instance=null ;
	
	public static boolean isNull(){
		
		return _instance==null;
		
	}
	
	public static User get(){
		return _instance;
	}
	
	public User(RPObject object) throws AttributeNotFoundException {
		super(object);
		_instance = this;
		
	}
	public User()  {
		super();
		_instance = this;
		
	}

	@Override
    public void onMove(int x, int y, Direction direction, double speed) {
	   super.onMove(x, y, direction, speed);
			WorldObjects.firePlayerMoved();
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
		WorldObjects.firePlayerMoved();
    }
	public static  boolean isAdmin() {
		User me = User.get();
		if (me != null){
	      return me.rpObject.has("adminlevel") && (me.rpObject.getInt("adminlevel") >= 600);
		}
		else
			return false;
	}

	public int getObjectID() {
	    return this.rpObject.getID().getObjectID();
    }

	public  boolean hasSheep() {
		return this.rpObject.has("sheep");
    }

	@Override
    public void onHealed(int amount) {
	   
	    super.onHealed(amount);
	   
			StendhalUI.get().addEventLine(
			        getName() + " heals " + Grammar.quantityplnoun(amount, "health point") + ".", Color.green);
		
    }

	/**
     * the absolute world area (coordinates) where the player can possibly hear
     * sounds
     * @return Rectangle2D area
     */
    public Rectangle2D getHearingArea() {
    	final double HEARING_RANGE = 20;
    	double width = HEARING_RANGE * 2;
    	return new Rectangle2D.Double(getX() - HEARING_RANGE, getY() - HEARING_RANGE, width, width);
    }
	
	
    }


