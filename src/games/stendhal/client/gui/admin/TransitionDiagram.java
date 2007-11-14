package games.stendhal.client.gui.admin;

public class TransitionDiagram {

	public void showTransitionDiagram(String data) {
		// TODO
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
