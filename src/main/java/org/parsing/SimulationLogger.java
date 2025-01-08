package production;

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
            // Записываем заголовки
            writer.append("Time,Center ID,Center Name,Workers,Buffers,Processed Parts,Connections\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logState(double currentTime, List<ProductionCenter> productionCenters) {
        try {
            for (ProductionCenter center : productionCenters) {
                writer.append(String.format("%.2f,%d,%s,%d/%d,%d,%d,%d\n",
                        currentTime,
                        center.getId(),
                        center.getName(),
                        center.getWorkers().size(),
                        center.getMaxWorkers(),
                        center.getBufferSize(),
                        center.getConnections().size(),
                        center.getProcessedPartsCount()
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
