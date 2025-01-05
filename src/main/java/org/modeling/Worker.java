package org.modeling;

import production.ProductionCenter;

public class Worker {
    private int id; // ID of the worker
    private boolean busy; // Status: busy or not
    private ProductionCenter currentCenter; // Current production center
    private Part currentPart; // Part that is processing at this moment
    private double remainingTime; // Remaining time for part's processing (in minutes)

    // Let's make a constructor
    public Worker(int id) {
        this.id = id;
        this.busy = false;
        this.currentCenter = null;
        this.currentPart = null;
        this.remainingTime = 0.0;
    }

    // Function that will start part's processing
    public void startProcessing(Part part, double processingTime) {
        this.busy = true;
        this.currentPart = part;
        this.remainingTime = processingTime;
        part.setStatus("PROCESSING");
    }

    // Function that will finish part's processing
    public void completeProcessing() {
        if (currentPart != null) {
            currentPart.setStatus("DONE");
            currentPart = null;
            busy = false;
            remainingTime = 0.0;
        }
    }

    // Function that will update status depends on time
    public void update(double timeStep) {
        if (busy && remainingTime > 0) {
            remainingTime -= timeStep;
            if (remainingTime <= 0) {
                completeProcessing();
            }
        }
    }

    // Function that will move worker to the other production center
    public void moveTo(ProductionCenter newCenter) {
        if (!busy) {
            this.currentCenter = newCenter;
        }
    }

    // Now let's add some getters and only one setter (for current production center)
    public int getId() {
        return id;
    }

    public boolean isBusy() {
        return busy;
    }

    public ProductionCenter getCurrentCenter() {
        return currentCenter;
    }

    public Part getCurrentPart() {
        return currentPart;
    }

    public void setCurrentCenter(ProductionCenter center) {
        this.currentCenter = center;
    }
}
