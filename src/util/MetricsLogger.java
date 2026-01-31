package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MetricsLogger {

    private static final String CSV_FILE = "data/tiempos_ejecucion.csv";

    // Algoritmo, Nodo Inicio, Nodo Destino, Tiempo
    public static void log(String algorithm, String inicio, String destino, long durationNs) {
        try (PrintWriter out = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            out.println(algorithm + "," + inicio + "," + destino + "," + durationNs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}