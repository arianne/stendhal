package games.stendhal.client.webstart;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

public class Persistence {
	PersistenceService ps;
	BasicService bs;
	
	public Persistence() {
		try {
			ps = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
			bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
		} catch (UnavailableServiceException e) {
			ps = null;
			bs = null;
		}


		if (ps != null && bs != null) {

			try {
				// find all the muffins for our URL
				URL codebase = bs.getCodeBase();
				String[] muffins = ps.getNames(codebase);

				// get the attributes (tags) for each of these muffins.
				// update the server's copy of the data if any muffins
				// are dirty
				int[] tags = new int[muffins.length];
				URL[] muffinURLs = new URL[muffins.length];
				for (int i = 0; i < muffins.length; i++) {
					muffinURLs[i] = new URL(codebase.toString() + muffins[i]);
					tags[i] = ps.getTag(muffinURLs[i]);
					// update the server if anything is tagged DIRTY
					if (tags[i] == PersistenceService.DIRTY) {
						doUpdateServer(muffinURLs[i]);
					}
				}


				// read in the contents of a muffin and then delete it
				FileContents fc = ps.get(muffinURLs[0]);
				int maxsize = (int) fc.getMaxLength();
				byte[] buf = new byte[(int) fc.getLength()];
				InputStream is = fc.getInputStream();
				int pos = 0;
				while ((pos = is.read(buf, pos, buf.length - pos)) > 0) {
					// just loop
				}
				is.close();

				ps.delete(muffinURLs[0]);

				// re-create the muffin and repopulate its data
				ps.create(muffinURLs[0], maxsize);
				fc = ps.get(muffinURLs[0]);
				// don't append
				OutputStream os = fc.getOutputStream(false);
				os.write(buf);
				os.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void doUpdateServer(URL url) {
		// update the server's copy of the persistent data
		// represented by the given URL
		// ...
		// ps.setTag(url, PersistenceService.CACHED);
	}
}
