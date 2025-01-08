package production;

import org.modeling.Part;
import org.modeling.Worker;

public class ProductionLineTest {
    public static void main(String[] args) {
        // Указываем путь для лог-файла
        String logFilePath = "parsing_log.csv";

        // Создаем модель с шагом времени 1 минута
        ProductionLineModel model = new ProductionLineModel(1.0, logFilePath);

        ProductionCenter center1 = new ProductionCenter(1, 2, 2.0);
        ProductionCenter center2 = new ProductionCenter(2, 3, 1.5);
        ProductionCenter center3 = new ProductionCenter(3, 1, 1.0);

        model.addProductionCenter(center1);
        model.addProductionCenter(center2);
        model.addProductionCenter(center3);

        Worker worker1 = new Worker(1);
        Worker worker2 = new Worker(2);
        Worker worker3 = new Worker(3);

        model.addWorker(worker1);
        model.addWorker(worker2);
        model.addWorker(worker3);

        model.addConnection(center1, center2);
        model.addConnection(center2, center3);

        center1.addPartToBuffer(new Part(1));
        center1.addPartToBuffer(new Part(2));
        center1.addPartToBuffer(new Part(3));

        System.out.println("Starting the model:");
        model.run();
        System.out.println("All parts were processed. Check the log file: " + logFilePath);
    }
}
