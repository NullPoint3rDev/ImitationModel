package org.modeling;

import production.ProductionCenter;

public class Worker {
    private final int id;
    private boolean busy;
    private double remainingTime;
    private ProductionCenter currentCenter;

    public Worker(int id) {
        this.id = id;
        this.busy = false;
        this.remainingTime = 0.0;
    }

    public boolean isBusy() {
        return busy;
    }

    public ProductionCenter getCurrentCenter() {
        return currentCenter;
    }

    public void moveTo(ProductionCenter center) {
        this.currentCenter = center;
    }

    public void startProcessing(Part part, double processingTime) {
        this.busy = true;
        this.remainingTime = processingTime;
        System.out.println("Worker #" + id + " начал обработку " + part + " в центре " + currentCenter.getName());
    }

    public void update(double timeStep) {
        if (busy) {
            remainingTime -= timeStep;
            if (remainingTime <= 0) {
                busy = false;
                System.out.println("Worker #" + id + " завершил работу.");
            }
        }
    }

    @Override
    public String toString() {
        return "Worker #" + id + (busy ? " (занят)" : " (свободен)");
    }
}
