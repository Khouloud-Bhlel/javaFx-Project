package entities;

public class Location {
    private int id;
    private String name;
    private String address;
    private int capacity;

    // Default constructor
    public Location() {
    }

    // Constructor without id (for creating new locations)
    public Location(String name, String address, int capacity) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }

    // Constructor with id (for retrieving existing locations)
    public Location(int id, String name, String address, int capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // toString method
    @Override
    public String toString() {
        return String.format(
                "Name: %-20.20s%nAddress:%n%s%nCapacity: %-10d",
                name.replaceAll("(?<=\\G.{" + 20 + "})", "\n"), address.replaceAll("(?<=\\G.{" + 100 + "})", "\n"), capacity
        );
    }
}