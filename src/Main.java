import TDAs.conjuntos.ConjuntoA;
import TDAs.conjuntos.ConjuntoTDA;
import TDAs.grafobi.GrafoLA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.*;

public class Main {
    //TODO: Aplicar Programación Dinámica
    public static Combinacion ProgramacionDinamica(ArrayList<Integer> centrosInvolucrados, Map<Integer, GrafoLA> diccionarioGrafos, Map<Integer, int[]> diccionarioCentros, Map<Integer, Integer> diccionarioClientes){
        Combinacion comb = new Combinacion(0,centrosInvolucrados);
        return comb;
    }


    public static Combinacion BackTracking(ArrayList<Integer> centros, Map<Integer,GrafoLA> grafos,Integer index, Map<Integer, int[]> infoCentros, Map<Integer, Integer> infoClientes, ArrayList<Integer> centrosIncluidos){
        //Caso base
        if(index == centros.size() - 1){
            Combinacion comb = ProgramacionDinamica(centrosIncluidos,grafos,infoCentros,infoClientes);

            System.out.println("Mostrando los elementos que quedaron incluidos: ");
            for (Integer i: centrosIncluidos){
                System.out.print(i + " ");
            }
            System.out.println();
            System.out.println("----------------");
            return comb;
        }
        //Llamamos a la función aumentando el indice, en la primera llamada no incluimos el elemento pero en la segunda si
        Combinacion combinacion1 = BackTracking(centros,grafos,index+1,infoCentros,infoClientes,centrosIncluidos);

        ArrayList<Integer> centrosIncluidosCopy = new ArrayList<>(centrosIncluidos);
        centrosIncluidosCopy.add(centros.get(index));
        Combinacion combinacion2 = BackTracking(centros,grafos,index+1,infoCentros,infoClientes,centrosIncluidosCopy);

        if (combinacion1.getValor() < combinacion2.getValor()){
            return combinacion1;
        } else {
            return combinacion2;
        }

    }

    /*Este dijkstra se encargará de devolver un diccionario cuya clave es el nodo origen y como valor es un
    grafo que contiene los costos de alcanzar todos los nodos restantes.
     */
    public static GrafoLA Dijkstra(GrafoLA g, int origen, Map<Integer, int[]> dictCentros) {
        int vertice, aux_vertice, mejor_vertice, mejor_distancia;

        GrafoLA distanciasMinimas = new GrafoLA();
        distanciasMinimas.InicializarGrafo();
        distanciasMinimas.AgregarVertice(origen);

        ConjuntoTDA vertices = g.Vertices();
        vertices.Sacar(origen);

        // Agregar vértices que no están en dictCentros al grafo de distancias mínimas
        while (!vertices.ConjuntoVacio()) {
            vertice = vertices.Elegir();
            vertices.Sacar(vertice);

            // Verificar si el vértice no es un centro de distribución
            if (!dictCentros.containsKey(vertice)) {
                distanciasMinimas.AgregarVertice(vertice);
                if (g.ExisteArista(origen, vertice)) {
                    distanciasMinimas.AgregarArista(origen, vertice, g.PesoArista(origen, vertice));
                }
            }
        }

        ConjuntoTDA pendientes = g.Vertices();
        pendientes.Sacar(origen);
        ConjuntoA aux_pendientes = new ConjuntoA();
        aux_pendientes.InicializarConjunto();

        // Aplicar Dijkstra sin considerar los centros en dictCentros
        while (!pendientes.ConjuntoVacio()) {
            mejor_distancia = 0;
            mejor_vertice = 0;

            // Encontrar el vértice pendiente con la menor distancia
            while (!pendientes.ConjuntoVacio()) {
                aux_vertice = pendientes.Elegir();
                pendientes.Sacar(aux_vertice);
                aux_pendientes.Agregar(aux_vertice);

                // Saltar los vértices que son centros de distribución
                if (dictCentros.containsKey(aux_vertice)) {
                    continue;
                }

                if (distanciasMinimas.ExisteArista(origen, aux_vertice) &&
                        (mejor_distancia == 0 || mejor_distancia > distanciasMinimas.PesoArista(origen, aux_vertice))) {
                    mejor_distancia = distanciasMinimas.PesoArista(origen, aux_vertice);
                    mejor_vertice = aux_vertice;
                }
            }

            vertice = mejor_vertice;
            if (vertice != 0) {
                aux_pendientes.Sacar(vertice);

                // Procesar las conexiones del vértice seleccionado
                while (!aux_pendientes.ConjuntoVacio()) {
                    aux_vertice = aux_pendientes.Elegir();
                    aux_pendientes.Sacar(aux_vertice);
                    pendientes.Agregar(aux_vertice);

                    // Saltar si el vértice destino es un centro
                    if (dictCentros.containsKey(aux_vertice)) {
                        continue;
                    }

                    if (g.ExisteArista(vertice, aux_vertice)) {
                        int nuevaDistancia = distanciasMinimas.PesoArista(origen, vertice) + g.PesoArista(vertice, aux_vertice);
                        if (!distanciasMinimas.ExisteArista(origen, aux_vertice)) {
                            distanciasMinimas.AgregarArista(origen, aux_vertice, nuevaDistancia);
                        } else if (distanciasMinimas.PesoArista(origen, aux_vertice) > nuevaDistancia) {
                            distanciasMinimas.AgregarArista(origen, aux_vertice, nuevaDistancia);
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
        Map<Integer, GrafoLA> dictGrafosC = new HashMap<>();

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
                    }
                }
            } catch (Exception e){
                System.out.println("Error " + e);
            }

            for(Integer key: dictCentros.keySet()){
                GrafoLA g = Dijkstra(grafo, key, dictCentros);
                dictGrafosC.put(key, g);
            }

            //Lista con los centros ordenados de mayor a menor
            List<Integer> keys = new ArrayList<>(dictCentros.keySet());
            keys.sort(Collections.reverseOrder());

            ArrayList<Integer> test = new ArrayList<>();
            test.add(1);
            test.add(2);
            test.add(3);
            ArrayList<Integer> centrosIncluidos = new ArrayList<>();
            BackTracking(test,dictGrafosC,0,dictCentros,dictClientes,centrosIncluidos);



        }
}