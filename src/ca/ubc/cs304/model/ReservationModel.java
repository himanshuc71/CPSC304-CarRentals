package ca.ubc.cs304.model;

import java.util.Objects;

public class ReservationModel {
    private final int confNo;
    private final VehicleType vtname;
    private final CustomerModel cellphone;
    private final String fromDateTime;
    private final String toDateTime;

    public ReservationModel(int confNo, VehicleType vtname, CustomerModel cellphone, String fromDateTime,
                            String toDateTime){
        this.confNo = confNo;
        this.vtname = vtname;
        this.cellphone = cellphone;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
    }

    public int getConfNo() {
        return confNo;
    }

    public VehicleType getVtname() {
        return vtname;
    }

    public CustomerModel getCellphone() {
        return cellphone;
    }

    public String getFromDateTime() {
        return fromDateTime;
    }

    public String getToDateTime() {
        return toDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationModel that = (ReservationModel) o;
        return confNo == that.confNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(confNo);
    }
}
