package ca.ubc.cs304.model;

import java.sql.Timestamp;

public class ReturnModel {
    private final int rid;
    private final Timestamp rtnDateTime;
    private final int odometer;
    private final int fullTank;   // 0 empty; 1 full
    private final int value;

    public ReturnModel(int rid, Timestamp rtnDateTime, int odometer, int fullTank, int value) {
        this.rid = rid;
        this.rtnDateTime = rtnDateTime;
        this.odometer = odometer;
        this.fullTank = fullTank;
        this.value = value;
    }

    public int getRid() {
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
