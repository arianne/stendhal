package conf;

public class PortalTestObject {
	private String zone;

	private String name;

	private String destZone;

	private String destName;

	PortalTestObject(String zone, String name, String destZone, String destName) {
		super();
		this.zone = zone;
		this.name = name;
		this.destZone = destZone;
		this.destName = destName;
	}

	public PortalTestObject() {
		zone = "";
		name = "";
		destZone = "";
		destName = "";
	}

	boolean isDestinationOf(PortalTestObject source) {
		if (source == null) {
			return false;
		}
		if (source.destName.equals("") || source.destZone.equals("")) {
			return false;
		}
		return this.name.equals(source.destName)
				&& this.zone.equals(source.destZone);

	}

	public boolean hasDestination() {

		return !(destName.equals("") && destZone.equals(""));
	}

	@Override
	public String toString() {
		return "ref: (" + zone + " / " + name + ") -> (" + destZone + "/"
				+ destName + ")";
	}

}
