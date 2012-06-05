/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

/**
 * records information about kill
 *
 * @author hendrik
 */
public class RequiredKillsInfo {

	private String name;
	private int requiredSolo;
	private int requiredMaybeShared;

	/**
	 * RequiredKillsInfo
	 */
	public RequiredKillsInfo() {
		super();
	}

	/**
	 * RequiredKillsInfo
	 *
	 * @param name name of creature
	 * @param requiredSolo number of kills the player must do alone
	 * @param requiredMaybeShared number of kills the player may do alone or with help
	 */
	public RequiredKillsInfo(String name, int requiredSolo, int requiredMaybeShared) {
		this.name = name;
		this.requiredSolo = requiredSolo;
		this.requiredMaybeShared = requiredMaybeShared;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the requiredSolo
	 */
	public int getRequiredSolo() {
		return requiredSolo;
	}

	/**
	 * @param requiredSolo the requiredSolo to set
	 */
	public void setRequiredSolo(int requiredSolo) {
		this.requiredSolo = requiredSolo;
	}

	/**
	 * @return the requiredMaybeShared
	 */
	public int getRequiredMaybeShared() {
		return requiredMaybeShared;
	}

	/**
	 * @param requiredMaybeShared the requiredMaybeShared to set
	 */
	public void setRequiredMaybeShared(int requiredMaybeShared) {
		this.requiredMaybeShared = requiredMaybeShared;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + requiredMaybeShared;
		result = prime * result + requiredSolo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RequiredKillsInfo other = (RequiredKillsInfo) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (requiredMaybeShared != other.requiredMaybeShared) {
			return false;
		}
		if (requiredSolo != other.requiredSolo) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RequiredKillsInfo [name=" + name + ", requiredSolo=" + requiredSolo
				+ ", requiredMaybeShared=" + requiredMaybeShared + "]";
	}


}
