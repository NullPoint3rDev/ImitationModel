package org.parsing;

import production.ProductionCenter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SimulationLogger {
    private final String filePath;
    private FileWriter writer;

    public SimulationLogger(String filePath) {
        this.filePath = filePath;
        try {
            writer = new FileWriter(filePath);

            writer.append("Time,Center ID,Center Name,Workers,Buffers,Processed Parts,Connections\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logState(double currentTime, List<ProductionCenter> productionCenters) {
        try {
            for (ProductionCenter center : productionCenters) {
                writer.append(String.format("%.2f,%s,%d/%d,%d,%d,%d,%d\n",
                        currentTime,
                        center.getName(), // %s - для имени центра (строка)
                        center.getWorkers().size(), // %d - текущее количество работников
                        center.getMaxWorkers(), // %d - максимальное количество работников
                        center.getId(), // %d - ID центра
                        center.getBufferSize(), // %d - размер буфера
                        center.getConnections().size(), // %d - количество соединений
                        center.getProcessedPartsCount() // %d - обработанные детали
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
