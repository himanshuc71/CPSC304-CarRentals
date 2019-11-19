package ca.ubc.cs304.model;

public class ReturnModel {
    private final RentalModel rid;
    private final String rtnDateTime;
    private final int odometer;
    private final int fullTank;   // 0 empty; 1 full
    private int value;

    public ReturnModel(RentalModel rid, String rtnDateTime, int odometer, int fullTank) {
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

    public String getRtnDateTime() {
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
