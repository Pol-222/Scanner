package scanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NetworkScannerApp extends JFrame {
    private JTextField startIPField, endIPField, timeoutField, retriesField;
    private JButton scanButton, stopButton, clearButton, saveButton, filterButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private volatile boolean scanning = false;

    public NetworkScannerApp() {
        setTitle("Escáner de Red");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("IP de inicio:"));
        startIPField = new JTextField(10);
        inputPanel.add(startIPField);

        inputPanel.add(new JLabel("IP de fin:"));
        endIPField = new JTextField(10);
        inputPanel.add(endIPField);

        inputPanel.add(new JLabel("Tiempo de espera (ms):"));
        timeoutField = new JTextField("1000", 5);
        inputPanel.add(timeoutField);

        inputPanel.add(new JLabel("Número de reintentos:"));
        retriesField = new JTextField("1", 3);
        inputPanel.add(retriesField);

        scanButton = new JButton("Iniciar escaneo");
        stopButton = new JButton("Detener escaneo");
        clearButton = new JButton("Limpiar");
        saveButton = new JButton("Guardar resultados");
        filterButton = new JButton("Mostrar solo activos");

        inputPanel.add(scanButton);
        inputPanel.add(stopButton);
        inputPanel.add(clearButton);
        inputPanel.add(saveButton);
        inputPanel.add(filterButton);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de resultados
        tableModel = new DefaultTableModel(new String[]{"IP", "Nombre equipo", "Activo", "Tiempo (ms)"}, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Barra de estado
        statusLabel = new JLabel("Listo para escanear");
        add(statusLabel, BorderLayout.SOUTH);

        // Eventos
        scanButton.addActionListener(e -> startScan());
        stopButton.addActionListener(e -> scanning = false);
        clearButton.addActionListener(e -> tableModel.setRowCount(0));
        saveButton.addActionListener(e -> NetworkScanner.saveResults(tableModel));
        filterButton.addActionListener(e -> filterActive());
    }

    private void startScan() {
        String startIP = startIPField.getText();
        String endIP = endIPField.getText();
        int timeout = Integer.parseInt(timeoutField.getText());
        int retries = Integer.parseInt(retriesField.getText());

        tableModel.setRowCount(0);
        scanning = true;
        int activeCount = 0;

        for (ScanResult result : NetworkScanner.scanRange(startIP, endIP, timeout, retries)) {
            if (!scanning) break;
            tableModel.addRow(new Object[]{
                result.getIp(),
                result.getHostname(),
                result.isActive() ? "Sí" : "No",
                result.getResponseTime()
            });
            if (result.isActive()) activeCount++;
        }

        statusLabel.setText("Escaneo finalizado - Equipos activos: " + activeCount);
    }

    private void filterActive() {
        DefaultTableModel filteredModel = new DefaultTableModel(new String[]{"IP", "Nombre equipo", "Activo", "Tiempo (ms)"}, 0);
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("Sí".equals(tableModel.getValueAt(i, 2))) {
                filteredModel.addRow(new Object[]{
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3)
                });
            }
        }
        resultTable.setModel(filteredModel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkScannerApp().setVisible(true));
    }
}
