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
package games.stendhal.client.gui.group;

/**
 * Represents a group member. Present members are those that the client has
 * direct information about, ie. the players on the same zone.
 */
class Member implements Comparable<Member> {
	private final String name;
	private boolean leader;
	private float hpRatio;
	private boolean present;
	
	/**
	 * Create a new member.
	 * 
	 * @param name name of the buddy
	 */
	Member(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of the member.
	 * 
	 * @return name
	 */
	String getName() {
		return name;
	}
	
	/**
	 * Check if the member is the group leader.
	 * 
	 * @return <code>true</code> if the member is the group leader,
	 *	<code>false</code> otherwise
	 */
	boolean isLeader() {
		return leader;
	}
	
	/**
	 * Set the leader status of the group member.
	 * 
	 * @param status the new leader status
	 * @return <code>true</code> if the leader changed, <code>false</code>
	 * 	otherwise
	 */
	boolean setLeader(boolean status) {
		boolean changed = leader != status;
		leader = status;
		return changed;
	}
	
	/**
	 * Check if the member is present.
	 * 
	 * @return <code>true</code> if the member is present, <code>false</code>
	 * 	otherwise
	 */
	boolean isPresent() {
		return present;
	}
	
	/**
	 * Set the member present or absent.
	 * 
	 * @param present
	 */
	void setPresent(boolean present) {
		this.present = present;
	}
	
	/**
	 * Get the ratio of the member's current HP vs her maximum HP. The value
	 * is reliable only if the member is present.
	 * 
	 * @return HP ratio
	 */
	float getHpRatio() {
		return hpRatio;
	}
	
	/**
	 * Set the ratio of the member's current HP vs her maximum HP.
	 * 
	 * @param ratio new HP ratio 
	 * @return <code>true</code> if the ratio changed significantly from the old
	 * 	stored value, <code>false</code> otherwise
	 */
	boolean setHpRatio(float ratio) {
		if (Math.abs(ratio - hpRatio) < 0.01) {
			return false;
		}
		this.hpRatio = ratio;
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Member) {
			return name.equals(((Member) obj).name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public int compareTo(Member member) {
		// There really should be only one leader, but check for consistent
		// ordering
		if (leader != member.leader) {
			return (leader) ? -1 : 1;
		}
		
		return name.compareToIgnoreCase(member.name);
	}
}
