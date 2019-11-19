package ca.ubc.cs304.model;

import java.util.Objects;

public class VehicleType {
    private final String vtName;
    private final String features;
    private final int wRate;
    private final int dRate;
    private final int hRate;
    private final int wiRate;
    private final int diRate;
    private final int hiRate;
    private final int kRate;

    public VehicleType(String vtName, String features, int wRate, int dRate, int hRate, int wiRate, int diRate,
                       int hiRate, int kRate) {
        this.vtName = vtName;
        this.features = features;
        this.wRate = wRate;
        this.dRate = dRate;
        this.hRate = hRate;
        this.wiRate = wiRate;
        this.diRate = diRate;
        this.hiRate = hiRate;
        this.kRate = kRate;
    }

    public String getVtName() {
        return vtName;
    }

    public String getFeatures() {
        return features;
    }

    public int getwRate() {
        return wRate;
    }

    public int getdRate() {
        return dRate;
    }

    public int gethRate() {
        return hRate;
    }

    public int getWiRate() {
        return wiRate;
    }

    public int getDiRate() {
        return diRate;
    }

    public int getHiRate() {
        return hiRate;
    }

    public int getkRate() {
        return kRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleType that = (VehicleType) o;
        return vtName.equals(that.vtName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vtName);
    }
}
