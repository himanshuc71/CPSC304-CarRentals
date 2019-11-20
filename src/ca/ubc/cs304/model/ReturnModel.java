package ca.ubc.cs304.model;

import java.sql.Timestamp;

public class ReturnModel {
    private final RentalModel rid;
    private final Timestamp rtnDateTime;
    private final int odometer;
    private final int fullTank;   // 0 empty; 1 full
    private int value;

    public ReturnModel(RentalModel rid, Timestamp rtnDateTime, int odometer, int fullTank) {
        this.rid = rid;
        this.rtnDateTime = rtnDateTime;
        this.odometer = odometer;
        this.fullTank = fullTank;
    }
    // calc price of the rental based on the vt prices mentioned
    private void setValue(){
        this.value = rid.getVid().getVtname().getwRate();
    }

    public RentalModel getRid() {
        return rid;
    }

    public Timestamp getRtnDateTime() {
        return rtnDateTime;
    }

    public int getOdometer() {
        return odometer;
    }

    public int getFullTank() {
        return fullTank;
    }

    public int getValue() {
        return value;
    }
}
