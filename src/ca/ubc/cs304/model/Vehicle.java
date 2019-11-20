package ca.ubc.cs304.model;

import java.util.Objects;

public class Vehicle {
    private final String vLicence;
    private final String make;
    private final String model;
    private final String year;
    private final String color;
    private final int odometer;
    private String status;  // available or maintenance or rented
    private final String vtname;
    private final String location;
    private final String city;

    public Vehicle(String vLicence, String make, String model, String year, String color, int odometer,
                   String status, String vtname, String location, String city) {
        this.vLicence = vLicence;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.status = status;
        this.vtname = vtname;
        this.location = location;
        this.city = city;
    }

    public String getvLicence() {
        return vLicence;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getStatus() {
        return status;
    }

    public String getVtname() {
        return vtname;
    }

    public String getLocation() {
        return this.location;
    }

    public String getCity() {
        return this.city;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return vLicence.equals(vehicle.vLicence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vLicence);
    }
}
