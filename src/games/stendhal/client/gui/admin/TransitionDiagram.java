package games.stendhal.client.gui.admin;

import games.stendhal.client.update.ClientGameConfiguration;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import org.apache.log4j.Logger;

public class TransitionDiagram {

        private static final Logger logger = Logger.getLogger(TransitionDiagram.class);
    
        public void showTransitionDiagram(String data) {
            showTransitionDiagram(data, null);
        }
        
	public void showTransitionDiagram(String data, Frame parent) {
		try {
                        File dat = File.createTempFile("stendhal-graph-data", ".txt");
                        File image = File.createTempFile("stendhal-graph", ".png");

                        //print the data
                        PrintStream ps = new PrintStream(new FileOutputStream(dat));
                        ps.print(data);
                        ps.close();
                        
                        //execute
                        String dotPath = ClientGameConfiguration.get("stendhal.dotPath"); //enable again when done testing
                        if (dotPath == null) {
                            dotPath = "dot";//PATH?
                        }
                        
                        //execute
                        Process p = Runtime.getRuntime().exec(dotPath + " -Tpng -o " + image.getAbsolutePath() + " " + dat.getAbsolutePath());
                        
                        //print output
                        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println("dot output: " + line);
                        }
                        
                        //open up the image
                        JDialog jd = new JDialog(parent, "NPC Transition Viewer", true);
                        jd.add(new JLabel(new ImageIcon(image.toURI().toURL())));
                        jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        jd.pack();
                        jd.setVisible(true);
                } catch (Exception e) {
                        //logger.error("Failed creating graph!", e);
                        e.printStackTrace();
                }
	}
	
	public static void main(String[] args) {
		TransitionDiagram td = new TransitionDiagram();
		td.showTransitionDiagram(
			"digraph finite_state_machine {\n"+
			"rankdir=LR\n"+
			"IDLE -> ATTENDING [ label = \"hi\" ];\n"+
			"IDLE -> ATTENDING [ label = \"hello\" ];\n"+
			"IDLE -> ATTENDING [ label = \"greetings\" ];\n"+
			"IDLE -> ATTENDING [ label = \"hola\" ];\n"+
			"ATTENDING -> ATTENDING [ label = \"job\" ];\n"+
			"HEAL_OFFERED -> ATTENDING [ label = \"yes *\" ];\n"+
			"HEAL_OFFERED -> ATTENDING [ label = \"ok *\" ];\n"+
			"HEAL_OFFERED -> ATTENDING [ label = \"no\" ];\n"+
			"ANY -> IDLE [ label = \"bye *\" ];\n"+
			"ANY -> IDLE [ label = \"farewell *\" ];\n"+
			"ANY -> IDLE [ label = \"cya *\" ];\n"+
			"ANY -> IDLE [ label = \"adios *\" ];\n"+
			"}");

	}
}
