import java.util.ArrayList;

public class Combinacion {
    private float valor;
    private ArrayList<Integer> centros;

    public Combinacion(float valor,ArrayList<Integer> centros){
        valor = valor;
        centros = centros;
    }

    public float getValor() {
        return valor;
    }

    public ArrayList<Integer> getCentros() {
        return centros;
    }
}
