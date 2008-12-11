/**
 * 
 */
package games.stendhal.client.gui.chattext;

import games.stendhal.common.filter.CollectionFilter;
import games.stendhal.common.filter.FilterCriteria;

class StringPrefixFilter extends CollectionFilter<String> {
	public StringPrefixFilter(final String prefix) {
		this.addFilterCriteria(new FilterCriteria<String>() {

			public boolean passes(final String o) {
				return prefix.length() > 0 && o.toLowerCase().startsWith(prefix.toLowerCase());
			}
		});
		
	}
	
}
