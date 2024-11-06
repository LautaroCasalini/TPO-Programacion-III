import TDAs.conjuntos.ConjuntoA;
import TDAs.conjuntos.ConjuntoTDA;
import TDAs.grafobi.GrafoLA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Main {
    /*Este dijkstra se encargará de devolver un diccionario cuya clave es el nodo origen y como valor es un
    grafo que contiene los costos de alcanzar todos los nodos restantes.
     */
    public static GrafoLA Dijkstra( GrafoLA g , int origen ) {
        int vertice , aux_vertice , mejor_vertice , mejor_distancia ;

        GrafoLA distanciasMinimas = new GrafoLA() ;
        distanciasMinimas.InicializarGrafo();
        distanciasMinimas.AgregarVertice(origen);
        ConjuntoTDA vertices = g.Vertices();
        vertices.Sacar(origen);
        while (! vertices.ConjuntoVacio()) {
            vertice = vertices.Elegir();
            vertices.Sacar(vertice);
            distanciasMinimas.AgregarVertice(vertice);
            if (g.ExisteArista(origen, vertice)) {
                distanciasMinimas.AgregarArista( origen , vertice, g.PesoArista( origen , vertice ));
            }
        }
        ConjuntoTDA pendientes = g.Vertices();
        pendientes.Sacar(origen);
        ConjuntoA aux_pendientes = new ConjuntoA();
        aux_pendientes.InicializarConjunto();
        while (!pendientes.ConjuntoVacio()) {
            mejor_distancia = 0;
            mejor_vertice = 0;
            while (!pendientes.ConjuntoVacio()) {
                aux_vertice = pendientes.Elegir();
                pendientes.Sacar(aux_vertice);
                aux_pendientes.Agregar(aux_vertice);
                if (( distanciasMinimas.ExisteArista(origen , aux_vertice) &&( mejor_distancia == 0 || (
                        mejor_distancia > distanciasMinimas.PesoArista (origen , aux_vertice ))))) {
                    mejor_distancia =distanciasMinimas.PesoArista( origen , aux_vertice ) ;
                    mejor_vertice = aux_vertice ;
                }
            }
            vertice = mejor_vertice ;
            if ( vertice != 0) {
                aux_pendientes.Sacar(vertice);
                while (! aux_pendientes.ConjuntoVacio()) {
                    aux_vertice = aux_pendientes.Elegir() ;
                    aux_pendientes.Sacar( aux_vertice );
                    pendientes.Agregar( aux_vertice );
                    if ( g.ExisteArista( vertice , aux_vertice ) ) {
                        if (! distanciasMinimas.ExisteArista( origen, aux_vertice ) )
                        {distanciasMinimas.AgregarArista( origen, aux_vertice , distanciasMinimas.PesoArista( origen , vertice ) + g.PesoArista( vertice , aux_vertice ));
                        } else {
                            if( distanciasMinimas.PesoArista( origen, aux_vertice ) > distanciasMinimas.PesoArista( origen , vertice ) + g.PesoArista( vertice , aux_vertice )) {
                                distanciasMinimas . AgregarArista (origen , aux_vertice , distanciasMinimas.PesoArista(origen , vertice ) + g.PesoArista(vertice , aux_vertice ));
                            }
                        }
                    }
                }
            }
        }
        return distanciasMinimas;
    }

        public static void main(String[] args) {
        GrafoLA grafo = new GrafoLA();
        grafo.InicializarGrafo();
        //El valor de cada clave de dictCentros es un arreglo que contiene [Coso unitario de enviar mercadería al puerto, Costo fijo del Centro]
        Map<Integer, int[]> dictCentros = new HashMap<>();
        //El valor de cada clave de dictClientes es el volumen de producción del cliente
        Map<Integer, Integer> dictClientes = new HashMap<>();

        //Leemos ambos archivos y los pasamos a un GrafoTDA
        FileReader archivo;
        BufferedReader lector;
        try {
            archivo = new FileReader("src/Archivos/clientesYCentros.txt");
            if (archivo.ready()) {
                lector = new BufferedReader(archivo);
                String linea;
                //Usamos un cont para diferenciar los clientes de los centros
                int cont = 0;
                while ((linea = lector.readLine()) != null) {
                    String[] entidadInfo = linea.split(",");
                    int clave = Integer.parseInt(entidadInfo[0]);
                    if (cont < 50) {
                        int valor = Integer.parseInt(entidadInfo[1]);
                        dictClientes.put(clave,valor);
                    } else {
                        int[] valores = new int[2];
                        valores[0] = Integer.parseInt(entidadInfo[1]);
                        valores[1] = Integer.parseInt(entidadInfo[2]);
                        dictCentros.put(clave,valores);
                    }
                    grafo.AgregarVertice(clave);
                    cont ++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
            try {
                archivo = new FileReader("src/Archivos/rutas.txt");
                if (archivo.ready()) {
                    lector = new BufferedReader(archivo);
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        String[] NodosInfo = linea.split(",");
                        int nodoOrigen = Integer.parseInt(NodosInfo[0]);
                        int nodoDestino = Integer.parseInt(NodosInfo[1]);
                        int costo = Integer.parseInt(NodosInfo[2]);

                        grafo.AgregarArista(nodoOrigen, nodoDestino, costo); // Agregar la arista
                        System.out.println("Arista agregada: " + nodoOrigen + " -> " + nodoDestino + " (Costo: " + costo + ")");
                    }
                }
            } catch (Exception e){
                System.out.println("Error " + e);
            }


            ConjuntoTDA vertices = grafo.Vertices();
            while (!vertices.ConjuntoVacio()) {
                int v = vertices.Elegir();
                vertices.Sacar(v);

                ConjuntoTDA vecinos = grafo.Vertices();
                while (!vecinos.ConjuntoVacio()) {
                    int u = vecinos.Elegir();
                    vecinos.Sacar(u);
                    if (grafo.ExisteArista(v, u)) {
                        System.out.println("Arista existe: " + u + " -> " + v + " con peso " + grafo.PesoArista(u, v));
                    }
                }
                System.out.println(grafo.PesoArista(50,3));
            }




        }
}