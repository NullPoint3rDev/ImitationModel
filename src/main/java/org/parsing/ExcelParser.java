package org.parsing;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

    public static ProductionLineModel parseExelFile(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workBook = new XSSFWorkbook(fis);
            Sheet scenarioSheet = workBook.getSheetAt(0);
            Sheet productionCenterSheet = workBook.getSheetAt(1);
            Sheet connectionSheet = workBook.getSheetAt(2);

            // Parsing scenario sheet
            Row scenarioRow = scenarioSheet.getRow(1); // Assuming data starts from row 1 (skip header)
            int workersCount = 0;
            int detailsCount = 0;

            // Check if the cell is numeric before parsing
            if (scenarioRow.getCell(0).getCellType() == CellType.NUMERIC) {
                workersCount = (int) scenarioRow.getCell(0).getNumericCellValue();
            }

            if (scenarioRow.getCell(1).getCellType() == CellType.NUMERIC) {
                detailsCount = (int) scenarioRow.getCell(1).getNumericCellValue();
            }

            System.out.println("workersCount: " + workersCount);
            System.out.println("detailsCount: " + detailsCount);

            // Initialize model
            ProductionLineModel model = new ProductionLineModel(1.0); // Default time step 1.0

            // Create workers
            for (int i = 0; i < workersCount; i++) {
                model.addWorker(new Worker(i + 1));
            }

            // Create parts
            model.initializeParts(detailsCount);

            // Parse ProductionCenter sheet
            Map<Integer, ProductionCenter> productionCenters = new HashMap<>();
            for (int i = 1; i < productionCenterSheet.getPhysicalNumberOfRows(); i++) {
                Row row = productionCenterSheet.getRow(i); // Skip the header row
                if (row != null) {
                    int id = 0;
                    double performance = 0;
                    int maxWorkersCount = 0;
                    String name = "";

                    // Read the id, name, performance, maxWorkersCount
                    if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                        id = (int) row.getCell(0).getNumericCellValue();
                    }

                    if (row.getCell(1).getCellType() == CellType.STRING) {
                        name = row.getCell(1).getStringCellValue();
                    }

                    if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                        performance = row.getCell(2).getNumericCellValue();
                    }

                    if (row.getCell(3).getCellType() == CellType.NUMERIC) {
                        maxWorkersCount = (int) row.getCell(3).getNumericCellValue();
                    }

                    ProductionCenter center = new ProductionCenter(id, maxWorkersCount, performance);
                    productionCenters.put(id, center);
                    model.addProductionCenter(center);

                    System.out.println("Production Center ID: " + id + ", Name: " + name +
                            ", Performance: " + performance + ", Max Workers Count: " + maxWorkersCount);
                }
            }

            // Parsing Connection sheet
            for (int i = 1; i < connectionSheet.getPhysicalNumberOfRows(); i++) {
                Row row = connectionSheet.getRow(i); // Skip the header row
                if (row != null) {
                    String sourceCenterName = "";
                    String destCenterName = "";

                    // Read source and destination center names
                    if (row.getCell(0).getCellType() == CellType.STRING) {
                        sourceCenterName = row.getCell(0).getStringCellValue();
                    }

                    if (row.getCell(1).getCellType() == CellType.STRING) {
                        destCenterName = row.getCell(1).getStringCellValue();
                    }

                    ProductionCenter sourceCenter = findCenterByName(productionCenters, sourceCenterName);
                    ProductionCenter destCenter = findCenterByName(productionCenters, destCenterName);

                    if (sourceCenter != null && destCenter != null) {
                        model.addConnection(sourceCenter, destCenter);
                        System.out.println("Source Center: " + sourceCenterName + ", Destination Center: " + destCenterName);
                    } else {
                        System.out.println("Error: Could not find connection between " + sourceCenterName + " and " + destCenterName);
                    }
                }
            }
            return model;
        }
    }

    private static int getCellNumericValue(Row row, int cellIndex) {
        try {
            if (row.getCell(cellIndex) != null && row.getCell(cellIndex).getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                return (int) row.getCell(cellIndex).getNumericCellValue();
            } else if (row.getCell(cellIndex) != null && row.getCell(cellIndex).getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                return Integer.parseInt(row.getCell(cellIndex).getStringCellValue().trim());
            }
        } catch (Exception e) {
            System.out.println("Error reading numeric value at row " + row.getRowNum() + ", cell " + cellIndex + ": " + e.getMessage());
        }
        return 0; // Default value if there's an error
    }

    private static String getCellStringValue(Row row, int cellIndex) {
        try {
            if (row.getCell(cellIndex) != null && row.getCell(cellIndex).getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                return row.getCell(cellIndex).getStringCellValue().trim();
            }
        } catch (Exception e) {
            System.out.println("Error reading string value at row " + row.getRowNum() + ", cell " + cellIndex + ": " + e.getMessage());
        }
        return ""; // Default value if there's an error
    }

    private static ProductionCenter findCenterByName(Map<Integer, ProductionCenter> centers, String name) {
        // This assumes the name is unique and follows the format "ProductionCenter #N"
        for (ProductionCenter center : centers.values()) {
            if (center.getName().equals(name)) {
                return center;
            }
        }
        return null;
    }
}
