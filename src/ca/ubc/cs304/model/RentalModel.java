package ca.ubc.cs304.model;

import java.sql.Timestamp;
import java.util.Objects;

public class RentalModel {
    private final int rid;
    private final String vLicense;
    private final int dLicense;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final String expDate;
    private final int confNo;
    private final Timestamp fromDateTime;
    private final Timestamp toDateTime;

    public RentalModel(int rid, String vLicense, int dLicense, int odometer, String cardName,
            int cardNo, String expDate, int confNo, Timestamp fromDateTime, Timestamp toDateTime) {
        this.rid = rid;
        this.vLicense = vLicense;
        this.dLicense = dLicense;
        this.odometer = odometer;
        this.cardName = cardName;
        this.cardNo = cardNo;
        this.expDate = expDate;
        this.confNo = confNo;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
    }

    public int getRid() {
        return rid;
    }

    public String getvLicense() {
        return vLicense;
    }

    public int getdLicense() {
        return dLicense;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getCardName() {
        return cardName;
    }

    public int getCardNo() {
        return cardNo;
    }

    public String getExpDate() {
        return expDate;
    }

    public int getConfNo() {
        return confNo;
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
        RentalModel that = (RentalModel) o;
        return rid == that.rid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rid);
    }
}
