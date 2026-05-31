import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
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
    private static final String CANCEL = "Cancelled";
    private static final String DATE_PLACEHOLDER = "YYYY-MM-DD";
    private static final String TIME_PLACEHOLDER = "HH:MM";
    private static final Pattern FLIGHT_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]*$");
    private static final Color APP_BG = new Color(2, 6, 2);
    private static final Color APP_PANEL_BG = new Color(6, 10, 6);
    private static final Color APP_CARD_BG = new Color(6, 14, 6);
    private static final Color APP_GREEN = new Color(0, 255, 0);
    private static final Color APP_GREEN_SOFT = new Color(0, 120, 0);
    private static final Color APP_TEXT_MUTED = new Color(110, 160, 110);
    private static final Color APP_HEADER_TOP = new Color(10, 18, 10);
    private static final Color APP_HEADER_BOTTOM = new Color(0, 0, 0);
    private static final Color APP_ROW_ALT = new Color(4, 10, 4);

    private final JTable flightTable;
    private final DefaultTableModel tableModel;
    private JComboBox<String> cbStatus;
    private JComboBox<String> cbAircraft;
    private JTextField txtSearchFlight;
    private JComboBox<String> cbFrom;
    private JComboBox<String> cbTo;
    private JFormattedTextField txtDate;
    private JFormattedTextField txtTime;
    private final JLabel lblCount;
    private final JLabel lblConfirmedCount;
    private final JLabel lblUnconfirmedCount;
    private final JLabel lblCancelledCount;
    private JButton editBtn;
    private JButton aaBtn;
    private int lastSelectedModelRow = -1;

    public Main() {
        setTitle("Flight Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(APP_BG);
        setLayout(new BorderLayout(10, 10));

        // 1. TOP AREA - Header + Filters
        JPanel topPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        topPanel.setBackground(APP_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, APP_GREEN_SOFT),
                new EmptyBorder(10, 12, 10, 12)));

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
        styleFilterButton(clearFiltersBtn, 10, new Insets(1, 8, 1, 8));
        clearFiltersBtn.addActionListener(e -> clearFilters());
        clearFiltersBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearFiltersBtn.setMaximumSize(clearFiltersBtn.getPreferredSize());
        searchGroup.add(clearFiltersBtn);
        topPanel.add(searchGroup);

        JPanel fromGroup = createFilterGroupPanel();
        fromGroup.setLayout(new BoxLayout(fromGroup, BoxLayout.Y_AXIS));
        JLabel fromLabel = createStyledLabel("FROM:", labelFont);
        fromLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fromGroup.add(fromLabel);
        fromGroup.add(Box.createVerticalStrut(4));
        cbFrom = new JComboBox<>(new String[] { ALL });
        cbFrom.setPrototypeDisplayValue("International City");
        Dimension fromSize = cbFrom.getPreferredSize();
        cbFrom.setPreferredSize(new Dimension(Math.max(160, fromSize.width), fromSize.height));
        cbFrom.setMinimumSize(cbFrom.getPreferredSize());
        cbFrom.setMaximumSize(cbFrom.getPreferredSize());
        cbFrom.setAlignmentX(Component.CENTER_ALIGNMENT);
        fromGroup.add(cbFrom);
        fromGroup.add(Box.createVerticalStrut(4));
        JButton fromClearBtn = createClearComboButton(cbFrom);
        fromClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        fromClearBtn.setMaximumSize(fromClearBtn.getPreferredSize());
        fromGroup.add(fromClearBtn);
        fromGroup.add(Box.createVerticalStrut(6));
        topPanel.add(fromGroup);

        JPanel toGroup = createFilterGroupPanel();
        toGroup.setLayout(new BoxLayout(toGroup, BoxLayout.Y_AXIS));
        JLabel toLabel = createStyledLabel("TO:", labelFont);
        toLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toGroup.add(toLabel);
        toGroup.add(Box.createVerticalStrut(4));
        cbTo = new JComboBox<>(new String[] { ALL });
        cbTo.setPrototypeDisplayValue("International City");
        Dimension toSize = cbTo.getPreferredSize();
        cbTo.setPreferredSize(new Dimension(Math.max(160, toSize.width), toSize.height));
        cbTo.setMinimumSize(cbTo.getPreferredSize());
        cbTo.setMaximumSize(cbTo.getPreferredSize());
        cbTo.setAlignmentX(Component.CENTER_ALIGNMENT);
        toGroup.add(cbTo);
        toGroup.add(Box.createVerticalStrut(4));
        JButton toClearBtn = createClearComboButton(cbTo);
        toClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toClearBtn.setMaximumSize(toClearBtn.getPreferredSize());
        toGroup.add(toClearBtn);
        toGroup.add(Box.createVerticalStrut(6));
        topPanel.add(toGroup);

        JPanel dateGroup = createFilterGroupPanel();
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
        aircraftGroup.add(Box.createVerticalStrut(4));
        JButton aircraftClearBtn = createClearComboButton(cbAircraft);
        aircraftClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        aircraftClearBtn.setMaximumSize(aircraftClearBtn.getPreferredSize());
        aircraftGroup.add(aircraftClearBtn);
        aircraftGroup.add(Box.createVerticalStrut(6));
        topPanel.add(aircraftGroup);

        JPanel statusGroup = createFilterGroupPanel();
        statusGroup.setLayout(new BoxLayout(statusGroup, BoxLayout.Y_AXIS));
        JLabel statusLabel = createStyledLabel("STATUS:", labelFont);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusGroup.add(statusLabel);
        statusGroup.add(Box.createVerticalStrut(4));
        cbStatus = new JComboBox<>(new String[] { ALL, CONF, UNCONF, CANCEL });
        cbStatus.setMaximumSize(cbStatus.getPreferredSize());
        cbStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusGroup.add(cbStatus);
        statusGroup.add(Box.createVerticalStrut(4));
        JButton statusClearBtn = createClearComboButton(cbStatus);
        statusClearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusClearBtn.setMaximumSize(statusClearBtn.getPreferredSize());
        statusGroup.add(statusClearBtn);
        statusGroup.add(Box.createVerticalStrut(6));
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
        flightTable.setGridColor(APP_GREEN_SOFT);
        flightTable.setBackground(APP_BG);
        flightTable.setForeground(APP_GREEN);
        flightTable.setSelectionBackground(APP_GREEN_SOFT);
        flightTable.setSelectionForeground(APP_BG);
        flightTable.setShowVerticalLines(true);
        flightTable.setShowHorizontalLines(true);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setFillsViewportHeight(true);

        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? APP_BG : APP_ROW_ALT);
                    c.setForeground(APP_GREEN);
                } else {
                    c.setBackground(APP_GREEN_SOFT);
                    c.setForeground(APP_BG);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        for (int i = 0; i < flightTable.getColumnCount(); i++) {
            flightTable.getColumnModel().getColumn(i).setCellRenderer(centeredRenderer);
        }

        JTableHeader tableHeader = flightTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(APP_PANEL_BG);
        tableHeader.setForeground(APP_GREEN);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        tableHeader.setPreferredSize(new Dimension(0, 36));
        TableCellRenderer baseHeaderRenderer = tableHeader.getDefaultRenderer();
        tableHeader.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            Component c = baseHeaderRenderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (c instanceof JComponent jc) {
                int right = column == table.getColumnCount() - 1 ? 0 : 1;
                jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, right,
                        APP_GREEN_SOFT));
                if (jc instanceof JLabel jl) {
                    jl.setHorizontalAlignment(SwingConstants.CENTER);
                    jl.setForeground(APP_GREEN);
                }
            }
            return c;
        });

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(APP_GREEN_SOFT),
                new EmptyBorder(10, 20, 10, 20)));
        scrollPane.getViewport().setBackground(APP_BG);

        // 3. BOTTOM PANEL - Records Found
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        bottomPanel.setBackground(APP_BG);
        bottomPanel.setBorder(new EmptyBorder(6, 16, 10, 16));
        editBtn = createEditButton();
        aaBtn = createAABtn();
        Font countFont = new Font("Segoe UI", Font.ITALIC, 13);
        Color countColor = APP_GREEN;
        lblConfirmedCount = new JLabel("Confirmed: 0");
        lblConfirmedCount.setFont(countFont);
        lblConfirmedCount.setForeground(countColor);
        lblUnconfirmedCount = new JLabel("Unconfirmed: 0");
        lblUnconfirmedCount.setFont(countFont);
        lblUnconfirmedCount.setForeground(countColor);
        lblCancelledCount = new JLabel("Cancelled: 0");
        lblCancelledCount.setFont(countFont);
        lblCancelledCount.setForeground(countColor);
        lblCount = new JLabel("Total Records: 0");
        lblCount.setFont(countFont);
        lblCount.setForeground(countColor);
        JPanel countsBar = new RoundedPanel(14, APP_CARD_BG, APP_GREEN_SOFT,
                new FlowLayout(FlowLayout.LEFT, 12, 6));
        countsBar.setBorder(new EmptyBorder(6, 12, 6, 12));
        countsBar.add(lblConfirmedCount);
        countsBar.add(createCountSeparator());
        countsBar.add(lblUnconfirmedCount);
        countsBar.add(createCountSeparator());
        countsBar.add(lblCancelledCount);
        countsBar.add(createCountSeparator());
        countsBar.add(lblCount);
        bottomPanel.add(editBtn);
        bottomPanel.add(aaBtn);
        bottomPanel.add(countsBar);

        loadFlightsFromDatabase();

        // Live Filters
        addLiveFilter(txtSearchFlight);
        cbFrom.addActionListener(e -> applyFilters());
        cbTo.addActionListener(e -> applyFilters());
        addLiveFilter(txtDate);
        addLiveFilter(txtTime);
        cbAircraft.addActionListener(e -> applyFilters());
        cbStatus.addActionListener(e -> applyFilters());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(APP_BG);
        northPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || editBtn == null) {
                return;
            }
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow >= 0) {
                lastSelectedModelRow = flightTable.convertRowIndexToModel(selectedRow);
                editBtn.setEnabled(true);
            }
        });

        flightTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int selectedRow = flightTable.rowAtPoint(e.getPoint());
                if (selectedRow >= 0) {
                    lastSelectedModelRow = flightTable.convertRowIndexToModel(selectedRow);
                    if (editBtn != null) {
                        editBtn.setEnabled(true);
                    }
                }
            }
        });
    }

    private JComponent createHeaderPanel() {
        GradientPanel header = new GradientPanel(APP_HEADER_TOP, APP_HEADER_BOTTOM);
        header.setLayout(new BorderLayout(20, 0));
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel logo = createHeaderIconLabel();
        JLabel building = createHeaderBuildingLabel();

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Flight Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(APP_GREEN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("Operations Dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(APP_TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(subtitle);

        header.add(logo, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
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

    private static final class GradientPanel extends JPanel {
        private final Color top;
        private final Color bottom;

        private GradientPanel(Color top, Color bottom) {
            this.top = top;
            this.bottom = bottom;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            GradientPaint paint = new GradientPaint(0, 0, top, 0, height, bottom);
            g2.setPaint(paint);
            g2.fillRect(0, 0, width, height);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(APP_GREEN);
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
            g2.setColor(APP_GREEN_SOFT);
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

    private static final class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color stroke;

        private RoundedPanel(int radius, Color fill, Color stroke, LayoutManager layout) {
            super(layout);
            this.radius = radius;
            this.fill = fill;
            this.stroke = stroke;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int arc = Math.max(0, radius * 2);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);
            if (stroke != null) {
                g2.setColor(stroke);
                g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
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
        JPanel panel = new RoundedPanel(12, APP_CARD_BG, APP_GREEN_SOFT,
                new FlowLayout(FlowLayout.CENTER, 10, 8));
        panel.setBorder(new EmptyBorder(6, 10, 6, 10));
        return panel;
    }

    private JButton createClearFieldButton(JTextField field) {
        JButton button = new JButton("CLEAR");
        styleFilterButton(button, 10, new Insets(1, 8, 1, 8));
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

    private JSeparator createCountSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(APP_GREEN_SOFT);
        separator.setPreferredSize(new Dimension(1, 12));
        separator.setMinimumSize(new Dimension(1, 12));
        separator.setMaximumSize(new Dimension(1, 12));
        separator.setAlignmentY(Component.CENTER_ALIGNMENT);
        return separator;
    }

    private JButton createClearComboButton(JComboBox<?> combo) {
        JButton button = new JButton("ALL");
        styleFilterButton(button, 10, new Insets(2, 8, 10, 8));
        button.addActionListener(e -> {
            if (combo == null) {
                applyFilters();
                return;
            }
            combo.setSelectedIndex(0);
            applyFilters();
        });
        return button;
    }

    private JButton createEditButton() {
        JButton button = new JButton("EDIT");
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(APP_BG);
        button.setForeground(APP_GREEN);
        button.setBorder(BorderFactory.createLineBorder(APP_GREEN_SOFT));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(4, 12, 4, 12));
        button.addActionListener(e -> {
            try {
                openEditDialog();
            } catch (RuntimeException ex) {
                String detail = ex.getMessage();
                String type = ex.getClass().getSimpleName();
                String message = (detail == null || detail.isBlank()) ? type : (type + ": " + detail);
                JOptionPane.showMessageDialog(this,
                        "Edit failed: " + message,
                        "Edit Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return button;
    }

    private JButton createAABtn() {
        JButton button = new JButton("AA");
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(APP_BG);
        button.setForeground(APP_GREEN);
        button.setBorder(BorderFactory.createLineBorder(APP_GREEN_SOFT));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(4, 12, 4, 12));
        button.addActionListener(e -> {
            // Comment out the next line if you want to temporarily disable the sound
            // button.
            SoundEffectPlayer.playAllahuAkbarSound();
        });
        return button;
    }

    private void styleFilterButton(JButton button, int fontSize, Insets margin) {
        if (button == null) {
            return;
        }
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(APP_BG);
        button.setForeground(APP_GREEN);
        button.setBorder(BorderFactory.createLineBorder(APP_GREEN_SOFT));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Insets appliedMargin = margin == null ? new Insets(1, 8, 1, 8) : margin;
        button.setMargin(appliedMargin);

        FontMetrics fm = button.getFontMetrics(button.getFont());
        Insets currentMargin = button.getMargin();
        int textWidth = fm.stringWidth(button.getText());
        int minWidth = textWidth + currentMargin.left + currentMargin.right + 12;
        Dimension size = button.getPreferredSize();
        Dimension finalSize = new Dimension(Math.max(size.width, minWidth), size.height);
        button.setPreferredSize(finalSize);
        button.setMinimumSize(finalSize);
    }

    private void clearFilters() {
        txtSearchFlight.setText("");
        cbFrom.setSelectedIndex(0);
        cbTo.setSelectedIndex(0);
        txtDate.setValue(null);
        txtTime.setValue(null);
        cbAircraft.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        applyFilters();
    }

    private void openEditDialog() {
        int viewRow = flightTable.getSelectedRow();
        int modelRow;
        if (viewRow >= 0) {
            modelRow = flightTable.convertRowIndexToModel(viewRow);
        } else if (lastSelectedModelRow >= 0) {
            modelRow = lastSelectedModelRow;
        } else {
            JOptionPane.showMessageDialog(this,
                    "Select a record to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String dateValue = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String timeValue = String.valueOf(tableModel.getValueAt(modelRow, 1));
        String fromValue = String.valueOf(tableModel.getValueAt(modelRow, 2));
        String toValue = String.valueOf(tableModel.getValueAt(modelRow, 3));
        String flightIdValue = String.valueOf(tableModel.getValueAt(modelRow, 4));
        String aircraftValue = String.valueOf(tableModel.getValueAt(modelRow, 5));
        String statusValue = String.valueOf(tableModel.getValueAt(modelRow, 6));

        JLabel flightIdField = new JLabel(flightIdValue);
        flightIdField.setOpaque(true);
        flightIdField.setBackground(APP_PANEL_BG);
        flightIdField.setForeground(APP_GREEN);
        flightIdField.setFont(UIManager.getFont("TextField.font"));
        flightIdField.setBorder(BorderFactory.createLineBorder(APP_GREEN_SOFT));

        JFormattedTextField dateField = createMaskedField("####-##-##", DATE_PLACEHOLDER, 10, "-",
                new int[] { 4, 7, 10 }, this::showDateError);
        dateField.setText(dateValue);
        JFormattedTextField timeField = createMaskedField("##:##", TIME_PLACEHOLDER, 10, ":",
                new int[] { 2, 5 }, this::showTimeError);
        timeField.setText(formatTimeForEdit(timeValue));
        List<String> locationOptions = getLocationOptions();
        JComboBox<String> fromField = createLocationComboBox(fromValue, locationOptions);
        JComboBox<String> toField = createLocationComboBox(toValue, locationOptions);
        JComboBox<String> aircraftField = new JComboBox<>(getAircraftOptions());
        if (aircraftValue != null && !aircraftValue.isBlank()) {
            boolean found = false;
            ComboBoxModel<String> model = aircraftField.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (aircraftValue.equals(model.getElementAt(i))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                aircraftField.insertItemAt(aircraftValue, 0);
            }
            aircraftField.setSelectedItem(aircraftValue);
        }
        JComboBox<String> statusField = new JComboBox<>(new String[] { CONF, UNCONF, CANCEL });
        statusField.setSelectedItem(statusValue);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        form.setBackground(APP_BG);
        form.add(new JLabel("Flight ID:"));
        form.add(flightIdField);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Time (HH:MM):"));
        form.add(timeField);
        form.add(new JLabel("From:"));
        form.add(fromField);
        form.add(new JLabel("To:"));
        form.add(toField);
        form.add(new JLabel("Aircraft:"));
        form.add(aircraftField);
        form.add(new JLabel("Status:"));
        form.add(statusField);

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Flight",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newDateText = getMaskedFilterText(dateField);
        String newTimeText = getMaskedFilterText(timeField);
        String newFromInput = getComboEditorText(fromField);
        String newToInput = getComboEditorText(toField);
        String newFrom = resolveLocationInput(newFromInput, locationOptions);
        String newTo = resolveLocationInput(newToInput, locationOptions);
        String newAircraft = aircraftField.getSelectedItem() == null ? ""
                : aircraftField.getSelectedItem().toString().trim();
        String newStatus = statusField.getSelectedItem() == null ? "" : statusField.getSelectedItem().toString();

        if (newFrom == null) {
            JOptionPane.showMessageDialog(this,
                    "From must match an available place from the database.",
                    "Invalid From",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newTo == null) {
            JOptionPane.showMessageDialog(this,
                    "To must match an available place from the database.",
                    "Invalid To",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newFrom.equalsIgnoreCase(newTo)) {
            JOptionPane.showMessageDialog(this,
                    "From and To cannot be the same place.",
                    "Invalid Route",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newDateText.isEmpty() || newTimeText.isEmpty() || newFrom.isEmpty() || newTo.isEmpty()
                || newAircraft.isEmpty() || newStatus.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields except Flight ID are required.",
                    "Missing Data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date sqlDate;
        Time sqlTime;
        try {
            sqlDate = Date.valueOf(newDateText);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Date must be in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate selectedDate = sqlDate.toLocalDate();
        LocalDate today = LocalDate.now();
        if (selectedDate.isBefore(today)) {
            JOptionPane.showMessageDialog(this,
                    "Date must be today or later.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            sqlTime = Time.valueOf(normalizeTimeInput(newTimeText));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Time must be in HH:MM format.",
                    "Invalid Time",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (updateFlightRecord(flightIdValue, sqlDate, sqlTime, newFrom, newTo, newAircraft, newStatus)) {
            loadFlightsFromDatabase();
            applyFilters();
        }
    }

    private String formatTimeForEdit(String timeValue) {
        if (timeValue == null) {
            return "";
        }
        String trimmed = timeValue.trim();
        if (trimmed.length() >= 5) {
            return trimmed.substring(0, 5);
        }
        return trimmed;
    }

    private String normalizeTimeInput(String input) {
        String trimmed = input == null ? "" : input.trim();
        if (trimmed.matches("^\\d{2}:\\d{2}$")) {
            return trimmed + ":00";
        }
        return trimmed;
    }

    private boolean updateFlightRecord(String flightId, Date date, Time time,
            String origin, String destination, String aircraft, String status) {
        String sql = "UPDATE flights SET flight_date = ?, flight_time = ?, origin = ?, destination = ?,"
                + " aircraft = ?, status = ? WHERE flight_number = ?";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            stmt.setTime(2, time);
            stmt.setString(3, origin);
            stmt.setString(4, destination);
            stmt.setString(5, aircraft);
            stmt.setString(6, status);
            stmt.setString(7, flightId);

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                JOptionPane.showMessageDialog(this,
                        "No records were updated. Please try again.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (SQLException e) {
            showDatabaseError(e);
            return false;
        }
    }

    private String[] getAircraftOptions() {
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
        return aircraftSet.toArray(String[]::new);
    }

    private List<String> getLocationOptions() {
        Set<String> locations = new TreeSet<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object origin = tableModel.getValueAt(row, 2);
            Object destination = tableModel.getValueAt(row, 3);
            if (origin != null) {
                String value = origin.toString().trim();
                if (!value.isEmpty()) {
                    locations.add(value);
                }
            }
            if (destination != null) {
                String value = destination.toString().trim();
                if (!value.isEmpty()) {
                    locations.add(value);
                }
            }
        }
        return new ArrayList<>(locations);
    }

    private JComboBox<String> createLocationComboBox(String selectedValue, List<String> locationOptions) {
        JComboBox<String> combo = new JComboBox<>(locationOptions.toArray(String[]::new));
        combo.setEditable(false);
        if (selectedValue != null && !selectedValue.isBlank()) {
            combo.setSelectedItem(selectedValue);
        }
        return combo;
    }

    private String getComboEditorText(JComboBox<String> combo) {
        if (combo == null) {
            return "";
        }
        Object editorItem = combo.getEditor().getItem();
        if (editorItem != null) {
            return editorItem.toString().trim();
        }
        Object selected = combo.getSelectedItem();
        return selected == null ? "" : selected.toString().trim();
    }

    private String resolveLocationInput(String input, List<String> options) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        for (String option : options) {
            if (option.equalsIgnoreCase(trimmed)) {
                return option;
            }
        }
        return null;
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
            updateLocationFilterOptions();
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

    private void updateLocationFilterOptions() {
        if (cbFrom == null || cbTo == null) {
            return;
        }

        Object selectedFrom = cbFrom.getSelectedItem();
        Object selectedTo = cbTo.getSelectedItem();
        List<String> locations = getLocationOptions();

        DefaultComboBoxModel<String> fromModel = new DefaultComboBoxModel<>();
        fromModel.addElement(ALL);
        for (String location : locations) {
            fromModel.addElement(location);
        }
        cbFrom.setModel(fromModel);
        if (selectedFrom != null && locations.contains(selectedFrom.toString())) {
            cbFrom.setSelectedItem(selectedFrom.toString());
        } else {
            cbFrom.setSelectedIndex(0);
        }

        DefaultComboBoxModel<String> toModel = new DefaultComboBoxModel<>();
        toModel.addElement(ALL);
        for (String location : locations) {
            toModel.addElement(location);
        }
        cbTo.setModel(toModel);
        if (selectedTo != null && locations.contains(selectedTo.toString())) {
            cbTo.setSelectedItem(selectedTo.toString());
        } else {
            cbTo.setSelectedIndex(0);
        }
    }

    private void updateTotalCount(int count) {
        lblCount.setText("Total Records: " + count);
        lblCount.setForeground(APP_GREEN);
        int[] statusCounts = countStatuses(null);
        updateStatusCounts(statusCounts[0], statusCounts[1], statusCounts[2]);
    }

    private void updateStatusCounts(int confirmed, int unconfirmed, int cancelled) {
        lblConfirmedCount.setText("Confirmed: " + confirmed);
        lblUnconfirmedCount.setText("Unconfirmed: " + unconfirmed);
        lblCancelledCount.setText("Cancelled: " + cancelled);
        Color countColor = APP_GREEN;
        lblConfirmedCount.setForeground(countColor);
        lblUnconfirmedCount.setForeground(countColor);
        lblCancelledCount.setForeground(countColor);
    }

    private int[] countStatuses(TableRowSorter<DefaultTableModel> sorter) {
        int confirmed = 0;
        int unconfirmed = 0;
        int cancelled = 0;
        int rowCount = sorter == null ? tableModel.getRowCount() : sorter.getViewRowCount();
        for (int viewRow = 0; viewRow < rowCount; viewRow++) {
            int modelRow = sorter == null ? viewRow : sorter.convertRowIndexToModel(viewRow);
            Object value = tableModel.getValueAt(modelRow, 6);
            if (value == null) {
                continue;
            }
            String status = value.toString().trim();
            if (CONF.equalsIgnoreCase(status)) {
                confirmed++;
            } else if (UNCONF.equalsIgnoreCase(status)) {
                unconfirmed++;
            } else if (CANCEL.equalsIgnoreCase(status)) {
                cancelled++;
            }
        }
        return new int[] { confirmed, unconfirmed, cancelled };
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
        if (cbFrom.getSelectedIndex() > 0) {
            String fromValue = cbFrom.getSelectedItem().toString();
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(fromValue) + "$", 2));
        }
        if (cbTo.getSelectedIndex() > 0) {
            String toValue = cbTo.getSelectedItem().toString();
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(toValue) + "$", 3));
        }
        String dateText = getMaskedFilterText(txtDate);
        if (!dateText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(dateText), 0));
        }
        String timeText = getMaskedFilterText(txtTime);
        if (!timeText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(timeText), 1));
        }
        if (cbAircraft.getSelectedIndex() > 0) {
            String aircraftValue = cbAircraft.getSelectedItem().toString();
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(aircraftValue) + "$", 5));
        }
        if (cbStatus.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("^" + cbStatus.getSelectedItem() + "$", 6));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        int count = sorter.getViewRowCount();
        lblCount.setText(count + " flight(s) found.");
        lblCount.setForeground(APP_GREEN);
        int[] statusCounts = countStatuses(sorter);
        updateStatusCounts(statusCounts[0], statusCounts[1], statusCounts[2]);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            // Ignore and fall back to default Look & Feel.
        }
        applyTheme();
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private static void applyTheme() {
        UIManager.put("Panel.background", APP_BG);
        UIManager.put("OptionPane.background", APP_BG);
        UIManager.put("OptionPane.foreground", APP_GREEN);
        UIManager.put("OptionPane.messageForeground", APP_GREEN);
        UIManager.put("Label.foreground", APP_GREEN);
        UIManager.put("TextField.background", APP_BG);
        UIManager.put("TextField.foreground", APP_GREEN);
        UIManager.put("TextField.caretForeground", APP_GREEN);
        UIManager.put("TextField.selectionBackground", APP_GREEN_SOFT);
        UIManager.put("TextField.selectionForeground", APP_BG);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(APP_GREEN_SOFT));
        UIManager.put("FormattedTextField.background", APP_BG);
        UIManager.put("FormattedTextField.foreground", APP_GREEN);
        UIManager.put("FormattedTextField.caretForeground", APP_GREEN);
        UIManager.put("FormattedTextField.border", BorderFactory.createLineBorder(APP_GREEN_SOFT));
        UIManager.put("ComboBox.background", APP_BG);
        UIManager.put("ComboBox.foreground", APP_GREEN);
        UIManager.put("ComboBox.selectionBackground", APP_GREEN_SOFT);
        UIManager.put("ComboBox.selectionForeground", APP_BG);
        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(APP_GREEN_SOFT));
        UIManager.put("Button.background", APP_BG);
        UIManager.put("Button.foreground", APP_GREEN);
        UIManager.put("Button.border", BorderFactory.createLineBorder(APP_GREEN_SOFT));
        UIManager.put("Table.background", APP_BG);
        UIManager.put("Table.foreground", APP_GREEN);
        UIManager.put("Table.selectionBackground", APP_GREEN_SOFT);
        UIManager.put("Table.selectionForeground", APP_BG);
        UIManager.put("TableHeader.background", APP_PANEL_BG);
        UIManager.put("TableHeader.foreground", APP_GREEN);
        UIManager.put("ScrollPane.background", APP_BG);
        UIManager.put("Viewport.background", APP_BG);
        UIManager.put("Separator.foreground", APP_GREEN_SOFT);
        UIManager.put("Table.alternateRowColor", APP_ROW_ALT);
    }

}
