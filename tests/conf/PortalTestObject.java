package conf;

public class PortalTestObject {
	private final String zone;

	private final String name;

	private final String destZone;

	private final String destName;

	PortalTestObject(final String zone, final String name, final String destZone, final String destName) {
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

	boolean isDestinationOf(final PortalTestObject source) {
		if (source == null) {
			return false;
		}
		if ("".equals(source.destName) || "".equals(source.destZone)) {
			return false;
		}
		return this.name.equals(source.destName)
				&& this.zone.equals(source.destZone);

	}

	public boolean hasDestination() {

		return !("".equals(destName) && "".equals(destZone));
	}

	@Override
	public String toString() {
		return "ref: (" + zone + " / " + name + ") -> (" + destZone + "/"
				+ destName + ")";
	}

}
