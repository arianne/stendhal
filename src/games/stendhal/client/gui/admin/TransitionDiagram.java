package games.stendhal.client.gui.admin;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.common.NotificationType;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

/**
 * Displays the state-transition chart of an NPC.
 * 
 * @author timothyb89
 */
public class TransitionDiagram {

	private static final Logger logger = Logger.getLogger(TransitionDiagram.class);

	public void showTransitionDiagram(String data) {
		showTransitionDiagram(data, null);
	}

	public void showTransitionDiagram(String data, Frame parent) {
		try {
			File dat = File.createTempFile("stendhal-graph-data", ".txt");
			File image = File.createTempFile("stendhal-graph", ".png");

			// print the data
			PrintStream ps = new PrintStream(new FileOutputStream(dat));
			ps.print(data);
			ps.close();

			// execute
			String dotPath = StendhalClient.get().getCache().getConfiguration().get(
					"stendhal.dotPath");
			if (dotPath == null) {
				dotPath = "dot";
			}

			// execute
			Process p = Runtime.getRuntime().exec(
					dotPath + " -Tpng -o " + image.getAbsolutePath() + " "
							+ dat.getAbsolutePath());
			p.waitFor();

			// open up the image
			JDialog jd = new JDialog(parent, "NPC Transition Viewer", false);

			Image img = Toolkit.getDefaultToolkit().createImage(
					image.toURI().toURL());
			System.out.println("Checking image with size " + img.getWidth(jd)
					+ "x" + img.getHeight(jd));
			ImageIcon icon = new ImageIcon(img);

			JLabel label = new JLabel(scale(icon));
			jd.add(label);

			jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jd.pack();
			jd.setVisible(true);

			image.deleteOnExit();
			dat.deleteOnExit();
		} catch (Exception e) {
			logger.error("Failed creating graph: ", e);
			StendhalUI.get().addEventLine(
					"Failed creating graph (Is graphviz installed and on your system search path?): "
							+ e.getMessage(), NotificationType.ERROR);
		}
	}

	/**
	 * Testcode.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		TransitionDiagram td = new TransitionDiagram();
		td.showTransitionDiagram("digraph finite_state_machine {\n"
				+ "rankdir=LR\n" + "IDLE -> ATTENDING [ label = \"hi\" ];\n"
				+ "IDLE -> ATTENDING [ label = \"hello\" ];\n"
				+ "IDLE -> ATTENDING [ label = \"greetings\" ];\n"
				+ "IDLE -> ATTENDING [ label = \"hola\" ];\n"
				+ "ATTENDING -> ATTENDING [ label = \"job\" ];\n"
				+ "HEAL_OFFERED -> ATTENDING [ label = \"yes *\" ];\n"
				+ "HEAL_OFFERED -> ATTENDING [ label = \"ok *\" ];\n"
				+ "HEAL_OFFERED -> ATTENDING [ label = \"no\" ];\n"
				+ "ANY -> IDLE [ label = \"bye *\" ];\n"
				+ "ANY -> IDLE [ label = \"farewell *\" ];\n"
				+ "ANY -> IDLE [ label = \"cya *\" ];\n"
				+ "ANY -> IDLE [ label = \"adios *\" ];\n" + "}");
	}

	private ImageIcon scale(ImageIcon image) {
		Image img = image.getImage();
		Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
		int ow = img.getWidth(null);
		int oh = img.getHeight(null);
		int w = ssize.width - 20; // screens are usually wide..
		int h = ssize.height - 100;

		if (ow >= w || oh >= h) {
			return new ImageIcon(img.getScaledInstance(w, h,
					Image.SCALE_AREA_AVERAGING));
		} else {
			return image;
		}
	}

}
