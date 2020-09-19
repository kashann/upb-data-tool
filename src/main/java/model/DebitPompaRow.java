package model;

public class DebitPompaRow {
    private int period;
    private double electrical;
    private double thermal;

    public DebitPompaRow() {
    }

    public DebitPompaRow(int period, double electrical, double thermal) {
        this.period = period;
        this.electrical = electrical;
        this.thermal = thermal;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double getElectrical() {
        return electrical;
    }

    public void setElectrical(double electrical) {
        this.electrical = electrical;
    }

    public double getThermal() {
        return thermal;
    }

    public void setThermal(double thermal) {
        this.thermal = thermal;
    }
}
