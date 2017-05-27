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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A <code>ListModel</code> for group members. The group leader is always kept
 * first.
 */
class MemberListModel extends AbstractListModel<Member> implements Iterable<Member> {
	private List<Member> memberList = new ArrayList<Member>();
	private Map<String, Member> memberMap = new HashMap<String, Member>();
	private final MemberListHealthListener healthListener = new MemberListHealthListener();

	@Override
	public Member getElementAt(int index) {
		return memberList.get(index);
	}

	@Override
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
	 * @param members list of members
	 */
	void setMembers(List<String> members) {
		if (members == null) {
			int size = memberList.size();
			memberMap.clear();
			memberList.clear();
			if (size > 0) {
				this.fireIntervalRemoved(this, 0, size - 1);
			}

			return;
		}
		// Find out the added members
		List<String> newMembers = new ArrayList<String>(members);
		newMembers.removeAll(memberMap.keySet());
		addMembers(newMembers);
		// Then the removed members
		List<String> removedMembers = new ArrayList<String>(memberMap.keySet());
		removedMembers.removeAll(members);
		removeMembers(removedMembers);
	}

	/**
	 * Add a group of new members.
	 *
	 * @param newMembers list of new members
	 */
	private void addMembers(List<String> newMembers) {
		if (newMembers.isEmpty()) {
			return;
		}
		int startIndex = -1;
		int endIndex = -1;
		for (String name : newMembers) {
			Member member = new Member(name);
			member.setChangeListener(healthListener);
			memberMap.put(name, member);
			memberList.add(member);
			int index = memberList.indexOf(member);
			if (startIndex == -1) {
				startIndex = index;
				endIndex = index;
			} else {
				startIndex = Math.min(startIndex, index);
				endIndex = Math.max(endIndex, index);
			}
		}
		fireIntervalAdded(this, startIndex, endIndex);
	}

	/**
	 * Remove a group of members.
	 *
	 * @param members removed members
	 */
	private void removeMembers(List<String> members) {
		if (members.isEmpty()) {
			return;
		}
		int startIndex = -1;
		int endIndex = -1;
		for (String name : members) {
			Member member = memberMap.remove(name);
			int index = memberList.indexOf(member);
			memberList.remove(index);
			if (startIndex == -1) {
				startIndex = index;
				endIndex = index;
			} else {
				startIndex = Math.min(startIndex, index);
				endIndex = Math.max(endIndex, index);
			}
		}
		fireIntervalRemoved(this, startIndex, endIndex);
	}

	/**
	 * To be called when a member changes a value that makes a difference in
	 * drawing it.
	 *
	 * @param member member whose attributes changed
	 */
	void memberChanged(Member member) {
		int index = memberList.indexOf(member);
		if (index != -1) {
			this.fireContentsChanged(this, index, index);
		}
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

	@Override
	public Iterator<Member> iterator() {
		return memberList.iterator();
	}

	/**
	 * Listener significant HP ratio changes that happen in any of the members.
	 */
	private class MemberListHealthListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			final Object source = e.getSource();
			if (source instanceof Member) {
				/*
				 * HP changes can come from the game loop. Resizes also result
				 * in ratio changes, and they come from EDT, but we can ignore
				 * those as they result in redraws anyway.
				 */
				if (!SwingUtilities.isEventDispatchThread()) {
					memberChanged((Member) source);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							memberChanged((Member) source);
						}
					});
				}
			}
		}
	}
}
