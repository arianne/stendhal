/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.behaviour.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class CollectingGroupQuestBehaviour {
	private String questSlot;
	private String projectName;
	private Map<String, Integer> required;
	private Map<String, Integer> chunkSize;
	private Map<String, String> hints;
	private Map<String, Integer> progress;

	public CollectingGroupQuestBehaviour(String questSlot, Map<String, Integer> required,
			Map<String, Integer> chunkSize, Map<String, String> hints, Map<String, Integer> progress) {
		this.questSlot = questSlot;
		this.required = new LinkedHashMap<>(required);
		this.chunkSize = ImmutableMap.copyOf(chunkSize);
		this.hints = ImmutableMap.copyOf(hints);
		this.progress = new HashMap<>(progress);
	}

	public String getQuestSlot() {
		return questSlot;
	}

	/**
	 * counts the items in the parameter map
	 *
	 * @param map map to count items from
	 * @return total amount of items
	 */
	private int countItems(Map<String, Integer> map) {
		int res = 0;
		for (Integer value : map.values()) {
			res = res + value.intValue();
		}
		return res;
	}

	public int getProgressPercent() {
		return countItems(progress) * 100 / countItems(required);
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Map<String, Integer> calculateRemainingItems() {
		Map<String, Integer> res = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : required.entrySet()) {
			Integer remaining = entry.getValue();
			Integer collected = progress.get(entry.getKey());
			if (collected != null) {
				remaining = Integer.valueOf(remaining.intValue() - collected.intValue());
				if (remaining.intValue() > 0) {
					res.put(entry.getKey(), remaining);
				}
			} else {
				res.put(entry.getKey(), entry.getValue());
			}
		}
		return res;
	}

	public Integer getChunkSize(String item) {
		return chunkSize.get(item);
	}

	public String getHint(String item) {
		return hints.get(item);
	}

	public void addProgress(String item, Integer stackSize) {
		Integer quantity = progress.get(item);
		if (quantity == null) {
			quantity = Integer.valueOf(0);
		}
		quantity = Integer.valueOf(quantity.intValue() + stackSize);
		progress.put(item, quantity);
	}
}
