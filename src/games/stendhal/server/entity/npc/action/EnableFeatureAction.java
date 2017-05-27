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
package games.stendhal.server.entity.npc.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Enables a client side feature.
 */
@Dev(category=Category.OTHER, label="Feature+")
public class EnableFeatureAction implements ChatAction {

	private final String feature;
	private final String value;

	/**
	 * Creates a new EnableFeatureAction.
	 *
	 * @param feature name of feature
	 */
	public EnableFeatureAction(final String feature) {
		this(feature, "");
	}

	/**
	 * Creates a new EnableFeatureAction.
	 *
	 * @param feature name of feature
	 * @param value value of feature (<code>null</code> means disabled, "" means enabled without value)
	 */
	@Dev
	public EnableFeatureAction(final String feature, final String value) {
		this.feature = checkNotNull(feature);
		this.value = value;
	}


	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		player.setFeature(feature, value);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("enable feature <");
		sb.append(feature);
		sb.append("m ");
		sb.append(value);
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return 5171 * feature.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof EnableFeatureAction)) {
			return false;
		}
		EnableFeatureAction other = (EnableFeatureAction) obj;
		return feature.equals(other.feature)
			&& Objects.equal(value, other.value);
	}

}
