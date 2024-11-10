import TDAs.conjuntos.ConjuntoA;
import TDAs.conjuntos.ConjuntoTDA;
import TDAs.grafobi.GrafoLA;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
    public static int costoMinimo = Integer.MAX_VALUE;
    public static List<Integer> combinacionOptima = new ArrayList<>();
    private static Map<String, Integer> memoCostosTransporte = new HashMap<>();

    public static void BackTracking(Map<Integer, GrafoLA> grafos, List<Integer> centrosKeys, Integer index, Map<Integer, int[]> infoCentros,
                                    Map<Integer, Integer> infoClientes, ArrayList<Integer> centrosIncluidos) {
        if (index == centrosKeys.size()) {
            if (centrosIncluidos.isEmpty()) {
                return;
            }
            int costoTotal = calcularCostoTotal(centrosIncluidos, grafos, infoCentros, infoClientes);
            if (costoTotal < costoMinimo) {
                costoMinimo = costoTotal;
                combinacionOptima.clear();
                combinacionOptima.addAll(centrosIncluidos);
            }
            return;
        }

        int centro = centrosKeys.get(index);
        centrosIncluidos.add(centro);
        BackTracking(grafos, centrosKeys, index + 1, infoCentros, infoClientes, centrosIncluidos);

        centrosIncluidos.remove(centrosIncluidos.size() - 1);
        BackTracking(grafos, centrosKeys, index + 1, infoCentros, infoClientes, centrosIncluidos);
    }

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
        int costoTransporteTotal = calcularCostoAsignacionClientes(centrosActivos, dictGrafosC, dictClientes);
        return costoFijoTotal + costoTransporteTotal;
    }

    public static int calcularCostoAsignacionClientes(ArrayList<Integer> centrosActivos, Map<Integer, GrafoLA> dictGrafosC,
                                                      Map<Integer, Integer> dictClientes) {
        int costoTransporteTotal = 0;
        for (int cliente : dictClientes.keySet()) {
            int centroMasCercano = encontrarCentroMasCercano(cliente, centrosActivos, dictGrafosC);
            if (centroMasCercano == -1) {
                System.out.println("Error: No se encontrÃ³ un centro accesible para el cliente " + cliente);
                return Integer.MAX_VALUE;
            }
            String claveMemo = centroMasCercano + "-" + cliente;
            int costoTransporte;
            if (memoCostosTransporte.containsKey(claveMemo)) {
                costoTransporte = memoCostosTransporte.get(claveMemo);
            } else {
                costoTransporte = dictGrafosC.get(centroMasCercano).costo(centroMasCercano, cliente) * dictClientes.get(cliente);
                memoCostosTransporte.put(claveMemo, costoTransporte);
            }
            costoTransporteTotal += costoTransporte;
        }
        return costoTransporteTotal;
    }

    public static int encontrarCentroMasCercano(int cliente, ArrayList<Integer> centrosActivos, Map<Integer, GrafoLA> dictGrafosC) {
        int distanciaMinima = Integer.MAX_VALUE;
        int centroMasCercano = -1;
        for (int centro : centrosActivos) {
            GrafoLA grafoCentro = dictGrafosC.get(centro);
            if (grafoCentro != null && grafoCentro.ExisteArista(centro, cliente)) {
                int distancia = grafoCentro.costo(centro, cliente);
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    centroMasCercano = centro;
                }
            }
        }
        return centroMasCercano;
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
            int u = obtenerVerticeMinDistancia(distancia, visitado);
            if (u == -1) break;

            visitado.put(u, true);

            ConjuntoTDA adyacentes = g.Vertices();
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

    private static int obtenerVerticeMinDistancia(Map<Integer, Integer> distancia, Map<Integer, Boolean> visitado) {
        int minDistancia = Integer.MAX_VALUE;
        int minVertice = -1;

        for (Map.Entry<Integer, Integer> entry : distancia.entrySet()) {
            int vertice = entry.getKey();
            int dist = entry.getValue();

            if (!visitado.get(vertice) && dist < minDistancia) {
                minDistancia = dist;
                minVertice = vertice;
            }
        }
        return minVertice;
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

        List<Integer> keys = new ArrayList<>(dictCentros.keySet());
        keys.sort(Collections.reverseOrder());

        ArrayList<Integer> centrosIncluidos = new ArrayList<>();
        List<Integer> centrosKeys = new ArrayList<>(dictCentros.keySet());
        BackTracking(dictGrafosC, centrosKeys, 0, dictCentros, dictClientes, centrosIncluidos);

        System.out.println("Mejor combinación de centros activos: " + combinacionOptima);
        System.out.println("Costo mínimo total: " + costoMinimo);
    }
}
