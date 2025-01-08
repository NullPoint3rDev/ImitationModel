package production;

import org.modeling.Part;
import org.modeling.Worker;

import java.util.*;

public class ProductionCenter {
    private int id; // Center's identification
    private String name;
    private int maxWorkers; // Maximum workers
    private double processingTime; // Processing time for one part (in minutes)
    private List<Worker> workers; // Current workers in the production center
    private Queue<Part> buffer; // Part's buffer
    private List<ProductionCenter> connections; // Connected centers
    private int processedPartsCount; // Counter for processed parts

    // Constructor
    public ProductionCenter(int id, int maxWorkers, double processingTime) {
        this.id = id;
        this.maxWorkers = maxWorkers;
        this.processingTime = processingTime;
        this.workers = new ArrayList<>();
        this.buffer = new LinkedList<>();
        this.connections = new ArrayList<>();
        this.name = "Производственный центр №" + id;
        this.processedPartsCount = 0; // Initialize counter
    }

    // Function that will add a new worker
    public boolean addWorker(Worker worker) {
        if (workers.size() < maxWorkers) {
            workers.add(worker);
            return true;
        }
        return false; // If it's impossible to add new worker
    }

    // Function that will remove a worker
    public boolean removeWorker(Worker worker) {
        return workers.remove(worker);
    }

    // Function that will add a new part to the buffer
    public void addPartToBuffer(Part part) {
        buffer.offer(part);
    }

    // Function that will give us and remove part from the buffer
    public Part getPartFromBuffer() {
        return buffer.poll(); // Give us and remove element or null if the buffer is empty
    }

    // Function that making connections with other centers
    public void addConnection(ProductionCenter center) {
        connections.add(center);
    }

    // Function that will give us current status
    public String getStatus() {
        return String.format("ID: %d, Workers: %d/%d, Buffer: %d, Processed Parts: %d, Connections: %d",
                id, workers.size(), maxWorkers, buffer.size(), processedPartsCount, connections.size());
    }

    // Processing parts
    public void processingParts() {
        Iterator<Worker> workerIterator = workers.iterator();
        while (workerIterator.hasNext() && !buffer.isEmpty()) {
            Worker worker = workerIterator.next();
            if (!worker.isBusy()) {
                Part part = getPartFromBuffer();
                if (part != null) {
                    worker.startProcessing(part, processingTime);
                    processedPartsCount++; // Increment counter when part is processed
                }
            }
        }
    }

    // New method to get the count of processed parts
    public int getProcessedPartsCount() {
        return processedPartsCount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getBufferSize() {
        return buffer.size();
    }

    public List<ProductionCenter> getConnections() {
        return connections;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public int getMaxWorkers() {
        return maxWorkers;
    }

    public String getName() {
        return name;
    }
}
