package games.stendhal.client.entity;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.WorldObjects;
import games.stendhal.client.soundreview.HearingArea;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Direction;
import games.stendhal.common.FeatureList;
import games.stendhal.common.Grammar;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;


public class User extends Player {

	private static User _instance=null ;

	/**
	 * Client features.
	 */
	private FeatureList	features;

	
	public static boolean isNull(){
		
		return _instance==null;
		
	}
	
	public static User get(){
		return _instance;
	}
	

	public User()  {
		super();
		_instance = this;
		modificationCount = 0;
		features = new FeatureList();
	}

	@Override
    public void onMove(final int x, final int y, final Direction direction, final double speed) {
	   super.onMove(x, y, direction, speed);
	   
	   HearingArea.set(x,y);
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
		
	   	if (rpObject.has("sheep")) {
				list.add(ActionType.LEAVE_SHEEP.getRepresentation());
			}
		}
	  
	@Override
	public void onAction(final ActionType at, final String... params) {

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
				SoundMaster.play("sheep-2.wav",x,y);
				break;
			default:
				super.onAction(at, params);
				break;
		}

	}

	private int modificationCount;
	/**
	 * returns the modificationCount. This counter is increased each time a
	 * perception is received from the server (so all serverside changes
	 * increases the mod-count). This counters purpose is to be sure that this
	 * entity is modified or not (ie for gui elements).
	 */
	public long getModificationCount() {
		return modificationCount;
	}
    //TODO: verify if OnAway is still to be usable or not
	@Override
    protected void onAway(final String message) {
	    
	    super.onAway(message);
	    StendhalUI.get().addEventLine(
		        (message != null) ? "You have been marked as being away."
		                : "You are no longer marked as being away.", Color.orange);
    }

	@Override
    public void onEnter(final int _x, final int _y) {
		WorldObjects.firePlayerMoved();
    }
	public static  boolean isAdmin() {
		
		if (isNull()) {
	        return false;
        }
		User me = User.get();
		if (me.rpObject== null) {
	        return false;
        }
		
		return me.rpObject.has("adminlevel") && (me.rpObject.getInt("adminlevel") >= 600);
	
	}

	public int getObjectID() {
	    return rpObject.getID().getObjectID();
    }

	public  boolean hasSheep() {
		if (rpObject== null) {
	        return false;
        }
		return rpObject.has("sheep");
    }

	@Override
    public void onHealed(final int amount) {
	   
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


	public String getFeature(String name) {
		return features.get(name);
	}


	public boolean hasFeature(String name) {
		return features.has(name);
	}


	public void onAdded(final RPObject base) {
		super.onAdded(base);

		if(base.has("features")) {
			features.decode(base.get("features"));
			changed();
		}
        }


	@Override
    public void onChangedAdded(final RPObject base, final  RPObject diff) throws AttributeNotFoundException {
		modificationCount++;
		super.onChangedAdded(base, diff);

		if(diff.has("features")) {
			features.decode(diff.get("features"));
			changed();
		}
    }

	@Override
    public void onChangedRemoved(final RPObject base, final RPObject diff) {
		modificationCount++;
	    super.onChangedRemoved(base, diff);

		if(diff.has("features")) {
			features.clear();
			changed();
		}
    }
	
	/**
	 * Returns true when the entity was modified since the
	 * <i>oldModificationCount</i>.
	 * 
	 * @param oldModificationCount
	 *            the old modificationCount
	 * @return true when the entity was modified, false otherwise
	 * @see #getModificationCount()
	 */
	public boolean isModified(final long oldModificationCount) {
		return oldModificationCount != modificationCount;
	}

	public static void setNull() {
		_instance =null;
		
	}
    
}


