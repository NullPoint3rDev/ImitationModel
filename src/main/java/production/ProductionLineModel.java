package production;

import org.modeling.Connection;
import org.modeling.Part;
import org.modeling.Worker;

import java.util.*;

public class ProductionLineModel {
    private List<ProductionCenter> productionCenters;
    private List<Worker> workers;
    private List<Connection> connections;
    private Queue<Part> partsQueue;
    private double currentTime;
    private double timeStep;
    private production.SimulationLogger logger;

    public ProductionLineModel(double timeStep, String logFilePath) {
        this.productionCenters = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.partsQueue = new LinkedList<>();
        this.currentTime = 0.0;
        this.timeStep = timeStep;
        this.logger = new production.SimulationLogger(logFilePath);
    }

    public void addProductionCenter(ProductionCenter center) {
        productionCenters.add(center);
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void addConnection(ProductionCenter from, ProductionCenter to) {
        Connection connection = new Connection(from, to);
        connections.add(connection);
        from.addConnection(to);
    }

    public void initializeParts(int count) {
        for (int i = 0; i < count; i++) {
            Part part = new Part(i + 1);
            partsQueue.offer(part);
            if (!productionCenters.isEmpty()) {
                productionCenters.get(0).addPartToBuffer(part);
            }
        }
    }

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

    public void update() {
        for (Worker worker : workers) {
            worker.update(timeStep);
        }

        for (ProductionCenter center : productionCenters) {
            center.processingParts();
        }

        for (Connection connection : connections) {
            ProductionCenter from = connection.getFromCenter();
            Part part = from.getPartFromBuffer();
            if (part != null) {
                connection.transferPart(part);
            }
        }

        distributeWorkers();
        currentTime += timeStep;

        logger.logState(currentTime, productionCenters);
    }

    public void logState() {
        System.out.printf("Time: %.2f%n", currentTime);
        for (ProductionCenter center : productionCenters) {
            System.out.println(center);
        }
        System.out.println();
    }

    public boolean isProcessingComplete() {
        return partsQueue.isEmpty() && productionCenters.stream()
                .allMatch(center -> center.getBufferSize() == 0)
                && workers.stream().noneMatch(Worker::isBusy);
    }

    public void run() {
        while (!isProcessingComplete()) {
            update();
            logState();
        }

        logger.close();
    }
}
