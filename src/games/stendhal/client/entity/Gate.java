package games.stendhal.client.entity;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject.ID;



public class Gate implements IEntity {

	private double x;
	private double y;
	private double width;
	private double height;
	private RPObject rpObject;
	private double radius;

	public Gate() {
		radius = 6;
	}

	public void addChangeListener(final EntityChangeListener listener) {
		
	}

	public Rectangle2D getArea() {
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}

	public Rectangle2D getAudibleArea() {
	
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

	public RPSlot getSlot(final String name) {
		
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

	public void initialize(final RPObject object) {
		x = object.getInt("x");
		y = object.getInt("y");
		width = object.getInt("width");
		height = object.getInt("height");
		this.rpObject = object;
	}

	public boolean isObstacle(final IEntity entity) {
		return getResistance() > 0;
	}

	public boolean isOnGround() {
		return true;
	}
	
	public boolean isUser() {
		return false;
	}

	public void release() {
		// do nothing
		
	}

	public void removeChangeListener(final EntityChangeListener listener) {
		
	}

	public void setAudibleRange(final double range) {
		radius = range;
		
	}

	public void update(final int delta) {
		
	}

	
}
