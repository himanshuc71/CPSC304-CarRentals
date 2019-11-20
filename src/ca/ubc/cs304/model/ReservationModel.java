package ca.ubc.cs304.model;

import java.sql.Timestamp;
import java.util.Objects;

public class ReservationModel {
    private final int confNo;
    private final VehicleType vtname;
    private final CustomerModel dLicense;
    private final Timestamp fromDateTime;
    private final Timestamp toDateTime;

    public ReservationModel(int confNo, VehicleType vtname, CustomerModel dLicense, Timestamp fromDateTime,
                            Timestamp toDateTime){
        this.confNo = confNo;
        this.vtname = vtname;
        this.dLicense = dLicense;
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
        return dLicense;
    }

    public Timestamp getFromDateTime() {
        return fromDateTime;
    }

    public Timestamp getToDateTime() {
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
