package games.stendhal.tools.loganalyser.itemlog.contraband;

/**
 * Information about an item
 *
 * @author hendrik
 */
public class ItemInfo {

	private String name;
	private String itemid;
	private String quantity;
	private String owner;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	

}
