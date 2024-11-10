package TDAs.conjuntos;


public class ConjuntoA implements ConjuntoTDA{
	private int[] a;
	private int cant;

	public void InicializarConjunto() {
		a = new int[100];
		cant = 0;
	}

	public void Agregar(int x) {
		if (!this.Pertenece(x)) {
			a[cant]=x;
			cant++;
		}
	}

	public boolean ConjuntoVacio() {
		return(cant==0);
	}

	public int Elegir() {
		return a[cant - 1];
	}

	public boolean Pertenece(int x) {
		int i = 0;
		while (i< cant && a[i]!=x) {
			i++;
		}
		return (i<cant);
	}

	public void Sacar(int x) {
		int i = 0;
		while (i<cant && a[i]!=x) {
			i++;
		}
		if (i<cant) {
			a[i]=a[cant-1];
			cant--;
		}
	}

	public void MostrarConjunto() {
		ConjuntoTDA aux = new ConjuntoA();
		aux.InicializarConjunto();
		int g;
		System.out.print("{ ");
		while(!this.ConjuntoVacio()) {
			g=this.Elegir();
			this.Sacar(g);
			aux.Agregar(g);
			System.out.print(g+" ");
		}
		System.out.print("}");
		while(!aux.ConjuntoVacio()) {
			g=aux.Elegir();
			aux.Sacar(g);
			this.Agregar(g);
		}

	}

	public boolean TodosPertenecen(ConjuntoTDA origen) {
		boolean todospert = true;
		int g;
		ConjuntoTDA aux = new ConjuntoA();
		aux.InicializarConjunto();
		while(todospert == true && !origen.ConjuntoVacio()) {
			g=origen.Elegir();
			origen.Sacar(g);
			aux.Agregar(g);
			if (!this.Pertenece(g)) {
				todospert = false;
			}
		}
		while(!aux.ConjuntoVacio()) {
			int h=aux.Elegir();
			aux.Sacar(h);
			origen.Agregar(h);

		}
		return(todospert);

	}

}