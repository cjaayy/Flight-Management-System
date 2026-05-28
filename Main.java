import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Main extends JFrame {
    
    private static final String PH = "Philippines";
    private static final String GER = "Germany";
    private static final String AUS = "Australia";
    private static final String ALL = "All";
    private static final String CONF = "Confirmed";
    private static final String UNCONF = "Unconfirmed";

    private final JTable flightTable;
    private final DefaultTableModel tableModel;
    private JComboBox<String> cbFrom, cbTo, cbStatus;
    private JTextField txtSearchFlight;
    private final JLabel lblCount;

    public Main() {
        setTitle("Flight Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // 1. TOP PANEL - Middle Controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        topPanel.setBackground(new Color(245, 247, 249));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        
        topPanel.add(createStyledLabel("SEARCH FLIGHT #:", labelFont));
        txtSearchFlight = new JTextField(10);
        topPanel.add(txtSearchFlight);

        topPanel.add(createStyledLabel("FROM:", labelFont));
        cbFrom = new JComboBox<>(new String[]{ALL, PH, GER, AUS});
        topPanel.add(cbFrom);

        topPanel.add(createStyledLabel("TO:", labelFont));
        cbTo = new JComboBox<>(new String[]{ALL, PH, GER, AUS});
        topPanel.add(cbTo);

        topPanel.add(createStyledLabel("STATUS:", labelFont));
        cbStatus = new JComboBox<>(new String[]{ALL, CONF, UNCONF});
        topPanel.add(cbStatus);

        JButton applyBtn = new JButton("APPLY");
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyBtn.setBackground(new Color(41, 128, 185));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(applyBtn);

        // 2. CENTER PANEL - Table
        String[] columns = {"Date", "Time", "From", "To", "Flight Number", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        flightTable = new JTable(tableModel);
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightTable.setRowHeight(30);
        flightTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // 3. BOTTOM PANEL - Records Found
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        bottomPanel.setBackground(Color.WHITE);
        lblCount = new JLabel("Total Records: 50");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        bottomPanel.add(lblCount);

        generate50Records();

        // Button Action
        applyBtn.addActionListener(e -> applyFilters());
        txtSearchFlight.addActionListener(e -> applyFilters());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private void generate50Records() {
        String[] locations = {PH, GER, AUS};
        String[] statusOptions = {CONF, UNCONF};
        Random rand = new Random();

        for (int i = 1; i <= 50; i++) {
            String date = "2026-05-" + String.format("%02d", (rand.nextInt(31)) + 1);
            String time = String.format("%02d:%02d", rand.nextInt(24), rand.nextInt(60));
            String fromLoc = locations[rand.nextInt(locations.length)];
            String toLoc;
            do { toLoc = locations[rand.nextInt(locations.length)]; } while (fromLoc.equals(toLoc));
            String flightNum = "FL-" + (700 + i);
            String currentStatus = statusOptions[rand.nextInt(statusOptions.length)];

            tableModel.addRow(new Object[]{date, time, fromLoc, toLoc, flightNum, currentStatus});
        }
    }

    private void applyFilters() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        flightTable.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Search Filter
        String searchText = txtSearchFlight.getText().trim();
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 4));
        }

        // Dropdown Filters
        if (cbFrom.getSelectedIndex() > 0) 
            filters.add(RowFilter.regexFilter("^" + cbFrom.getSelectedItem() + "$", 2));
        if (cbTo.getSelectedIndex() > 0) 
            filters.add(RowFilter.regexFilter("^" + cbTo.getSelectedItem() + "$", 3));
        if (cbStatus.getSelectedIndex() > 0) 
            filters.add(RowFilter.regexFilter("^" + cbStatus.getSelectedItem() + "$", 5));

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        // CHECK IF DATA EXISTS
        int count = sorter.getViewRowCount();
        if (count == 0) {
            // LALABAS ANG ERROR MESSAGE KAPAG WALANG DATA
            JOptionPane.showMessageDialog(this, 
                "No flight found with the given criteria.", 
                "Search Result", 
                JOptionPane.WARNING_MESSAGE);
            
            // Optional: I-reset ang filters para ipakita ulit ang data
            sorter.setRowFilter(null);
            count = tableModel.getRowCount();
        }

        lblCount.setText(count + " flight(s) found.");
        lblCount.setForeground(new Color(40, 40, 40));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Ignore and fall back to default Look & Feel.
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

