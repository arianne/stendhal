/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 *
 * @author silvio
 */
public class ResourceLocator
{
	public static interface Locator
	{
		public InputStream locate     (URI uri);
		public boolean     isLocatable(URI uri);
	}

	private static class Unlocatable implements Locator
	{
		public InputStream locate     (URI uri) { return null;  }
		public boolean     isLocatable(URI uri) { return false; }
	}
	
	private static class ResourceImpl implements Resource
	{
		final URI     mURI;
		final String  mURIString;
		final Locator mLocator;

		public ResourceImpl(URI uri, String uriString, Locator locator)
		{
			mURI       = uri;
			mURIString = uriString;
			mLocator   = locator;
		}

		public InputStream getInputStream()
		{
			return mLocator.locate(mURI);
		}

		public String  getURI() { return mURIString;                 }
		public boolean exists() { return mLocator.isLocatable(mURI); }
	}

	private final HashMap<String,Locator> mLocatorMap     = new HashMap<String,Locator>();
	private Locator                       mDefaultLocator = new Unlocatable();

	protected Locator chooseLocator(URI uri)
	{
		Locator locator = mLocatorMap.get(uri.getScheme());

		if(locator != null)
			return locator;

		return mDefaultLocator;
	}
	
	public void setLocator(String uriScheme, Locator locator)
	{
		assert locator != null;
		mLocatorMap.put(uriScheme, locator);
	}

	public void setDefaultLocator(Locator locator)
	{
		assert locator != null;
		mDefaultLocator = locator;
	}

	public void removeLocator(String uriScheme)
	{
		mLocatorMap.remove(uriScheme);
	}

	public Resource getResource(URI uri)
	{
		return new ResourceImpl(uri, uri.toString(), chooseLocator(uri));
	}

	public Resource getResource(String uriString)
	{
		try
		{
			return getResource(new URI(uriString));
		}
		catch(URISyntaxException exception)
		{
			assert false: "invalid URI syntax!";
		}

		return new ResourceImpl(null, uriString, mDefaultLocator);
	}

	public InputStream getInputStream(URI uri)
	{
		return chooseLocator(uri).locate(uri);
	}

	public InputStream getInputStream(String uriString)
	{
		try
		{
			return getInputStream(new URI(uriString));
		}
		catch(URISyntaxException exception)
		{
			assert false: "invalid URI syntax!";
		}

		return null;
	}

	public boolean isLocatable(URI uri)
	{
		return chooseLocator(uri).isLocatable(uri);
	}

	public boolean isLocatable(String uriString)
	{
		try
		{
			return isLocatable(new URI(uriString));
		}
		catch(URISyntaxException exception)
		{
			assert false: "invalid URI syntax!";
		}

		return false;
	}
}
