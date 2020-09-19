package model;

import java.util.ArrayList;

public class DebitPompaRun {
    private ArrayList<DebitPompaRow> rows = new ArrayList<>();

    public DebitPompaRun() {
    }

    public DebitPompaRun(ArrayList<DebitPompaRow> rows) {
        this.rows = rows;
    }

    public ArrayList<DebitPompaRow> getRows() {
        return rows;
    }

    public void setRows(ArrayList<DebitPompaRow> rows) {
        this.rows = rows;
    }

    public void addRow(DebitPompaRow newRow) {
        this.rows.add(newRow);
    }
}
