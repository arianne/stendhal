/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author silvio
 */
public class ResourceManager extends ResourceLocator
{
	/*private static class DefaultLocator implements Locator
	{
		public InputStream locate(URI uri)
		{
			try
			{
				return new FileInputStream(uri.getPath());
			}
			catch(FileNotFoundException ex) { }

			return null;
		}

		public boolean isLocatable(URI uri)
		{
			return new File(uri.getPath()).exists();
		}
	}//*/

	private interface SchemeLocator
	{
		void        addSearchPath(String path);
		InputStream locate       (URI uri);
		boolean     isLocatable  (URI uri);
	}

	private class CPSchemeLocator implements Locator, SchemeLocator
	{
		final ArrayList<String> mPaths = new ArrayList<String>();

		public void addSearchPath(String path)
		{
			if(path.endsWith("/"))
				path = path.substring(0, path.length() - 1);

			if(!path.startsWith("/"))
				path = "/" + path;
			
			mPaths.add(path);
		}

		public InputStream locate(URI uri)
		{
			for(String path: mPaths)
			{
				InputStream stream = this.getClass().getResourceAsStream(path + uri.getPath());

				if(stream != null)
					return stream;
			}

			return null;
		}

		public boolean isLocatable(URI uri)
		{
			InputStream stream = locate(uri);

			if(stream != null)
			{
				try
				{
					stream.close();
				}
				catch(IOException exception)
				{
					assert false: exception;
					stream = null;
				}
			}
			return stream != null;
		}
	}

	private class FSSchemeLocator implements Locator, SchemeLocator
	{
		final ArrayList<File> mDirectories = new ArrayList<File>();

		public void addSearchPath(String path)
		{
			File directory = new File(mRootDirectory, path);

			if(directory.isDirectory())
				mDirectories.add(directory);
		}

		public File locateFile(URI uri)
		{
			for(File directory: mDirectories)
			{
				File file = new File(directory, uri.getPath());
				
				if(file.isFile())
					return file;
			}

			return null;
		}
		
		public InputStream locate(URI uri)
		{
			File file = locateFile(uri);

			try
			{
				if(file != null)
					return new FileInputStream(file);
			}
			catch(FileNotFoundException ex) { }

			return null;
		}

		public boolean isLocatable(URI uri)
		{
			return locateFile(uri) != null;
		}
	}

	private final HashMap<String,SchemeLocator> mSchemeLocators = new HashMap<String,SchemeLocator>();
	private File                                mRootDirectory  = null;

	public void addScheme(String name, String ...searchPaths)
	{
		assert name != null;
		assert !mSchemeLocators.containsKey(name): "scheme " + name + " was already added";

		if(!mSchemeLocators.containsKey(name))
		{
			SchemeLocator locator = new CPSchemeLocator();

			mSchemeLocators.put(name, locator);
			setLocator(name, (Locator)locator);

			for(String path: searchPaths)
				locator.addSearchPath(path);
		}
	}

	public void addSearchPath(String scheme, String path)
	{
		SchemeLocator locator = mSchemeLocators.get(scheme);
		
		assert locator != null;
		assert path    != null;

		locator.addSearchPath(path);
	}

	public void setRootDirectory(String path)
	{
		if(path == null)
		{
			mRootDirectory = null;
		}
		else
		{
			File file = new File(path);
			
			if(file.isDirectory())
				mRootDirectory = file;
		}
	}
}
