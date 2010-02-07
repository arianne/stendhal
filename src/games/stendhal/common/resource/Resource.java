/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.resource;

import java.io.InputStream;

/**
 *
 * @author silvio
 */
public interface Resource
{
	InputStream getInputStream();
	String      getURI();
	boolean     exists();
}
