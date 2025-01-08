//package org.simulation;
//
//import org.modeling.Worker;
//import production.ProductionCenter;
//import production.ProductionLineModel;
//
//import java.util.List;
//
//public class ProcessSimulation {
//
//    private final ProductionLineModel model;
//
//    public ProcessSimulation(ProductionLineModel model) {
//        this.model = model;
//    }
//
//    public void simulateProcess() {
//        int timeStep = 0;
//        while (!model.allPartsProcessed()) {
//            System.out.println("Шаг времени: " + timeStep);
//
//            // 1. Выполнение обработки деталей в каждом центре
//            processDetails();
//
//            // 2. Перераспределение сотрудников для оптимизации
//            redistributeWorkers();
//
//            // 3. Переход к следующему шагу времени
//            timeStep++;
//        }
//
//        System.out.println("Моделирование завершено. Все детали обработаны за " + timeStep + " шагов.");
//    }
//
//    private void processDetails() {
//        for (ProductionCenter center : model.getProductionCenters()) {
//            center.processingParts();
//            System.out.println("Центр: " + center.getName() +
//                    ", Буфер: " + center.getBuffer() +
//                    ", Занято сотрудников: " + center.getBusyWorkers());
//        }
//    }
//
//    private void redistributeWorkers() {
//        List<ProductionCenter> centers = model.getProductionCenters();
//
//        // Сортировка по загрузке (центры с недостаточной загрузкой обрабатываются первыми)
//        centers.sort((c1, c2) -> Double.compare(c1.getLoad(), c2.getLoad()));
//
//        for (ProductionCenter center : centers) {
//            if (center.getLoad() < 1.0) { // Центр недозагружен
//                for (ProductionCenter donor : centers) {
//                    if (donor.getLoad() > 1.0 && donor != center) { // Центр-источник перегружен
//                        Worker reassignedWorker = donor.removeWorker(); // Убираем сотрудника из донора
//                        if (reassignedWorker != null) {
//                            center.addWorker(reassignedWorker); // Добавляем сотрудника в недозагруженный центр
//                            System.out.println("Перераспределение сотрудника: " +
//                                    "из " + donor.getName() + " в " + center.getName());
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
