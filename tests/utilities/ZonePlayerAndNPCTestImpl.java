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
package utilities;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * NPCTest is the base class for tests dealing with NPCs.
 *
 * @author Martin Fuchs
 */
public abstract class ZonePlayerAndNPCTestImpl extends ZoneAndPlayerTestImpl {

	private final List<String> npcNames = new LinkedList<>();

	private final Map<ZoneConfigurator, String> zoneConfiguratorsAndZones = new HashMap<>();

	protected ZonePlayerAndNPCTestImpl() {
	}

	/**
	 * Register NPC names for cleanup in tearDown().
	 * @param zoneName
	 *
	 * @param npcNames
	 */
	protected ZonePlayerAndNPCTestImpl(final String zoneName, final String... npcNames) {
		super(zoneName);

		assertTrue(npcNames.length > 0);

		for (final String npcName : npcNames) {
			this.npcNames.add(npcName);
		}
    }

	@Before
	@Override
	public void setUp() throws Exception {
		for (Map.Entry<ZoneConfigurator, String> configuratorToZone : zoneConfiguratorsAndZones.entrySet()) {
			setupZone(configuratorToZone.getValue(), configuratorToZone.getKey());
		}

		super.setUp();

		for (final String npcName : npcNames) {
			resetNPC(npcName);
		}
	}

	@Override
	@After
	public void tearDown() throws Exception {
		for (final String npcName : npcNames) {
			removeNPC(npcName);
		}

		super.tearDown();
	}

	/**
	 * Return the SpeakerNPC of the given name.
	 *
	 * @param npcName
	 * @return SpeakerNPC
	 */
	protected SpeakerNPC getNPC(final String npcName) {
		final SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);

		assertNotNull(npc);

		return npc;
	}

	protected void addZoneConfigurator(ZoneConfigurator zoneConfigurator, String zoneName) {
		zoneConfiguratorsAndZones.put(zoneConfigurator, zoneName);
	}

	protected void setNpcNames(final String... npcNames) {
		assertTrue(npcNames.length > 0);

		for (final String npcName : npcNames) {
			this.npcNames.add(npcName);
		}
    }
}
