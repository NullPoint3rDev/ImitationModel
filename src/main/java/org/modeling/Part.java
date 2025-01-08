package org.modeling;

public class Part {
    private final int id;

    public Part(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Part #" + id;
    }
}
