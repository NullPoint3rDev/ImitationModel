package production;

import org.modeling.Part;
import org.modeling.Worker;

import java.util.*;

public class ProductionCenter {
    private final int id;
    private final String name;
    final int maxWorkers;
    private final double processingTime;
    private final List<Worker> workers;
    private final Queue<Part> buffer;
    private final List<ProductionCenter> connections;
    private int processedPartsCount;

    public ProductionCenter(int id, int maxWorkers, double processingTime) {
        this.id = id;
        this.name = "Production Center #" + id;
        this.maxWorkers = maxWorkers;
        this.processingTime = processingTime;
        this.workers = new ArrayList<>();
        this.buffer = new LinkedList<>();
        this.connections = new ArrayList<>();
        this.processedPartsCount = 0;
    }

    public String getName() {
        return name;
    }

    public void addWorker(Worker worker) {
        if (workers.size() < maxWorkers) {
            workers.add(worker);
        }
    }

    public Worker removeWorker() {
        if (!workers.isEmpty()) {
            return workers.remove(0);
        }
        return null;
    }

    public void addPartToBuffer(Part part) {
        buffer.offer(part);
    }

    public Part getPartFromBuffer() {
        return buffer.poll();
    }

    public void addConnection(ProductionCenter center) {
        connections.add(center);
    }

    public void processingParts() {
        Iterator<Worker> workerIterator = workers.iterator();
        while (workerIterator.hasNext() && !buffer.isEmpty()) {
            Worker worker = workerIterator.next();
            if (!worker.isBusy()) {
                Part part = getPartFromBuffer();
                if (part != null) {
                    worker.startProcessing(part, processingTime);
                    processedPartsCount++;
                }
            }
        }
    }

    public int getBufferSize() {
        return buffer.size();
    }

    public List<ProductionCenter> getConnections() {
        return connections;
    }

    public double getLoad() {
        return buffer.size() / (double) maxWorkers;
    }

    @Override
    public String toString() {
        return String.format("%s: Workers %d/%d, Buffer %d", name, workers.size(), maxWorkers, buffer.size());
    }


    public List<Worker> getWorkers() {
        return workers;
    }

    public int getMaxWorkers() {
        return maxWorkers;
    }

    public int getId() {
        return id;
    }

    public int getProcessedPartsCount() {
        return processedPartsCount;
    }
}
