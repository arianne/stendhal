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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

/**
 * A <code>ListModel</code> for group members. The group leader is always kept
 * first.
 */
public class MemberListModel extends AbstractListModel {
	// Keep FindBugs happy
	private static final long serialVersionUID = -5983645746012160833L;
	
	private List<Member> memberList = new ArrayList<Member>();
	private Map<String, Member> memberMap = new HashMap<String, Member>();

	public Object getElementAt(int index) {
		return memberList.get(index);
	}

	public int getSize() {
		return memberList.size();
	}
	
	/**
	 * Set the current leader. The leader must be a member of the group, thus
	 * this method must not be called before setting the members.
	 * 
	 * @param name name of the leader 
	 */
	void setLeader(String name) {
		Member leader = memberMap.get(name);
		int index1 = memberList.indexOf(leader);
		boolean changed = leader.setLeader(true);
		if (changed) {
			// find the old leader and demote her
			for (Member member : memberList) {
				if (member.isLeader() && !member.getName().equals(leader.getName())) {
					member.setLeader(false);
					break;
				}
			}
			
			Collections.sort(memberList);
			int index2 = memberList.indexOf(leader);
			fireContentsChanged(this, index1, index2);
		}
	}
	
	/**
	 * Set the list of current group members.
	 * 
	 * @param members
	 */
	void setMembers(List<String> members) {
		// Very dumb way to update the list. Adding and removing individual
		// members as they change would be cleaner
		
		int size = memberList.size();
		memberMap.clear();
		memberList.clear();
		if (size > 0) {
			this.fireIntervalRemoved(this, 0, size - 1);
		}
		
		if (members == null) {
			return;
		}
		
		for (String name : members) {
			if (memberMap.containsKey(name)) {
				// Already a member. Skip to next
				continue;
			} else {
				Member member = new Member(name);
				memberList.add(member);
				memberMap.put(name, member);
			}
		}
		this.fireIntervalAdded(this, 0, members.size() - 1);
	}
	
	/**
	 * Get data of a specified group member.
	 *  
	 * @param name member name
	 * @return member data, or <code>null</code> if there's no such player in
	 * 	the group
	 */
	Member getMember(String name) {
		return memberMap.get(name);
	}
}
