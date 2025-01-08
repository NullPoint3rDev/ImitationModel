import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import production.ProductionCenter;
import production.ProductionLineModel;
import org.modeling.Part;
import org.modeling.Worker;

import static org.junit.jupiter.api.Assertions.*;


class ProductionLineModelTest {
    private ProductionLineModel model;
    private ProductionCenter center1;
    private ProductionCenter center2;
    private Worker worker1;
    private Worker worker2;

    @BeforeEach
    void setUp() {
        model = new ProductionLineModel(1.0, "production_simulation_log.csv");
        center1 = new ProductionCenter(1, 2, 2.0);
        center2 = new ProductionCenter(2, 3, 1.5);
        worker1 = new Worker(1);
        worker2 = new Worker(2);

        model.addProductionCenter(center1);
        model.addProductionCenter(center2);
        model.addWorker(worker1);
        model.addWorker(worker2);

        center1.addPartToBuffer(new Part(1));
        center2.addPartToBuffer(new Part(2));
    }

    @Test
    void testProcessingParts() {
        assertEquals(2, center1.getBufferSize(), "Размер буфера центра 1 должен быть 2");
        assertEquals(0, center1.getProcessedPartsCount(), "Обработанные детали должны быть 0");

        model.update();

        assertEquals(1, center1.getBufferSize(), "Размер буфера центра 1 должен быть 1 после обработки");
        assertEquals(1, center1.getProcessedPartsCount(), "Обработанные детали должны быть 1");
    }



    @Test
    void testPartTransferBetweenCenters() {
        model.addConnection(center1, center2);

        assertEquals(0, center2.getBufferSize(), "Центр 2 должен быть пустым");

        model.update();

        assertEquals(1, center2.getBufferSize(), "Центр 2 должен получить 1 деталь");
        assertEquals(1, center1.getBufferSize(), "Центр 1 должен передать 1 деталь");
    }
}