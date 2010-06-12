/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.resource;

import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author silvio
 */
public interface Locator
{
	public InputStream locate     (URI uri);
	public boolean     isLocatable(URI uri);
}
