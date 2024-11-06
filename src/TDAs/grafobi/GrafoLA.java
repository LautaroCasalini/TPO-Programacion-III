package TDAs.grafobi;
import TDAs.conjuntos.*;

public class GrafoLA implements GrafoTDA {
	//Nodo y Vertice son lo mismo. Pero en el código se entiende al vertice como un objeto y al Nodo como el número del Vertice
	class NodoVertice{
		int nodo;
		NodoArista arista;
		NodoVertice sigNodo;
	}
	class NodoArista{
		int peso;
		NodoVertice nodoDestino;
		NodoArista sigArista;
	}

	NodoVertice origen;
	
	public void InicializarGrafo() {
		origen = null;
	}

	public void AgregarVertice(int x) {
		NodoVertice nuevo = new NodoVertice();	// El nuevo nodo se coloca al inicio
		nuevo.nodo = x;
		nuevo.arista = null;					// El nodo se agrega sin aristas inicialmente
		nuevo.sigNodo = origen;
		origen = nuevo;
	}
	

	public void EliminarVertice(int x) {
		if (origen.nodo == x)					// El primer v�rtice es el que debe eliminarse
			origen = origen.sigNodo;
		NodoVertice aux = origen;				// Se define un nodo viajero
		while (aux != null) {					// El nodo "aux" recorre todos los v�rtices
			this.EliminarAristaEnNodo(aux, x);
			if (aux.sigNodo != null && aux.sigNodo.nodo == x) {
				aux.sigNodo = aux.sigNodo.sigNodo;
			}
			aux = aux.sigNodo;
		}
	}

	private void EliminarAristaEnNodo(NodoVertice nodo, int v) { //El nodo ya est� encontrado
		NodoArista aux = nodo.arista;
		if (aux != null) {
			if (aux.nodoDestino.nodo == v) {		// La arista que debe eliminarse es la primera
				nodo.arista = aux.sigArista;
			} else {								// La arista que debe eliminarse es otra
				while(aux.sigArista != null && aux.sigArista.nodoDestino.nodo != v)
					aux = aux.sigArista;
				if (aux.sigArista != null)			// Se encontr� la arista
					aux.sigArista = aux.sigArista.sigArista;
			}
		}
	}
	
	private NodoVertice Vertice2Nodo(int x) {	// Devuelve el nodo del v�rtice o null 
		NodoVertice aux = origen;
		while(aux !=null && aux.nodo != x)
			aux = aux.sigNodo;
		return aux;
	}

	public void AgregarArista(int x, int y, int w) {
		NodoVertice n1 = Vertice2Nodo(x);
		NodoVertice n2 = Vertice2Nodo(y);

		if (n1 == null || n2 == null) return; // Si alguno de los nodos no existe, salir

		NodoArista nuevaArista1 = new NodoArista();
		nuevaArista1.peso = w;
		nuevaArista1.nodoDestino = n2;
		nuevaArista1.sigArista = n1.arista;
		n1.arista = nuevaArista1;

		NodoArista nuevaArista2 = new NodoArista();
		nuevaArista2.peso = w;
		nuevaArista2.nodoDestino = n1;
		nuevaArista2.sigArista = n2.arista;
		n2.arista = nuevaArista2;
	}



	public void EliminarArista(int x, int y) {
		NodoVertice n1 = Vertice2Nodo(x);
		EliminarAristaEnNodo(n1,y);
		NodoVertice n2 = Vertice2Nodo(y);
		EliminarAristaEnNodo(n2,x);

	}

	public int PesoArista(int x, int y) {
		NodoVertice nodo = Vertice2Nodo(x);
		NodoArista aux = nodo.arista;
		while (aux.nodoDestino.nodo != y)
			aux = aux.sigArista;
		return aux.peso;
	}

	public boolean ExisteArista(int x, int y) {
		NodoVertice nodo = Vertice2Nodo(x);
		NodoArista aux = nodo.arista;
		while (aux != null && aux.nodoDestino.nodo != y)
			aux = aux.sigArista;
		return (aux != null);
	}

	public ConjuntoTDA Vertices() {
		ConjuntoTDA CNodos = new ConjuntoA();
		CNodos.InicializarConjunto();
		NodoVertice aux = origen;
		while (aux != null) {
			CNodos.Agregar(aux.nodo);
			aux = aux.sigNodo;
		}
		return CNodos;
	}

}
