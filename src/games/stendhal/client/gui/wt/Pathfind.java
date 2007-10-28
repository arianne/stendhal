package games.stendhal.client.gui.wt;

import games.stendhal.common.CollisionDetection;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;


/**
 * A* implementation. 
 * TODO: OPTIMIZATION AND CLEANING!!!! and comment the code.. :p 
 * i hope durkham dont look here, i dont want hurt her with my
 * crappy code XD
 *
 * @author Kawn
 */


public class Pathfind {

	private HashMap<Integer, Node> nodeRegistry = new HashMap<Integer, Node>();
	private HashMap<Integer, Node> nodeRegistryclose = new HashMap<Integer, Node>();

	private PriorityQueue<Node> lista_abierta = new PriorityQueue<Node>(16,
			new Comparator<Node>() {

		public int compare(Node o1, Node o2) {
			return (int) Math.signum(o1.F - o2.F);
		}
	});

	static Rectangle search_area;
	static List<Node> lista_cerrada = new ArrayList<Node>();
	static LinkedList<Node> final_path 	 = new LinkedList<Node>();
	Node nodo_actual;
	int final_path_index = 0;

	private static int colision(CollisionDetection collisiondetection,int x1, int y1){
		/*
		if (x1 < 0) return 1;
		if (y1 < 0) return 1;

		if (x1 >= collisiondetection.getWidth()) return 1;
		if (y1 >= collisiondetection.getHeight()) return 1;

		 */

		if (x1 < search_area.getMinX()) return 1;
		if (y1 < search_area.getMinY()) return 1;

		if (x1 >= search_area.getMaxX()) return 1;
		if (y1 >= search_area.getMaxY()) return 1;

		if (!collisiondetection.walkable(x1, y1)) return 1;

		return 0;
	}
	public void PathNextNode(){

		if (final_path_index != 0) {
			final_path_index--;
			nodo_actual = final_path.get(final_path_index);
		}
	}

	public void PathJumpNode(){
		final_path_index = final_path_index - 20;

		if (final_path_index < 0) {
			final_path_index = 0;
		}

		nodo_actual = final_path.get(final_path_index);
	}

	public void PathJumpToNode(int destnode){
		final_path_index = destnode;

		if (final_path_index < 0) {
			final_path_index = 0;
		}
		nodo_actual = final_path.get(destnode);
	}

	public int NodeGetX(){
		return nodo_actual.x;
	}

	public int NodeGetY(){
		return nodo_actual.y;
	}

	public boolean ReachedGoal(){
		return final_path_index==0;
	}

	public void Reinice(){
		if (final_path!=null)
			final_path_index=final_path.size();
	}

	public void ClearPath(){
		lista_abierta.clear();
		lista_cerrada.clear();
		final_path.clear();
		final_path_index=0;
		nodeRegistry.clear();
		nodeRegistryclose.clear();

	}
	public boolean NewPath (CollisionDetection collisiondetection,int x_inicial, int y_inicial, int x_final, int y_final, Rectangle search_area2){


	//	System.out.println("PATHFIND: " + x_inicial + " " + y_inicial + " " +x_final + " "+ y_final + " " +collisiondetection.toString());
		

		search_area =  search_area2;

		//System.out.println("AREA: " + search_area.getMinX() + " " +  search_area.getMinY() + " " +search_area.getMaxX()  + " " + search_area.getMaxY());
		/*if (colision( collisiondetection,x_final,y_final)!=0){
			System.out.println("DESTINO NO CAMINABLE: " + x_inicial + " " + y_inicial + " " +x_final + " "+ y_final + " " );
			return false;

		}*/
		//	long computation_time = System.currentTimeMillis();

		Node nodo_ini = new Node(x_inicial,y_inicial,x_inicial,y_inicial);

		ClearPath();

		//1) Anyade el cuadro inicial a la lista abierta.
		nodo_ini.padre=new Node();

		lista_abierta.offer(nodo_ini);
		nodeRegistry.put(nodo_ini.x + nodo_ini.y * collisiondetection.getWidth(), nodo_ini);

		do{
			// NO HAY CAMINO
			if (lista_abierta.size()==0) {
				//	computation_time = System.currentTimeMillis() - computation_time;
			//	System.out.println("NO HAY CAMINO!! "+ lista_cerrada.size());// elapsed time: " + computation_time);
				return false;
			}

			/* a) Busca el cuadro con el coste F mas bajo en la lista abierta. 
			 * Nos referimos a este como el cuadro actual. 
			 */

			//b) Cambialo a la lista cerrada

			Node nodo_Fm = lista_abierta.poll();
			lista_cerrada.add(nodo_Fm);
			nodeRegistryclose.put(nodo_Fm.x + nodo_Fm.y * collisiondetection.getWidth(), nodo_Fm);

			//	System.out.println("Elementos:"+lista_abierta.size()+":"+lista_cerrada.size());

			// Se ha llegado al final
			if ((nodo_Fm.x==x_final)&&(nodo_Fm.y==y_final)) break;


			//     c) Para cada uno de los 8 cuadros adyacentes al cuadro actual ...
			int x_tmp;
			int y_tmp;

			for (y_tmp = nodo_Fm.y-1; y_tmp <= nodo_Fm.y+1; y_tmp++){
				for (x_tmp = nodo_Fm.x-1; x_tmp <= nodo_Fm.x+1; x_tmp++){

					if (y_tmp==nodo_Fm.y)
						if (x_tmp==nodo_Fm.x)
							continue;
					if ((y_tmp!=nodo_Fm.y)&&(x_tmp!=nodo_Fm.x))	continue;

//					//  Si no es transitable o si esta en la lista cerrada, ignoralo. 
//					En cualquier otro caso haz lo siguiente.

					if(nodeRegistryclose.get(x_tmp + y_tmp * collisiondetection.getWidth())!=null) continue;


					if (colision( collisiondetection,x_tmp,y_tmp)==0){
						int manhatan=10*(Math.abs(x_tmp - x_final) + Math.abs(y_tmp - y_final));

						Node  nodo_UP;
						if (Math.abs(x_tmp-nodo_Fm.x) == 1 && Math.abs(y_tmp-nodo_Fm.y) == 1)
							nodo_UP = new Node(x_tmp,y_tmp,nodo_Fm.G+14, manhatan);
						else	{
							int patata = 0;

							// --- Bonus si no cambia de direccion
							int incx= (nodo_Fm.padre.x -nodo_Fm.x);
							int incy= (nodo_Fm.padre.y -nodo_Fm.y);

							int incx2= (nodo_Fm.x - x_tmp );
							int incy2= (nodo_Fm.y - y_tmp );


							if ((incx==incx2)&&(incy==incy2)){ 
								patata = 1;
								//	System.out.println("HOPLA: " +incx +" "+ incy);
							}

							nodo_UP = new Node(x_tmp,y_tmp,nodo_Fm.G+10-patata, manhatan);

						}
						nodo_UP.padre=nodo_Fm;
						//	System.out.println("ADYACENTE:"+x_tmp+":"+y_tmp + " G " +nodo_UP.G + " H " + nodo_UP.H + " H " + nodo_UP.F);

						Node temp = nodeRegistry.get(nodo_UP.x + nodo_UP.y * collisiondetection.getWidth());

						if(temp!=null){
//							//Si ya esta en la lista abierta, comprueba si el camino para ese 
//							es mejor usando el coste G como baremo. Un coste G menor significa 
//							que este es un mejor camino. Si es asi, cambia el padre del cuadrado 
//							al cuadro actual y recalcula G y F del cuadro. Si estas manteniendo la 
//							lista abierta por orden de puntuacion F, podrias necesitar reordenar la 
//							lista para llevar cuenta del cambio.
							if (nodo_UP.G < temp.G){
								temp.G = nodo_UP.G;
								temp.F = nodo_UP.F;
								temp.H = nodo_UP.H;
								temp.padre = nodo_UP.padre;
							}

						}else{
//							//Si no esta en la lista abierta, anyadelo a la lista abierta. 
//							Haz que el cuadro actual sea el padre de este cuadro. Almacena los 
//							costes F, G y H del cuadro.
							//lista_abierta.add(nodo_UP);
							lista_abierta.offer(nodo_UP);
							nodeRegistry.put(nodo_UP.x + nodo_UP.y * collisiondetection.getWidth(), nodo_UP);
						}
					}
				}
			}
		}while(true);

		//System.out.println("-----------------");
		ListIterator<Node> i=lista_cerrada.listIterator();
		Node temp;
		while(i.hasNext())
		{
			temp=(Node)i.next();
			//		System.out.println("E:"+temp.x+":"+temp.y + " G " +temp.G + " H " + temp.H + " "+ temp.padre.x +":" + temp.padre.y);
		}

		int petiX=x_final;
		int petiY=y_final;

		final_path.clear();

		for (int j=lista_cerrada.size()-1; j>=0;j--){

			temp = (Node) lista_cerrada.get(j);
			if ((petiX==temp.x)&&(petiY==temp.y)){

				//	System.out.println("S:"+temp.x+":"+temp.y + " G " +temp.G + " H " + temp.H + " "+ temp.padre.x +":" + temp.padre.y);
				petiX = temp.padre.x;
				petiY = temp.padre.y;
				final_path.addLast(temp);
			}
		}

		final_path_index = final_path.size();


		//	computation_time = System.currentTimeMillis() - computation_time;

	//	System.out.println("CAMINO ENCONTRADO!! " + lista_cerrada.size());//elapsed time: " + computation_time);

		return ((final_path.size() > 0) ? true:false);
	}
	public static void main(String[] args) {
		/*
		Pathfind Path = new Pathfind();
		Path.NewPath(1,2,5,2);
		while (!Path.ReachedGoal()){
			Path.PathNextNode();
			System.out.println("PEPITO:"+Path.NodeGetX()+":"+Path.NodeGetY());

		}*/
	}

	private class Node{
		int x;
		int y;
		int G;
		int H;
		int F;
		Node padre;
		public Node(int x, int y, int g, int h) {
			super();
			this.x = x;
			this.y = y;
			G = g;
			H = h;
			F= G+H;
		}
		public Node() {
			x=0;
			y=0;
			G=0;
			H=0;
			F=0;
		}

	}
}

