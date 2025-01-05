package production;


import org.modeling.Part;
import org.modeling.Worker;

// In this class we will run our model and manually testing how it works
public class ProductionLineTest {
    public static void main(String[] args) {

        // Creating a model with 1 minute time step
        ProductionLineModel model = new ProductionLineModel(1.0);

        // Creating production centers
        ProductionCenter center1 = new ProductionCenter(1, 2, 2.0);
        ProductionCenter center2 = new ProductionCenter(2, 3, 1.5);
        ProductionCenter center3 = new ProductionCenter(3, 1, 1.0);

        // Adding centers to the model
        model.addProductionCenter(center1);
        model.addProductionCenter(center2);
        model.addProductionCenter(center3);

        // Creating workers
        Worker worker1 = new Worker(1);
        Worker worker2 = new Worker(2);
        Worker worker3 = new Worker(3);

        // Adding workers to the model
        model.addWorker(worker1);
        model.addWorker(worker2);
        model.addWorker(worker3);

        // Creating connections between centers
        model.addConnection(center1, center2);
        model.addConnection(center2, center3);

        // Initializing parts in the native (root) center
        center1.addPartToBuffer(new Part(1));
        center1.addPartToBuffer(new Part(2));
        center1.addPartToBuffer(new Part(3));

        // Running the model
        System.out.println("Starting the model:");
        model.run();
        System.out.println("All parts was processed.");
    }
}
