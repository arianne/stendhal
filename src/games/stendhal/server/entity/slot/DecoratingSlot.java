package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

import java.io.IOException;
import java.util.Iterator;

import marauroa.common.game.DetailLevel;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObjectNotFoundException;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject.ID;
import marauroa.common.net.InputSerializer;
import marauroa.common.net.OutputSerializer;

/**
 * Integrates another slot into the owning object
 *
 * @author hendrik
 */
public class DecoratingSlot extends EntitySlot {
	private EntitySlot entitySlot = null;

	/**
	 * Sets the real slot
	 *
	 * @param slot real slot
	 */
	public void setEntitySlot(EntitySlot slot) {
		this.entitySlot = slot;
	}

	@Override
    public void add(RPObject arg0) {
	    entitySlot.add(arg0);
    }

	@Override
    public void assignValidID(RPObject arg0) {
	    entitySlot.assignValidID(arg0);
    }

	@Override
    public void clear() {
	    entitySlot.clear();
    }

	@Override
    public Object clone() {
	    return entitySlot.clone();
    }

	@Override
    public boolean equals(Object arg0) {
	    return entitySlot.equals(arg0);
    }

	@Override
    public RPObject get(ID arg0) throws RPObjectNotFoundException {
	    return entitySlot.get(arg0);
    }

	@Override
    public byte getCapacity() {
	    return entitySlot.getCapacity();
    }

	@Override
    public int getContainedDepth() {
	    return entitySlot.getContainedDepth();
    }

	@Override
    public RPObject getFirst() throws RPObjectNotFoundException {
	    return entitySlot.getFirst();
    }

	@Override
    public String getName() {
	    return entitySlot.getName();
    }

	@Override
    public int getNumberOfContainedItems() {
	    return entitySlot.getNumberOfContainedItems();
    }

	@Override
    public boolean has(ID arg0) {
	    return entitySlot.has(arg0);
    }

	@Override
    public boolean hasAsParent(ID arg0) {
	    return entitySlot.hasAsParent(arg0);
    }

	@Override
    public int hashCode() {
	    return entitySlot.hashCode();
    }

	@Override
    public boolean isFull() {
	    return entitySlot.isFull();
    }

	@Override
    public boolean isItemSlot() {
	    return entitySlot.isItemSlot();
    }

	@Override
    public boolean isReachableBy(Entity entity) {
	    return entitySlot.isReachableBy(entity);
    }

	@Override
    public Iterator<RPObject> iterator() {
	    return entitySlot.iterator();
    }

	@Override
    public void readObject(InputSerializer arg0) throws IOException, ClassNotFoundException {
	    entitySlot.readObject(arg0);
    }

	@Override
    public RPObject remove(ID arg0) throws RPObjectNotFoundException {
	    return entitySlot.remove(arg0);
    }

	@Override
    public void resetAddedAndDeletedRPObjects() {
	    entitySlot.resetAddedAndDeletedRPObjects();
    }

	@Override
    public void setAddedRPObject(RPSlot arg0) {
	    entitySlot.setAddedRPObject(arg0);
    }

	@Override
    public void setCapacity(int arg0) {
	    entitySlot.setCapacity(arg0);
    }

	@Override
    public void setDeletedRPObject(RPSlot arg0) {
	    entitySlot.setDeletedRPObject(arg0);
    }

	@Override
    public void setName(String arg0) {
	    entitySlot.setName(arg0);
    }

	@Override
    public int size() {
	    return entitySlot.size();
    }

	@Override
    public String toString() {
	    return "DecoratingSlot <" + entitySlot.toString() + ">";
    }

	@Override
    public void writeObject(OutputSerializer arg0, DetailLevel arg1) throws IOException {
	    entitySlot.writeObject(arg0, arg1);
    }

	@Override
    public void writeObject(OutputSerializer arg0) throws IOException {
	    entitySlot.writeObject(arg0);
    }
}
