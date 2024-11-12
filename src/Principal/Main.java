package Principal;

import TDA.*;
import Implementacion.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
    public static int costoMinimo = Integer.MAX_VALUE;
    public static List<Integer> combinacionOptima = new ArrayList<>();
    private static Map<String, int[]> memoCentroMasCercano = new HashMap<>();


    public static void BackTracking(Map<Integer, GrafoLA> grafos, List<Integer> centrosKeys, int index,
                                    Map<Integer, int[]> infoCentros, Map<Integer, Integer> infoClientes,
                                    ArrayList<Integer> centrosIncluidos, int costoFijoAcumulado) {
        // Caso Base
        if (index == centrosKeys.size()) {
            // Verificar que haya al menos un centro activo en la combinación actual
            if (centrosIncluidos.isEmpty()) {
                return; // Ignorar esta combinación ya que no hay centros activos
            }
            int costoTotal = calcularCostoTotal(centrosIncluidos, grafos, infoCentros, infoClientes);
            // Actualizar si se encuentra un costo menor
            if (costoTotal < costoMinimo) {
                costoMinimo = costoTotal;
                combinacionOptima.clear();
                combinacionOptima.addAll(centrosIncluidos);
            }
            return;
        }

        int centro = centrosKeys.get(index);
        int costoFijoCentro = infoCentros.get(centro)[1];
        int nuevoCostoFijoAcumulado = costoFijoAcumulado + costoFijoCentro;

        // Podar esta rama si el costo fijo acumulado ya supera el costo mínimo actual
        if (nuevoCostoFijoAcumulado > costoMinimo) {
            return;
        }

        // Incluir el centro y continuar con la siguiente combinación
        centrosIncluidos.add(centro);
        BackTracking(grafos, centrosKeys, index + 1, infoCentros, infoClientes, centrosIncluidos, nuevoCostoFijoAcumulado);

        // Excluir el centro y probar la combinación sin él
        centrosIncluidos.remove(centrosIncluidos.size() - 1);
        BackTracking(grafos, centrosKeys, index + 1, infoCentros, infoClientes, centrosIncluidos, costoFijoAcumulado);
    }


    // Se utiliza esta función para calcular el costo total de la combinación de centros obtenida al momento.
    // Suma el costo fijo por centro + el costo por el transporte total de los nodos a los centros.
    public static int calcularCostoTotal(ArrayList<Integer> centrosActivos, Map<Integer, GrafoLA> dictGrafosC,
                                         Map<Integer, int[]> dictCentros, Map<Integer, Integer> dictClientes) {
        int costoFijoTotal = 0;
        for (int centro : centrosActivos) {
            int[] datosCentro = dictCentros.get(centro);
            if (datosCentro != null && datosCentro.length > 1) {
                costoFijoTotal += datosCentro[1];
            } else {
                System.out.println("Error: Centro " + centro + " no tiene datos completos en dictCentros.");
                return Integer.MAX_VALUE;
            }
        }
        int costoTransporteTotal = calcularCostoAsignacionClientes(centrosActivos, dictGrafosC, dictClientes, costoFijoTotal,dictCentros);
        return costoFijoTotal + costoTransporteTotal;
    }

    // Se utiliza esta función para calcular el costo total por el transporte.
    public static int calcularCostoAsignacionClientes(ArrayList<Integer> centrosActivos, Map<Integer, GrafoLA> dictGrafosC,
                                                      Map<Integer, Integer> dictClientes, int costoFijoTotal, Map<Integer, int[]> dictCentros) {
        int costoTransporteTotal = 0;
        for (int cliente : dictClientes.keySet()) {
            int[] resultado = encontrarCentroMasCercano(cliente, centrosActivos, dictGrafosC);
            int centroMasCercano = resultado[0];
            int distanciaMinima = resultado[1];
            int costoTrasladoPuerto = dictCentros.get(centroMasCercano)[0];
            // Calcular el costo de transporte total por cliente considerando centro y puerto
            int costoTransporte = (distanciaMinima + costoTrasladoPuerto) * dictClientes.get(cliente);
            costoTransporteTotal += costoTransporte;
            if(costoTransporteTotal+costoFijoTotal>costoMinimo){
                return costoTransporteTotal;
            } // Si el costo de transporte actual + costo fijo supera al costo mínimo actual se corta el proceso de
            // asignación de clientes a los centros activos ya que esta combinación no será la óptima

        }
        return costoTransporteTotal;
    }


    // Esta función se utiliza para encontrar el centro menos costoso desde el nodo. Se le envian los centros de la combinación
    // actual junto el el cliente de la iteración y los Dijkstra para obtener los caminos de ese nodo a los centros activos.
    // Se utiliza un hash para guardar las keys cliente-combinación. De existir, se devuelve el resultaod obtenido con anterioridad
    public static int[] encontrarCentroMasCercano(int cliente, ArrayList<Integer> centrosActivos, Map<Integer, GrafoLA> dictGrafosC) {
        String memoKey = cliente + "-" + centrosActivos.toString();
        if (memoCentroMasCercano.containsKey(memoKey)) {
            return memoCentroMasCercano.get(memoKey);
        }
        // Si no está en el memo, se calcula el centro más cercano
        int costoDistanciaMinima = Integer.MAX_VALUE;
        int centroMasCercano = -1;
        for (int centro : centrosActivos) {
            GrafoLA grafoCentro = dictGrafosC.get(centro);
            if (grafoCentro != null && grafoCentro.ExisteArista(centro, cliente)) {
                int costoDistancia = grafoCentro.costo(centro, cliente);
                if (costoDistancia < costoDistanciaMinima) {
                    costoDistanciaMinima = costoDistancia;
                    centroMasCercano = centro;
                }
            }
        }
        int[] resultado = new int[]{centroMasCercano, costoDistanciaMinima};
        memoCentroMasCercano.put(memoKey, resultado);

        return resultado;
    }


    public static GrafoLA Dijkstra(GrafoLA g, int origen, Map<Integer, int[]> dictCentros) {
        GrafoLA distanciasMinimas = new GrafoLA();
        distanciasMinimas.InicializarGrafo();
        distanciasMinimas.AgregarVertice(origen);

        Map<Integer, Integer> distancia = new HashMap<>();
        Map<Integer, Boolean> visitado = new HashMap<>();

        ConjuntoTDA vertices = g.Vertices();

        while (!vertices.ConjuntoVacio()) {
            int vertice = vertices.Elegir();
            vertices.Sacar(vertice);
            distancia.put(vertice, Integer.MAX_VALUE);
            visitado.put(vertice, false);
            distanciasMinimas.AgregarVertice(vertice);
        }

        distancia.put(origen, 0);

        for (int i = 0; i < distancia.size(); i++) {
            // Integrando obtenerVerticeMinDistancia aquí
            int minDistancia = Integer.MAX_VALUE;
            int u = -1;

            for (Map.Entry<Integer, Integer> entry : distancia.entrySet()) {
                int vertice = entry.getKey();
                int dist = entry.getValue();

                if (!visitado.get(vertice) && dist < minDistancia) {
                    minDistancia = dist;
                    u = vertice;
                }
            }

            if (u == -1) break;  // Si no hay vértice no visitado con distancia mínima, salir del bucle

            visitado.put(u, true);

            ConjuntoTDA adyacentes = g.Vertices();  // Para iterar todos los posibles destinos
            while (!adyacentes.ConjuntoVacio()) {
                int v = adyacentes.Elegir();
                adyacentes.Sacar(v);

                if (!visitado.get(v) && g.ExisteArista(u, v)) {
                    int nuevaDist = distancia.get(u) + g.PesoArista(u, v);

                    if (nuevaDist < distancia.get(v)) {
                        distancia.put(v, nuevaDist);
                        if (!distanciasMinimas.ExisteArista(origen, v)) {
                            distanciasMinimas.AgregarArista(origen, v, nuevaDist);
                        } else {
                            distanciasMinimas.AgregarArista(origen, v, Math.min(distanciasMinimas.PesoArista(origen, v), nuevaDist));
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
        Map<Integer, int[]> dictCentros = new HashMap<>();
        Map<Integer, Integer> dictClientes = new HashMap<>();
        Map<Integer, GrafoLA> dictGrafosC = new HashMap<>();

        FileReader archivo;
        BufferedReader lector;
        try {
            archivo = new FileReader("src/Archivos/clientesYCentros.txt");
            if (archivo.ready()) {
                lector = new BufferedReader(archivo);
                String linea;

                while ((linea = lector.readLine()) != null) {
                    String[] entidadInfo = linea.split(",");
                    int clave = Integer.parseInt(entidadInfo[0]);

                    if (clave < 50) {
                        int valor = Integer.parseInt(entidadInfo[1]);
                        dictClientes.put(clave, valor);
                    } else {
                        int[] valores = new int[2];
                        valores[0] = Integer.parseInt(entidadInfo[1]);
                        valores[1] = Integer.parseInt(entidadInfo[2]);
                        dictCentros.put(clave, valores);
                    }
                    grafo.AgregarVertice(clave);
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
                    grafo.AgregarArista(nodoOrigen, nodoDestino, costo);
                }
            }
        } catch (Exception e) {
            System.out.println("Error " + e);
        }

        for (Integer key : dictCentros.keySet()) {
            GrafoLA g = Dijkstra(grafo, key, dictCentros);
            dictGrafosC.put(key, g);
        }

        List<Integer> centrosKeys = new ArrayList<>(dictCentros.keySet());
        BackTracking(dictGrafosC, centrosKeys, 0, dictCentros, dictClientes, new ArrayList<>(),0);

        System.out.println("Mejor combinación de centros activos: " + combinacionOptima);
        System.out.println("Costo mínimo total: $" + costoMinimo);
    }
}
