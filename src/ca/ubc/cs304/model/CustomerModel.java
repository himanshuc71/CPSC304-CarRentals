package ca.ubc.cs304.model;

import java.util.Objects;

public class CustomerModel {
    private final long cellphone;
    private final String cname;
    private final String address;
    private final long dLicense;

    public CustomerModel(long cellphone, String cname, String address, long dLicense ){
        this.cellphone = cellphone;
        this.cname = cname;
        this.address = address;
        this.dLicense = dLicense;
    }

    public long getCellphone() {
        return cellphone;
    }

    public long getdLicense() {
        return dLicense;
    }

    public String getAddress() {
        return address;
    }

    public String getCname() {
        return cname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerModel that = (CustomerModel) o;
        return dLicense == that.dLicense;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dLicense);
    }

    @Override
    public String toString() {
        return "Customer name: " + this.cname + " Cellphone no: " + (this.cellphone) +
                " Driver's License: " + (this.dLicense) + " Address: " + this.address;
    }
}
