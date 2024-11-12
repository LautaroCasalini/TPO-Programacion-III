package TDA;

public interface ConjuntoTDA {
    void InicializarConjunto();
    void Agregar(int x);
    int Elegir();
    boolean ConjuntoVacio();
    void Sacar(int x);
    boolean Pertenece(int x);
    boolean TodosPertenecen(ConjuntoTDA origen);
    void MostrarConjunto();
}
