package org.parsing;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modeling.Worker;
import production.ProductionCenter;
import production.ProductionLineModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelParser {

    public static ProductionLineModel parseExcelFile(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet scenarioSheet = workbook.getSheetAt(0);
            Sheet productionCenterSheet = workbook.getSheetAt(1);
            Sheet connectionSheet = workbook.getSheetAt(2);

            // Парсинг листа сценария (Scenario)
            Row scenarioRow = scenarioSheet.getRow(1); // Первая строка данных (строка 0 - заголовок)
            int workersCount = 0;
            int detailsCount = 0;

            if (scenarioRow != null) {
                workersCount = getCellNumericValue(scenarioRow, 0); // Колонка с количеством сотрудников
                detailsCount = getCellNumericValue(scenarioRow, 1); // Колонка с количеством деталей
            }

            System.out.println("Количество сотрудников: " + workersCount);
            System.out.println("Количество деталей: " + detailsCount);

            // Создание модели
            ProductionLineModel model = new ProductionLineModel(1.0); // Шаг времени по умолчанию - 1.0

            // Добавление работников
            for (int i = 0; i < workersCount; i++) {
                model.addWorker(new Worker(i + 1));
            }

            // Инициализация деталей
            model.initializeParts(detailsCount);

            // Парсинг листа центров производства (ProductionCenter)
            Map<String, ProductionCenter> productionCenters = new HashMap<>();
            for (int i = 1; i < productionCenterSheet.getPhysicalNumberOfRows(); i++) { // Начинаем с 1, чтобы пропустить заголовок
                Row row = productionCenterSheet.getRow(i);
                if (row != null) {
                    int id = getCellNumericValue(row, 0);
                    String name = getCellStringValue(row, 1);
                    double performance = getCellDoubleValue(row, 2);
                    int maxWorkersCount = getCellNumericValue(row, 3);

                    ProductionCenter center = new ProductionCenter(id, maxWorkersCount, performance);
                    productionCenters.put(name, center);
                    model.addProductionCenter(center);

                    System.out.println("Центр производства: ID=" + id + ", Имя=" + name +
                            ", Производительность=" + performance + ", Макс. сотрудников=" + maxWorkersCount);
                }
            }

            // Парсинг листа связей (Connection)
            for (int i = 1; i < connectionSheet.getPhysicalNumberOfRows(); i++) { // Начинаем с 1, чтобы пропустить заголовок
                Row row = connectionSheet.getRow(i);
                if (row != null) {
                    String sourceCenterName = getCellStringValue(row, 0);
                    String destCenterName = getCellStringValue(row, 1);

                    ProductionCenter sourceCenter = productionCenters.get(sourceCenterName);
                    ProductionCenter destCenter = productionCenters.get(destCenterName);

                    if (sourceCenter != null && destCenter != null) {
                        model.addConnection(sourceCenter, destCenter);
                        System.out.println("Связь: Источник=" + sourceCenterName + ", Назначение=" + destCenterName);
                    } else {
                        System.out.println("Ошибка: Не удалось найти связь между " + sourceCenterName + " и " + destCenterName);
                    }
                }
            }

            return model;
        }
    }

    // Метод для чтения числового значения
    private static int getCellNumericValue(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell != null && cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (value.matches("\\d+")) {
                    return Integer.parseInt(value);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения числового значения: строка " + row.getRowNum() + ", ячейка " + cellIndex + " (" + e.getMessage() + ")");
        }
        return 0; // Возвращаем 0 в случае ошибки
    }

    // Метод для чтения строкового значения
    private static String getCellStringValue(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim();
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения строкового значения: строка " + row.getRowNum() + ", ячейка " + cellIndex + " (" + e.getMessage() + ")");
        }
        return ""; // Возвращаем пустую строку в случае ошибки
    }

    // Метод для чтения чисел с плавающей точкой
    private static double getCellDoubleValue(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения значения с плавающей точкой: строка " + row.getRowNum() + ", ячейка " + cellIndex + " (" + e.getMessage() + ")");
        }
        return 0.0; // Возвращаем 0.0 в случае ошибки
    }
}
