package production;

import org.modeling.Connection;
import org.modeling.Part;
import org.modeling.Worker;

import java.util.*;

public class ProductionLineModel {
    private List<ProductionCenter> productionCenters; // List that contains all production centers
    private List<Worker> workers; // List that contains all workers
    private List<Connection> connections; // List that contains all connections
    private Queue<Part> partsQueue; // part's queue for the native (root) center
    private double currentTime; // Current model's time
    private double timeStep; // Time step for updating the information about production

    // Let's make a constructor for the class
    public ProductionLineModel(double timeStep) {
        this.productionCenters = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.partsQueue = new LinkedList<>();
        this.currentTime = 0.0;
        this.timeStep = timeStep;
    }

    // Now let's add a production center
    public void addProductionCenter(ProductionCenter center) {
        productionCenters.add(center);
    }

    // Let's add a worker
    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    // Let's add a connection between centers
    public void addConnection(ProductionCenter from, ProductionCenter to) {
        Connection connection = new Connection(from, to);
        connections.add(connection);
        from.addConnection(to);
    }

    // Let's add parts to the native (root) center
    public void initializeParts(int count) {
        for (int i = 0; i < count; i++) {
            Part part = new Part(i + 1);
            partsQueue.offer(part);
            if (!productionCenters.isEmpty()) {
                productionCenters.get(0).addPartToBuffer(part);
            }
        }
    }

    // Let's distribute workers between centers
    private void distributeWorkers() {
        for (ProductionCenter center : productionCenters) {
            while (center.getBufferSize() > 0 && center.getWorkers().size() < center.getMaxWorkers()) {
                Optional<Worker> freeWorker = workers.stream()
                        .filter(worker -> !worker.isBusy() && worker.getCurrentCenter() != center)
                        .findFirst();
                freeWorker.ifPresent(worker -> {
                    worker.moveTo(center);
                    center.addWorker(worker);
                });
            }
        }
    }

    // Model updating for time step
    public void update() {

        // Updating worker
        for (Worker worker : workers) {
            worker.update(timeStep);
        }

        // Updating centers
        for (ProductionCenter center : productionCenters) {
            center.processingParts();
        }

        // Transferring parts between centers
        for (Connection connection : connections) {
            ProductionCenter from = connection.getFromCenter();
            Part part = from.getPartFromBuffer();
            if (part != null) {
                connection.transferPart(part);
            }
        }

        // Distributing workers
        distributeWorkers();

        // Increasing current time
        currentTime += timeStep;
    }

    // Now let's log current status
    public void logState() {
        System.out.printf("Time: %.2f%n", currentTime);
        for (ProductionCenter center : productionCenters) {
            System.out.println(center.getStatus());
        }
        System.out.println();
    }

    // Checking if all parts were processed
    public boolean isProcessingComplete() {
        return partsQueue.isEmpty() && productionCenters.stream()
                .allMatch(center -> center.getBufferSize() == 0)
                && workers.stream().noneMatch(Worker::isBusy);
    }

    // Now let's run our model
    public void run() {
        while (!isProcessingComplete()) {
            update();
            logState();
        }
    }
}
