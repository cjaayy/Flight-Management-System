import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;

public class Main extends JFrame {

    private static final String ALL = "All";
    private static final String CONF = "Confirmed";
    private static final String UNCONF = "Unconfirmed";
    private static final String DATE_PLACEHOLDER = "YYYY-MM-DD";
    private static final String TIME_PLACEHOLDER = "HH:MM";
    private static final Pattern FLIGHT_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]*$");
    private static final Pattern LETTERS_ONLY_PATTERN = Pattern.compile("^[A-Za-z]*$");

    private final JTable flightTable;
    private final DefaultTableModel tableModel;
    private JComboBox<String> cbStatus;
    private JComboBox<String> cbAircraft;
    private JTextField txtSearchFlight;
    private JTextField txtFrom;
    private JTextField txtTo;
    private JFormattedTextField txtDate;
    private JFormattedTextField txtTime;
    private final JLabel lblCount;

    public Main() {
        setTitle("Flight Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // 1. TOP AREA - Header + Filters
        JPanel topPanel = new JPanel(new GridLayout(1, 7, 0, 0));
        topPanel.setBackground(new Color(245, 247, 249));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

        JPanel searchGroup = createFilterGroupPanel();
        searchGroup.setLayout(new BoxLayout(searchGroup, BoxLayout.Y_AXIS));
        JLabel searchLabel = createStyledLabel("FLIGHT ID:", labelFont);
        searchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchGroup.add(searchLabel);
        searchGroup.add(Box.createVerticalStrut(4));
        txtSearchFlight = new JTextField(10);
        txtSearchFlight.setHorizontalAlignment(SwingConstants.CENTER);
        txtSearchFlight.setMaximumSize(txtSearchFlight.getPreferredSize());
        addFlightIdFilter(txtSearchFlight);
        txtSearchFlight.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchGroup.add(txtSearchFlight);
        searchGroup.add(Box.createVerticalStrut(4));
        JButton clearFiltersBtn = new JButton("CLEAR");
        clearFiltersBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearFiltersBtn.setBackground(new Color(230, 235, 242));
        clearFiltersBtn.setForeground(new Color(40, 40, 40));
        clearFiltersBtn.setFocusPainted(false);
        clearFiltersBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearFiltersBtn.addActionListener(e -> clearFilters());
        clearFiltersBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearFiltersBtn.setMaximumSize(clearFiltersBtn.getPreferredSize());
        searchGroup.add(clearFiltersBtn);
        topPanel.add(searchGroup);

        JPanel fromGroup = createFilterGroupPanel();
        fromGroup.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 205, 212)));
        fromGroup.setLayout(new BoxLayout(fromGroup, BoxLayout.Y_AXIS));
        JLabel fromLabel = createStyledLabel("FROM:", labelFont);
        fromLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fromGroup.add(fromLabel);
        fromGroup.add(Box.createVerticalStrut(4));
        txtFrom = new JTextField(10);
        txtFrom.setHorizontalAlignment(SwingConstants.CENTER);
        txtFrom.setMaximumSize(txtFrom.getPreferredSize());
        addLettersOnlyFilter(txtFrom, "From");
        txtFrom.setAlignmentX(Component.CENTER_ALIGNMENT);
        fromGroup.add(txtFrom);
        fromGroup.add(Box.createVerticalStrut(4));
        JButton fromClearBtn = createClearFieldButton(txtFrom);
        fromClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        fromClearBtn.setMaximumSize(fromClearBtn.getPreferredSize());
        fromGroup.add(fromClearBtn);
        topPanel.add(fromGroup);

        JPanel toGroup = createFilterGroupPanel();
        toGroup.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 205, 212)));
        toGroup.setLayout(new BoxLayout(toGroup, BoxLayout.Y_AXIS));
        JLabel toLabel = createStyledLabel("TO:", labelFont);
        toLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toGroup.add(toLabel);
        toGroup.add(Box.createVerticalStrut(4));
        txtTo = new JTextField(10);
        txtTo.setHorizontalAlignment(SwingConstants.CENTER);
        txtTo.setMaximumSize(txtTo.getPreferredSize());
        addLettersOnlyFilter(txtTo, "To");
        txtTo.setAlignmentX(Component.CENTER_ALIGNMENT);
        toGroup.add(txtTo);
        toGroup.add(Box.createVerticalStrut(4));
        JButton toClearBtn = createClearFieldButton(txtTo);
        toClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toClearBtn.setMaximumSize(toClearBtn.getPreferredSize());
        toGroup.add(toClearBtn);
        topPanel.add(toGroup);

        JPanel dateGroup = createFilterGroupPanel();
        dateGroup.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 205, 212)));
        dateGroup.setLayout(new BoxLayout(dateGroup, BoxLayout.Y_AXIS));
        JLabel dateLabel = createStyledLabel("DATE:", labelFont);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateGroup.add(dateLabel);
        dateGroup.add(Box.createVerticalStrut(4));
        txtDate = createMaskedField("####-##-##", DATE_PLACEHOLDER, 10, "-", new int[] { 4, 7, 10 },
                this::showDateError);
        txtDate.setHorizontalAlignment(SwingConstants.CENTER);
        txtDate.setMaximumSize(txtDate.getPreferredSize());
        txtDate.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateGroup.add(txtDate);
        dateGroup.add(Box.createVerticalStrut(4));
        JButton dateClearBtn = createClearFieldButton(txtDate);
        dateClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateClearBtn.setMaximumSize(dateClearBtn.getPreferredSize());
        dateGroup.add(dateClearBtn);
        topPanel.add(dateGroup);

        JPanel timeGroup = createFilterGroupPanel();
        timeGroup.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 205, 212)));
        timeGroup.setLayout(new BoxLayout(timeGroup, BoxLayout.Y_AXIS));
        JLabel timeLabel = createStyledLabel("TIME:", labelFont);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeGroup.add(timeLabel);
        timeGroup.add(Box.createVerticalStrut(4));
        txtTime = createMaskedField("##:##", TIME_PLACEHOLDER, 10, ":", new int[] { 2, 5 }, this::showTimeError);
        txtTime.setHorizontalAlignment(SwingConstants.CENTER);
        txtTime.setMaximumSize(txtTime.getPreferredSize());
        txtTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeGroup.add(txtTime);
        timeGroup.add(Box.createVerticalStrut(4));
        JButton timeClearBtn = createClearFieldButton(txtTime);
        timeClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeClearBtn.setMaximumSize(timeClearBtn.getPreferredSize());
        timeGroup.add(timeClearBtn);
        topPanel.add(timeGroup);

        JPanel aircraftGroup = createFilterGroupPanel();
        aircraftGroup.setLayout(new BoxLayout(aircraftGroup, BoxLayout.Y_AXIS));
        JLabel aircraftLabel = createStyledLabel("AIRCRAFT:", labelFont);
        aircraftLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        aircraftGroup.add(aircraftLabel);
        aircraftGroup.add(Box.createVerticalStrut(4));
        cbAircraft = new JComboBox<>(new String[] { ALL });
        cbAircraft.setPrototypeDisplayValue("Airbus A350-900");
        Dimension aircraftSize = cbAircraft.getPreferredSize();
        cbAircraft.setPreferredSize(new Dimension(Math.max(160, aircraftSize.width), aircraftSize.height));
        cbAircraft.setMinimumSize(cbAircraft.getPreferredSize());
        cbAircraft.setMaximumSize(cbAircraft.getPreferredSize());
        cbAircraft.setAlignmentX(Component.CENTER_ALIGNMENT);
        aircraftGroup.add(cbAircraft);
        topPanel.add(aircraftGroup);

        JPanel statusGroup = createFilterGroupPanel();
        statusGroup.setLayout(new BoxLayout(statusGroup, BoxLayout.Y_AXIS));
        JLabel statusLabel = createStyledLabel("STATUS:", labelFont);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusGroup.add(statusLabel);
        statusGroup.add(Box.createVerticalStrut(4));
        cbStatus = new JComboBox<>(new String[] { ALL, CONF, UNCONF });
        cbStatus.setMaximumSize(cbStatus.getPreferredSize());
        cbStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusGroup.add(cbStatus);
        topPanel.add(statusGroup);

        String[] columns = { "Date", "Time", "From", "To", "Flight ID", "Aircraft", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightTable.setRowHeight(30);
        flightTable.setIntercellSpacing(new Dimension(1, 1));
        flightTable.setGridColor(new Color(200, 205, 212));
        flightTable.setShowVerticalLines(true);
        flightTable.setShowHorizontalLines(true);

        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < flightTable.getColumnCount(); i++) {
            flightTable.getColumnModel().getColumn(i).setCellRenderer(centeredRenderer);
        }

        JTableHeader tableHeader = flightTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(245, 247, 249));
        tableHeader.setForeground(new Color(60, 60, 60));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        TableCellRenderer baseHeaderRenderer = tableHeader.getDefaultRenderer();
        tableHeader.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            Component c = baseHeaderRenderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (c instanceof JComponent jc) {
                int right = column == table.getColumnCount() - 1 ? 0 : 1;
                jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, right,
                        new Color(200, 205, 212)));
                if (jc instanceof JLabel jl) {
                    jl.setHorizontalAlignment(SwingConstants.CENTER);
                }
            }
            return c;
        });

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
        addLiveFilter(txtDate);
        addLiveFilter(txtTime);
        cbAircraft.addActionListener(e -> applyFilters());
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
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JFormattedTextField createMaskedField(String mask, String placeholder, int columns,
            String allowedLiterals, int[] segmentEnds, Runnable invalidAction) {
        MaskFormatter formatter;
        try {
            formatter = new MaskFormatter(mask);
        } catch (ParseException e) {
            throw new IllegalStateException("Invalid mask: " + mask, e);
        }
        formatter.setPlaceholderCharacter(' ');
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);

        JFormattedTextField field = new HintFormattedTextField(formatter, placeholder);
        field.setColumns(columns);
        field.setFocusLostBehavior(JFormattedTextField.PERSIST);

        boolean[] adjusting = new boolean[] { false };

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                enforceSegmentInput(field, segmentEnds, adjusting);
            }
        });

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                enforceSegmentInput(field, segmentEnds, adjusting);
            }
        });

        field.addCaretListener(e -> enforceSegmentInput(field, segmentEnds, adjusting));

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || Character.isISOControl(c)) {
                    return;
                }
                if (allowedLiterals != null && allowedLiterals.indexOf(c) >= 0) {
                    return;
                }
                e.consume();
                if (invalidAction != null) {
                    invalidAction.run();
                }
            }
        });

        return field;
    }

    private boolean hasAnyDigit(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void enforceSegmentInput(JFormattedTextField field, int[] segmentEnds, boolean[] adjusting) {
        if (field == null || adjusting[0]) {
            return;
        }
        String text = field.getText();
        if (text == null) {
            return;
        }

        int target = -1;
        if (segmentEnds != null && segmentEnds.length > 0) {
            for (int end : segmentEnds) {
                int blank = findFirstBlank(text, 0, Math.min(end, text.length()));
                if (blank >= 0) {
                    target = blank;
                    break;
                }
            }
        }

        if (target < 0) {
            return;
        }

        int caretPos = field.getCaretPosition();
        if (caretPos <= target) {
            return;
        }

        int moveTo = target;
        adjusting[0] = true;
        SwingUtilities.invokeLater(() -> {
            field.setCaretPosition(moveTo);
            adjusting[0] = false;
        });
    }

    private int findFirstBlank(String text, int start, int end) {
        int limit = Math.min(end, text.length());
        for (int i = Math.max(0, start); i < limit; i++) {
            if (text.charAt(i) == ' ') {
                return i;
            }
        }
        return -1;
    }

    private final class HintFormattedTextField extends JFormattedTextField {
        private final String placeholder;

        private HintFormattedTextField(AbstractFormatter formatter, String placeholder) {
            super(formatter);
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            boolean showPlaceholder = placeholder != null && !hasAnyDigit(getText());
            if (!showPlaceholder) {
                super.paintComponent(g);
                return;
            }

            Color originalForeground = getForeground();
            setForeground(getBackground());
            super.paintComponent(g);
            setForeground(originalForeground);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(140, 140, 140));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(placeholder);
            int availableWidth = getWidth() - insets.left - insets.right;
            int x = insets.left + Math.max(0, (availableWidth - textWidth) / 2);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
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

    private void addFlightIdFilter(JTextField field) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) {
                    return;
                }
                String newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()))
                        .insert(offset, string)
                        .toString();
                if (FLIGHT_ID_PATTERN.matcher(newText).matches()) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    showFlightIdError();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String replacement = text == null ? "" : text;
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = new StringBuilder(current)
                        .replace(offset, offset + length, replacement)
                        .toString();
                if (FLIGHT_ID_PATTERN.matcher(newText).matches()) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    showFlightIdError();
                }
            }
        });
    }

    private void addLettersOnlyFilter(JTextField field, String fieldName) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) {
                    return;
                }
                String newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()))
                        .insert(offset, string)
                        .toString();
                if (LETTERS_ONLY_PATTERN.matcher(newText).matches()) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    showLettersOnlyError(fieldName);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String replacement = text == null ? "" : text;
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = new StringBuilder(current)
                        .replace(offset, offset + length, replacement)
                        .toString();
                if (LETTERS_ONLY_PATTERN.matcher(newText).matches()) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    showLettersOnlyError(fieldName);
                }
            }
        });
    }

    private String getMaskedFilterText(JFormattedTextField field) {
        if (field == null) {
            return "";
        }
        String text = field.getText();
        if (!hasAnyDigit(text)) {
            return "";
        }

        String cleaned = text.replace(" ", "");
        cleaned = cleaned.replaceAll("[-:]+$", "");
        return cleaned.trim();
    }

    private void showFlightIdError() {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this,
                "Only letters and numbers are allowed for Flight ID.",
                "Invalid Flight ID",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showLettersOnlyError(String fieldName) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this,
                "Only letters are allowed for " + fieldName + ".",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showDateError() {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this,
                "Only numbers and dashes are allowed for Date.",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showTimeError() {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this,
                "Only numbers and colons are allowed for Time.",
                "Invalid Time",
                JOptionPane.WARNING_MESSAGE);
    }

    private JPanel createFilterGroupPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 247, 249));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 212)));
        return panel;
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
            if (field == null) {
                applyFilters();
                return;
            }
            if (field instanceof JFormattedTextField formatted) {
                formatted.setValue(null);
            } else {
                field.setText("");
            }
            applyFilters();
        });
        return button;
    }

    private void clearFilters() {
        txtSearchFlight.setText("");
        txtFrom.setText("");
        txtTo.setText("");
        txtDate.setValue(null);
        txtTime.setValue(null);
        cbAircraft.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        applyFilters();
    }

    private void loadFlightsFromDatabase() {
        tableModel.setRowCount(0);

        String sql = "SELECT flight_date, flight_time, origin, destination, flight_number, aircraft, status "
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
                String aircraft = rs.getString("aircraft");
                String currentStatus = rs.getString("status");

                tableModel.addRow(new Object[] {
                        date != null ? date.toString() : "",
                        time != null ? time.toString() : "",
                        fromLoc,
                        toLoc,
                        flightNum,
                        aircraft,
                        currentStatus
                });
            }

            updateTotalCount(tableModel.getRowCount());
            updateAircraftFilterOptions();
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void updateAircraftFilterOptions() {
        if (cbAircraft == null) {
            return;
        }

        Object selected = cbAircraft.getSelectedItem();
        Set<String> aircraftSet = new TreeSet<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object value = tableModel.getValueAt(row, 5);
            if (value == null) {
                continue;
            }
            String aircraft = value.toString().trim();
            if (!aircraft.isEmpty()) {
                aircraftSet.add(aircraft);
            }
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(ALL);
        for (String aircraft : aircraftSet) {
            model.addElement(aircraft);
        }
        cbAircraft.setModel(model);

        if (selected != null && aircraftSet.contains(selected.toString())) {
            cbAircraft.setSelectedItem(selected.toString());
        } else {
            cbAircraft.setSelectedIndex(0);
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
        String dateText = getMaskedFilterText(txtDate);
        if (!dateText.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(dateText), 0));
        String timeText = getMaskedFilterText(txtTime);
        if (!timeText.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(timeText), 1));
        if (cbAircraft.getSelectedIndex() > 0) {
            String aircraftValue = cbAircraft.getSelectedItem().toString();
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(aircraftValue) + "$", 5));
        }
        if (cbStatus.getSelectedIndex() > 0)
            filters.add(RowFilter.regexFilter("^" + cbStatus.getSelectedItem() + "$", 6));

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
