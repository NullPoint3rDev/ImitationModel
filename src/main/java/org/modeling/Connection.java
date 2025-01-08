package org.modeling;

import production.ProductionCenter;

public class Connection {
    private ProductionCenter fromCenter; // Root center
    private ProductionCenter toCenter; // Destination center

    // Let's make a constructor for the class
    public Connection(ProductionCenter fromCenter, ProductionCenter toCenter) {
        this.fromCenter = fromCenter;
        this.toCenter = toCenter;
    }

    // Let's get the root center
    public ProductionCenter getFromCenter() {
        return fromCenter;
    }

    // Let's get the destination center
    public ProductionCenter getToCenter() {
        return toCenter;
    }

    // Function that will transfer a part
    public void transferPart(Part part) {
        if (part != null) {
            toCenter.addPartToBuffer(part);
        }
    }

    @Override
    public String toString() {
        return String.format("Connection{fromCenter=%d, toCenter=%d}",
                fromCenter.getId(), toCenter.getId());
    }
}