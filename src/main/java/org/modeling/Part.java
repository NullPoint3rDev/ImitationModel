package org.modeling;

public class Part {
    private int id; // ID of the part
    private String status; // Status of the part ("WAITING", "PROCESSING", "DONE")

    // Let's make a constructor for the class
    public Part(int id) {
        this.id = id;
        this.status = "WAITING"; // Default status if waiting, so the part is waiting to be processed
    }

    // Some getter and one setter (for status's setting)
    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Part{id=%d, status='%s'}", id, status);
    }
}
