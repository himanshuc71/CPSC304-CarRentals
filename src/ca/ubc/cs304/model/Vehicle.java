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
    private final VehicleType vtname;
    private final BranchModel branch;

    public Vehicle(String vLicence, String make, String model, String year, String color, int odometer,
                   String status, VehicleType vtname, BranchModel branch) {
        this.vLicence = vLicence;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.status = status;
        this.vtname = vtname;
        this.branch = branch;
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

    public VehicleType getVtname() {
        return vtname;
    }

    public String getLocation() {
        return this.branch.getLocation();
    }

    public String getCity() {
        return this.branch.getCity();
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
