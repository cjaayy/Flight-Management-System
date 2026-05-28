import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Main extends JFrame {

    private static final String ALL = "All";
    private static final String CONF = "Confirmed";
    private static final String UNCONF = "Unconfirmed";

    private final JTable flightTable;
    private final DefaultTableModel tableModel;
    private JComboBox<String> cbStatus;
    private JTextField txtSearchFlight;
    private JTextField txtFrom;
    private JTextField txtTo;
    private final JLabel lblCount;

    public Main() {
        setTitle("Flight Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // 1. TOP AREA - Header + Filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        topPanel.setBackground(new Color(245, 247, 249));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

        topPanel.add(createStyledLabel("SEARCH FLIGHT #:", labelFont));
        txtSearchFlight = new JTextField(10);
        topPanel.add(txtSearchFlight);
        JButton clearFiltersBtn = new JButton("CLEAR");
        clearFiltersBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearFiltersBtn.setBackground(new Color(230, 235, 242));
        clearFiltersBtn.setForeground(new Color(40, 40, 40));
        clearFiltersBtn.setFocusPainted(false);
        clearFiltersBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearFiltersBtn.addActionListener(e -> clearFilters());
        topPanel.add(clearFiltersBtn);

        topPanel.add(createStyledLabel("FROM:", labelFont));
        txtFrom = new JTextField(10);
        topPanel.add(txtFrom);
        topPanel.add(createClearFieldButton(txtFrom));

        topPanel.add(createStyledLabel("TO:", labelFont));
        txtTo = new JTextField(10);
        topPanel.add(txtTo);
        topPanel.add(createClearFieldButton(txtTo));

        topPanel.add(createStyledLabel("STATUS:", labelFont));
        cbStatus = new JComboBox<>(new String[] { ALL, CONF, UNCONF });
        topPanel.add(cbStatus);

        // 2. CENTER PANEL - Table
        String[] columns = { "Date", "Time", "From", "To", "Flight Number", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        lblCount = new JLabel("Total Records: 0");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        bottomPanel.add(lblCount);

        loadFlightsFromDatabase();

        // Live Filters
        addLiveFilter(txtSearchFlight);
        addLiveFilter(txtFrom);
        addLiveFilter(txtTo);
        cbStatus.addActionListener(e -> applyFilters());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JComponent createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(new Color(214, 233, 248));
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel logo = createHeaderIconLabel();

        JLabel building = createHeaderBuildingLabel();

        header.add(logo, BorderLayout.WEST);
        header.add(building, BorderLayout.EAST);
        return header;
    }

    private JLabel createHeaderIconLabel() {
        String iconPath = "assets/icons/airplane.png";
        ImageIcon icon = new ImageIcon(iconPath);
        JLabel label = new JLabel();
        label.setOpaque(false);

        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            int targetHeight = 48;
            int targetWidth = (int) Math.round((double) icon.getIconWidth() / icon.getIconHeight()
                    * targetHeight);
            Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        }

        label.setPreferredSize(new Dimension(220, 60));
        return label;
    }

    private JLabel createHeaderBuildingLabel() {
        String iconPath = "assets/icons/new-york.png";
        ImageIcon icon = new ImageIcon(iconPath);
        JLabel label = new JLabel();
        label.setOpaque(false);

        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            int targetHeight = 48;
            int targetWidth = (int) Math.round((double) icon.getIconWidth() / icon.getIconHeight()
                    * targetHeight);
            Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        }

        label.setPreferredSize(new Dimension(140, 60));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private void addLiveFilter(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });
    }

    private JButton createClearFieldButton(JTextField field) {
        JButton button = new JButton("CLEAR");
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(new Color(230, 235, 242));
        button.setForeground(new Color(40, 40, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(2, 6, 2, 6));
        button.addActionListener(e -> {
            field.setText("");
            applyFilters();
        });
        return button;
    }

    private void clearFilters() {
        txtSearchFlight.setText("");
        txtFrom.setText("");
        txtTo.setText("");
        cbStatus.setSelectedIndex(0);
        applyFilters();
    }

    private void loadFlightsFromDatabase() {
        tableModel.setRowCount(0);

        String sql = "SELECT flight_date, flight_time, origin, destination, flight_number, status "
                + "FROM flights ORDER BY flight_date, flight_time";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Date date = rs.getDate("flight_date");
                Time time = rs.getTime("flight_time");
                String fromLoc = rs.getString("origin");
                String toLoc = rs.getString("destination");
                String flightNum = rs.getString("flight_number");
                String currentStatus = rs.getString("status");

                tableModel.addRow(new Object[] {
                        date != null ? date.toString() : "",
                        time != null ? time.toString() : "",
                        fromLoc,
                        toLoc,
                        flightNum,
                        currentStatus
                });
            }

            updateTotalCount(tableModel.getRowCount());
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void updateTotalCount(int count) {
        lblCount.setText("Total Records: " + count);
        lblCount.setForeground(new Color(40, 40, 40));
    }

    private void showDatabaseError(SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage() + "\nRun schema.sql and update DatabaseConfig.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
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

        // Input Filters
        String fromText = txtFrom.getText().trim();
        if (!fromText.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(fromText), 2));
        String toText = txtTo.getText().trim();
        if (!toText.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(toText), 3));
        if (cbStatus.getSelectedIndex() > 0)
            filters.add(RowFilter.regexFilter("^" + cbStatus.getSelectedItem() + "$", 5));

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        int count = sorter.getViewRowCount();
        lblCount.setText(count + " flight(s) found.");
        lblCount.setForeground(new Color(40, 40, 40));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            // Ignore and fall back to default Look & Feel.
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

}
