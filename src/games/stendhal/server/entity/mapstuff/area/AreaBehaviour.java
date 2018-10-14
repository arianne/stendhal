package games.stendhal.server.entity.mapstuff.area;

public interface AreaBehaviour {

	/**
	 * activates the behaviour by adding it to an area entity
	 *
	 * @param parentAreaEntity area entity
	 */
	public abstract void addToWorld(AreaEntity parentAreaEntity);

	/**
	 * sets the area entity
	 */
	public abstract void removeFromWorld();

}
