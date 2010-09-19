/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
