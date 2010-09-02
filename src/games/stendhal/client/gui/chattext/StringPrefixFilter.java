/**
 * 
 */
package games.stendhal.client.gui.chattext;

import games.stendhal.common.filter.CollectionFilter;
import games.stendhal.common.filter.FilterCriteria;

import java.util.Locale;

class StringPrefixFilter extends CollectionFilter<String> {
	public StringPrefixFilter(final String prefix) {
		this.addFilterCriteria(new FilterCriteria<String>() {

			public boolean passes(final String o) {
				return prefix.length() > 0 && o.toLowerCase(Locale.ENGLISH).startsWith(prefix.toLowerCase(Locale.ENGLISH));
			}
		});
		
	}
	
}
