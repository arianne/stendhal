package games.stendhal.client.entity;

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.WorldObjects;
import games.stendhal.client.soundreview.HearingArea;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.FeatureList;
import games.stendhal.common.Grammar;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

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

	/**
	 * When the entity's position changed.
	 *
	 * @param	x		The new X coordinate.
	 * @param	y		The new Y coordinate.
	 */
	@Override
	protected void onPosition(final double x, final double y) {
		super.onPosition(x, y);

		WorldObjects.firePlayerMoved();
		HearingArea.set(x, y);
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

	public  boolean hasPet() {
		if (rpObject== null) {
	        return false;
        }
		return rpObject.has("pet");
    }

	@Override
    public void onHealed(final int amount) {
	   
	    super.onHealed(amount);
	   
			StendhalUI.get().addEventLine(
			        getTitle() + " heals " + Grammar.quantityplnoun(amount, "health point") + ".", Color.green);
		
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


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		if(object.has("features")) {
			features.decode(object.get("features"));
		}
      }


	@Override
    public void onChangedAdded(final RPObject base, final  RPObject diff) {
		modificationCount++;
		super.onChangedAdded(base, diff);

		if(diff.has("features")) {
			features.decode(diff.get("features"));
		}
    }

	@Override
    public void onChangedRemoved(final RPObject base, final RPObject diff) {
		modificationCount++;
	    super.onChangedRemoved(base, diff);

		if(diff.has("features")) {
			features.clear();
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
