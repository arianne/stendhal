package games.stendhal.client.entity;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject.ID;



public class Gate implements IEntity {

	private double x;
	private double y;
	private double width;
	private double height;
	private RPObject rpObject;

	public void addChangeListener(EntityChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void fillTargetInfo(RPAction action) {
		// TODO Auto-generated method stub
		
	}

	public Rectangle2D getArea() {
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}

	public Rectangle2D getAudibleArea() {
	
		double radius = 6;
		return new Rectangle2D.Double(getX() - radius / 2, getY() - radius / 2, radius, radius);
	}

	public String getEntityClass() {
		if (rpObject == null) {
			return "";
		}
		return this.rpObject.get("class");
	}

	public String getEntitySubClass() {
		if (rpObject == null) {
			return "";
		}
		return this.rpObject.get("subclass");
	}

	public double getHeight() {

		return height;
	}

	public ID getID() {
		if (rpObject == null) {
			return null;
		} else {
			return rpObject.getID();
		}
		
	}

	public String getName() {
		return "";
	}

	public RPObject getRPObject() {
		return rpObject;
	}

	public int getResistance() {
		if (rpObject == null) {
			return 0;	
		}
		
		return rpObject.getInt("resistance");
	}

	public int getResistance(final IEntity entity) {
		return getResistance();
	}

	public RPSlot getSlot(String name) {
		
		return null;
	}

	public String getTitle() {
		if (rpObject == null) {
			return null;
		}
		return rpObject.get("title");
	}

	public String getType() {
		if (rpObject == null) {
			return null;
		}
		return rpObject.get("type");
	}

	public int getVisibility() {
		
		return 100;
	}

	public double getWidth() {
		
		return width;
	}

	public double getX() {
		
		return x;
	}

	public double getY() {
		return y;
	}

	public void initialize(RPObject object) {
		x = object.getInt("x");
		y = object.getInt("y");
		width = object.getInt("width");
		height = object.getInt("height");
		this.rpObject = object;
	}

	public boolean isObstacle(IEntity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOnGround() {
		return true;
	}

	public void release() {
		// do nothing
		
	}

	public void removeChangeListener(EntityChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void setAudibleRange(double range) {
		// TODO Auto-generated method stub
		
	}

	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}

	
}
