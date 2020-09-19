package view;

import model.ETypes;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ViewClass {
    public File file = null;
    public List<String> allLines;
    public JFileChooser fileChooser;
    public JFrame f, filePickerWindow;
    public JPanel buttonPanel;
    public JButton openButton, exportButton;
    public JComboBox dropdown;

    public ViewClass() {
        f = new JFrame("UPB Convertor Date");

        openButton = new JButton("Alege fișier...");

        dropdown = new JComboBox(ETypes.values());

        exportButton = new JButton("Exportă Excel");

        buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(dropdown);
        buttonPanel.add(exportButton);

        f.add(buttonPanel, BorderLayout.PAGE_START);

        filePickerWindow = new JFrame("Choose file");
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("OUT files", "out");
        fileChooser.setFileFilter(filter);

        f.pack();
        f.setVisible(true);
    }
}
