package presenter;

import model.DebitPompaRow;
import model.DebitPompaRun;
import model.ETypes;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import view.ViewClass;

import javax.swing.*;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PresenterClass {
    public ViewClass view;
    public DebitPompaRun run;

    public PresenterClass(ViewClass view) {
        this.view = view;
        addActionListners();
        run = new DebitPompaRun();
    }

    public ViewClass getView() {
        return view;
    }

    public void setView(ViewClass view) {
        this.view = view;
    }

    public DebitPompaRun getDebitPompaRun() {
        return run;
    }

    public void setDebitPompaRun(DebitPompaRun run) {
        this.run = run;
    }

    public void addActionListners() {
        view.openButton.addActionListener(e -> {
            int returnVal = view.fileChooser.showOpenDialog(view.openButton);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                view.file = view.fileChooser.getSelectedFile();
                System.out.println("You chose to open this file: " + view.file.getName());

                try {
                    Path path = Paths.get(view.file.getPath());
                    view.allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    for(int i = 0; i < view.allLines.size(); i++) {
                        if(i > 1 && i < 367) {
                            String[] data = view.allLines.get(i)
                                    .replaceAll(" ","")
                                    .replaceAll("\\+","")
                                    .split("\t");
                            int period = Integer.parseInt(data[0]);
                            double electrical = Double.parseDouble(data[1]);
                            double thermal = Double.parseDouble(data[2]);
                            DebitPompaRow dpr = new DebitPompaRow(period, electrical, thermal);
                            run.addRow(dpr);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });
        view.exportButton.addActionListener(e -> {
            if(view.file != null) {
                try {
                    switch ((ETypes)view.dropdown.getSelectedItem()) {
                        case DEBIT_POMPA:
                            XSSFWorkbook workbook = new XSSFWorkbook();
                            XSSFSheet sheet = workbook.createSheet(view.file.getName());

                            XSSFRow rowtitle = sheet.createRow(0);
                            rowtitle.createCell(0).setCellValue(view.file.getName());

                            XSSFRow rowhead = sheet.createRow(1);
                            rowhead.createCell(0).setCellValue("Period");
                            rowhead.createCell(1).setCellValue("Electrical");
                            rowhead.createCell(2).setCellValue("Thermal");
                            rowhead.createCell(10).setCellValue("Day");
                            rowhead.createCell(11).setCellValue("q_EL [kWh]");
                            rowhead.createCell(12).setCellValue("q_TH [kWh]");

                            for(int i = 0; i < run.getRows().size(); i++) {
                                XSSFRow row = sheet.createRow(i + 2);
                                row.createCell(0, CellType.NUMERIC).setCellValue(run.getRows().get(i).getPeriod());
                                row.createCell(1, CellType.NUMERIC).setCellValue(run.getRows().get(i).getElectrical());
                                row.createCell(2, CellType.NUMERIC).setCellValue(run.getRows().get(i).getThermal());
                                row.createCell(10, CellType.FORMULA).setCellFormula("A" + (i + 3));
                                row.createCell(11, CellType.FORMULA).setCellFormula("B" + (i + 3) + "/1000");
                                row.createCell(12, CellType.FORMULA).setCellFormula("C" + (i + 3) + "/1000");
                            }

                            sheet.getRow(1).createCell(16).setCellValue("Values");
                            sheet.getRow(2).createCell(15).setCellValue("Annual Total Electrical Energy [kWh]");
                            sheet.getRow(2).createCell(16).setCellFormula("SUM(L3:L367)");
                            sheet.getRow(3).createCell(15).setCellValue("Annual Total Thermal Energy [kWh]");
                            sheet.getRow(3).createCell(16).setCellFormula("SUM(M3:M367)");
                            sheet.getRow(4).createCell(15).setCellValue("Daily Average Electrical [kWh]");
                            sheet.getRow(4).createCell(16).setCellFormula("AVERAGE(L3:L367)");
                            sheet.getRow(5).createCell(15).setCellValue("Daily Average Thermal [kWh]");
                            sheet.getRow(5).createCell(16).setCellFormula("AVERAGE(M3:M367)");

                            for(int i = 0; i < 20; i++) {
                                sheet.autoSizeColumn(i);
                            }

                            XSSFDrawing drawing = sheet.createDrawingPatriarch();
                            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 15, 10, 25, 30);

                            XSSFChart chart = drawing.createChart(anchor);
                            chart.setTitleText(view.file.getName());
                            chart.setTitleOverlay(false);

                            XDDFChartLegend legend = chart.getOrAddLegend();
                            legend.setPosition(LegendPosition.TOP_RIGHT);

                            XDDFCategoryAxis leftAxis = chart.createCategoryAxis(AxisPosition.LEFT);
                            leftAxis.setTitle("Days");
                            XDDFValueAxis bottomAxis = chart.createValueAxis(AxisPosition.BOTTOM);
                            bottomAxis.setTitle("Energy [kWh]");
                            bottomAxis.getOrAddMajorGridProperties();

                            XDDFDataSource<String> days = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                                    new CellRangeAddress(2, 366, 10, 10));

                            XDDFNumericalDataSource<Double> electrical = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                                    new CellRangeAddress(2, 366, 11, 11));

                            XDDFNumericalDataSource<Double> thermal = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                                    new CellRangeAddress(2, 366, 12, 12));

                            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, leftAxis, bottomAxis);

                            XDDFLineChartData.Series q_el = (XDDFLineChartData.Series) data.addSeries(days, electrical);
                            q_el.setTitle("q_EL [kWh]", null);
                            q_el.setSmooth(false);
                            q_el.setMarkerStyle(MarkerStyle.NONE);

                            XDDFLineChartData.Series q_th = (XDDFLineChartData.Series) data.addSeries(days, thermal);
                            q_th.setTitle("q_TH [kWh]", null);
                            q_th.setSmooth(false);
                            q_th.setMarkerStyle(MarkerStyle.NONE);

                            chart.plot(data);

                            FileOutputStream fileOut = new FileOutputStream("Rezultate" + view.dropdown.getSelectedItem() + ".xlsx");
                            workbook.write(fileOut);
                            fileOut.close();
                            workbook.close();
                            System.out.println("Fisier EXCEL exportat cu succes!");
                            JOptionPane.showMessageDialog(null, "Succes!");
                            break;
                        case ceva:
                            JOptionPane.showMessageDialog(view.exportButton, "Not implemented yet!");
                            break;
                    }
                } catch ( Exception ex ) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
            else {
                JOptionPane.showMessageDialog(view.exportButton, "Niciun fișier ales!");
            }
        });
    }
}
