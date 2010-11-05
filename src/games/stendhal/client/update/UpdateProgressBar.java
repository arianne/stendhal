package games.stendhal.client.update;

import java.net.URL;
import java.security.AccessControlException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * A progress bar for the download progress.
 */
public class UpdateProgressBar extends JFrame implements
		HttpClient.ProgressListener {
	private static final long serialVersionUID = -1607102841664745919L;

	private int max = 100;

	private int sizeOfLastFiles;

	private JPanel contentPane;

	private JProgressBar progressBar;

	/**
	 * Creates update progress bar.
	 * 
	 * @param max
	 *            max file size
	 */
	public UpdateProgressBar(final int max) {
		super("Downloading...");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new UpdateProgressBarWindowListener());

		this.max = max;
		try {
			final URL url = this.getClass().getClassLoader().getResource(
					ClientGameConfiguration.get("GAME_ICON"));
			setIconImage(new ImageIcon(url).getImage());
		} catch (final Exception e) {
			// in case that resource is not available
		}

		initializeComponents();

		this.pack();
		try {
			this.setAlwaysOnTop(true);
		} catch (final AccessControlException e) {
			// ignore it
		}
	}

	private void initializeComponents() {
		contentPane = (JPanel) this.getContentPane();

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		contentPane.add(new JLabel("Downloading..."));
		contentPane.add(Box.createVerticalStrut(5));

		progressBar = new JProgressBar(0, max);
		progressBar.setStringPainted(false);
		progressBar.setValue(0);
		contentPane.add(progressBar);
	}

	public void onDownloading(final int downloadedBytes) {
		progressBar.setValue(sizeOfLastFiles + downloadedBytes);
	}

	public void onDownloadCompleted(final int byteCounter) {
		sizeOfLastFiles = sizeOfLastFiles + byteCounter;
		progressBar.setValue(sizeOfLastFiles);
	}

}
