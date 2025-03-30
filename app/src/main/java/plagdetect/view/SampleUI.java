package plagdetect.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SampleUI extends JFrame {
    public SampleUI(List<String[]> userData) {
        setTitle("User Data");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Name", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (String[] row : userData) {
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
