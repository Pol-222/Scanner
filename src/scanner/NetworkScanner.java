package scanner;


import java.net.InetAddress;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class NetworkScanner {

    private static volatile boolean scanning = true;

    public static List<ScanResult> scanRange(String startIP, String endIP, int timeout, int retries) {
        List<ScanResult> results = new ArrayList<>();
        try {
            String[] startParts = startIP.split("\\.");
            String[] endParts = endIP.split("\\.");

            int start = Integer.parseInt(startParts[3]);
            int end = Integer.parseInt(endParts[3]);
            String base = startParts[0] + "." + startParts[1] + "." + startParts[2] + ".";

            scanning = true;

            for (int i = start; i <= end; i++) {
                if (!scanning) break; // detener si se presiona "Detener escaneo"

                String ip = base + i;
                boolean active = false;
                long responseTime = -1;
                String hostname = "Desconocido";

                // Reintentos
                for (int attempt = 0; attempt < retries; attempt++) {
                    long startTime = System.currentTimeMillis();
                    InetAddress inet = InetAddress.getByName(ip);

                    if (inet.isReachable(timeout)) {
                        active = true;
                        responseTime = System.currentTimeMillis() - startTime;
                        hostname = inet.getHostName();
                        break; // salir si ya respondió
                    }
                }

                // Si nunca respondió, mantener tiempo en -1
                if (!active) {
                    responseTime = -1;
                }

                results.add(new ScanResult(ip, hostname, active, responseTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void stopScan() {
        scanning = false;
    }

    public static void saveResults(DefaultTableModel tableModel) {
        try (FileWriter writer = new FileWriter("scan_results.txt")) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(
                    tableModel.getValueAt(i, 0) + " - " +
                    tableModel.getValueAt(i, 1) + " - " +
                    tableModel.getValueAt(i, 2) + " - " +
                    tableModel.getValueAt(i, 3) + " ms\n"
                );
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

