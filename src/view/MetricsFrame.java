package view;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MetricsFrame extends JFrame {

    public MetricsFrame() {

        setTitle("Métricas BFS vs DFS");
        setSize(700, 400);
        setLocationRelativeTo(null);

        String[] cols = {
            "Algoritmo",
            "Nodo Inicio",
            "Nodo Destino",
            "Tiempo (ns)"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        try (BufferedReader br = new BufferedReader(
                new FileReader("data/tiempos_ejecucion.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                model.addRow(new Object[]{
                    parts[0], // BFS / DFS
                    parts[1], // Inicio
                    parts[2], // Destino
                    parts[3]  // Tiempo
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "No hay métricas registradas todavía."
            );
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}