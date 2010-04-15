package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;

public class SpellGroupsXMLLoaderTest {
	
	@Test
	public void testLoad() throws URISyntaxException, SAXException, IOException {
		SpellGroupsXMLLoader loader = new SpellGroupsXMLLoader(new URI("testspells.xml"));
		List<DefaultSpell> list = loader.load();
		assertThat(Boolean.valueOf(list.isEmpty()), is(Boolean.FALSE));
		DefaultSpell spell = list.get(0);
		assertThat(spell.getName(), is("heal"));
		assertThat(spell.getImplementationClass(), notNullValue());
		assertThat(spell.getImplementationClass().getName(), is("games.stendhal.server.entity.spell.Spell"));
		assertThat(spell.getAmount(),is(Integer.valueOf(100)));
		assertThat(spell.getAtk(),is(Integer.valueOf(0)));
		assertThat(spell.getCooldown(),is(Integer.valueOf(3)));
		assertThat(spell.getDef(),is(Integer.valueOf(0)));
		assertThat(spell.getLifesteal(),is(Double.valueOf(0.5)));
		assertThat(spell.getMana(),is(Integer.valueOf(5)));
		assertThat(spell.getMinimumLevel(),is(Integer.valueOf(0)));
		assertThat(spell.getRange(),is(Integer.valueOf(10)));
		assertThat(spell.getRate(),is(Integer.valueOf(1)));
		assertThat(spell.getRegen(),is(Integer.valueOf(100)));
	}

}
