package ca.ubc.cs304.model;

import java.util.Objects;

public class RentalModel {
    private final int rid;
    private final Vehicle vLicense;
    private final CustomerModel cellPhone;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final String expDate;
    private final ReservationModel confNo;
    private final String fromDateTime;
    private final String toDateTime;

    public RentalModel(int rid, Vehicle vLicense, CustomerModel cellPhone, int odometer, String cardName,
            int cardNo, String expDate, ReservationModel confNo) {
        this.rid = rid;
        this.vLicense = vLicense;
        this.cellPhone = cellPhone;
        this.odometer = odometer;
        this.cardName = cardName;
        this.cardNo = cardNo;
        this.expDate = expDate;
        this.confNo = confNo;
        this.fromDateTime = confNo.getFromDateTime();
        this.toDateTime = confNo.getToDateTime();
    }

    public int getRid() {
        return rid;
    }

    public Vehicle getVid() {
        return vLicense;
    }

    public CustomerModel getCellPhone() {
        return cellPhone;
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

    public ReservationModel getConfNo() {
        return confNo;
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
        RentalModel that = (RentalModel) o;
        return rid == that.rid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rid);
    }
}
