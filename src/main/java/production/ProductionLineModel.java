package production;

import org.modeling.Connection;
import org.modeling.Part;
import org.modeling.Worker;
import org.parsing.SimulationLogger;

import java.util.*;

public class ProductionLineModel {
    private final List<ProductionCenter> productionCenters;
    private final List<Worker> workers;
    private List<Connection> connections;
    private final Queue<Part> partsQueue;
    private double currentTime;
    private final double timeStep;
    private SimulationLogger logger;

    public ProductionLineModel(double timeStep, String logFilePath) {
        this.productionCenters = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.partsQueue = new LinkedList<>();
        this.currentTime = 0.0;
        this.timeStep = timeStep;
        this.logger = new SimulationLogger(logFilePath);
    }

    public ProductionLineModel(double timeStep) {
        this.productionCenters = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.partsQueue = new LinkedList<>();
        this.currentTime = 0.0;
        this.timeStep = timeStep;
        this.logger = new SimulationLogger("simulation_log.csv");
    }

    public void addProductionCenter(ProductionCenter center) {
        productionCenters.add(center);
    }

    public void addConnection(ProductionCenter from, ProductionCenter to) {
        Connection connection = new Connection(from, to);
        connections.add(connection);
        from.addConnection(to); // Убедитесь, что этот метод реализован в ProductionCenter
    }


    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void initializeParts(int count) {
        for (int i = 1; i <= count; i++) {
            Part part = new Part(i);
            partsQueue.offer(part);
            if (!productionCenters.isEmpty()) {
                productionCenters.get(0).addPartToBuffer(part);
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

    private void distributeWorkers() {
        for (ProductionCenter center : productionCenters) {
            while (center.getBufferSize() > 0 && center.getWorkers().size() < center.maxWorkers) {
                Optional<Worker> freeWorker = workers.stream()
                        .filter(worker -> !worker.isBusy())
                        .findFirst();
                freeWorker.ifPresent(worker -> {
                    center.addWorker(worker);
                    worker.moveTo(center);
                });
            }
        }
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
