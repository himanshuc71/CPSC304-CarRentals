package ca.ubc.cs304.model;

import java.util.Objects;

public class CustomerModel {
    private final int cellphone;
    private final String cname;
    private final String address;
    private final int dLicense;

    public CustomerModel(int cellphone, String cname, String address, int dLicense ){
        this.cellphone = cellphone;
        this.cname = cname;
        this.address = address;
        this.dLicense = dLicense;
    }

    public int getCellphone() {
        return cellphone;
    }

    public int getdLicense() {
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
        return "Customer name: " + this.cname + " Cellphone no: " + Integer.toString(this.cellphone) +
                " Driver's License: " + Integer.toString(this.dLicense) + " Address: " + this.address;
    }
}
