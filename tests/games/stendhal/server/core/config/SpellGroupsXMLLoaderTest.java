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
package games.stendhal.server.core.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.transformer.SpellTransformer;
import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

public class SpellGroupsXMLLoaderTest {

	@Before
	public void setUp() {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testLoad() throws URISyntaxException, SAXException, IOException {
		SpellGroupsXMLLoader loader = new SpellGroupsXMLLoader(new URI("testspells.xml"));
		List<DefaultSpell> list = loader.load();
		assertThat(Boolean.valueOf(list.isEmpty()), is(Boolean.FALSE));
		DefaultSpell spell = list.get(0);
		assertThat(spell.getName(), is("healtest"));
		assertThat(spell.getNature(), is(Nature.LIGHT));
		assertThat(spell.getImplementationClass(), notNullValue());
		assertThat(spell.getImplementationClass().getName(), is("games.stendhal.server.entity.spell.HealingSpell"));
		assertThat(spell.getAmount(),is(Integer.valueOf(100)));
		assertThat(spell.getAtk(),is(Integer.valueOf(0)));
		assertThat(spell.getCooldown(),is(Integer.valueOf(3)));
		assertThat(spell.getDef(),is(Integer.valueOf(0)));
		assertThat(spell.getLifesteal(),is(Double.valueOf(0.5)));
		assertThat(spell.getMana(),is(Integer.valueOf(5)));
		assertThat(spell.getMinimumLevel(),is(Integer.valueOf(10)));
		assertThat(spell.getRange(),is(Integer.valueOf(10)));
		assertThat(spell.getRate(),is(Integer.valueOf(1)));
		assertThat(spell.getRegen(),is(Integer.valueOf(100)));
		SingletonRepository.getEntityManager().addSpell(spell);
		Spell entity = SingletonRepository.getEntityManager().getSpell("healtest");
		assertThat(entity, notNullValue());
		assertThat(entity.getName(), is("healtest"));
		assertThat(entity.getNature(), is(Nature.LIGHT));
		assertThat(entity.getAmount(),is(Integer.valueOf(100)));
		assertThat(entity.getAtk(),is(Integer.valueOf(0)));
		assertThat(entity.getCooldown(),is(Integer.valueOf(3)));
		assertThat(entity.getDef(),is(Integer.valueOf(0)));
		assertThat(entity.getLifesteal(),is(Double.valueOf(0.5)));
		assertThat(entity.getMana(),is(Integer.valueOf(5)));
		assertThat(entity.getMinimumLevel(),is(Integer.valueOf(10)));
		assertThat(entity.getRange(),is(Integer.valueOf(10)));
		assertThat(entity.getRate(),is(Integer.valueOf(1)));
		assertThat(entity.getRegen(),is(Integer.valueOf(100)));
		assertThat(entity.getClass().getName(), is("games.stendhal.server.entity.spell.HealingSpell"));
		entity.setID(new ID(1, "some_zone"));
		RPObject object = new SpellTransformer().transform(entity);
		assertThat(object, is((RPObject)entity));
	}

}
