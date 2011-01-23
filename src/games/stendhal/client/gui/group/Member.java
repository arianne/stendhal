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
 * Represents a group member.
 */
class Member implements Comparable<Member> {
	private final String name;
	private boolean leader;
	
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
		// There really should be only one member, but check for consistent
		// ordering
		if (leader != member.leader) {
			return (leader) ? -1 : 1;
		}
		
		return name.compareToIgnoreCase(member.name);
	}
}
