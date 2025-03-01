package fi.tj88888;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Main extends JFrame implements ActionListener {

    private JTextArea display;
    private String operator = "";
    private BigDecimal result = BigDecimal.ZERO;
    private boolean operatorPressed = false;
    private Color currentThemeColor = new Color(88, 101, 242);
    private int displayFontSize = 28;

    public Main() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 650);
        setLayout(new BorderLayout());
        setUndecorated(true);
        
        setShape(new RoundRectangle2D.Double(0, 0, 400, 650, 20, 20));
        
        Image windowIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/window-icon.png"));
        setIconImage(windowIcon);

        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                Image taskbarIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/taskbar-icon.png"));
                taskbar.setIconImage(taskbarIcon);
            } catch (UnsupportedOperationException | NullPointerException e) {
                System.out.println("The taskbar icon could not be set.");
            }
        }

        Color backgroundColor = new Color(32, 34, 37);
        Color numberButtonColor = new Color(47, 49, 54);
        Color operatorButtonColor = new Color(66, 70, 77);
        Color specialButtonColor = currentThemeColor;
        Color displayBackgroundColor = new Color(24, 25, 28);
        Color displayTextColor = Color.WHITE;
        Color buttonTextColor = Color.WHITE;
        
        JPanel titleBarPanel = new JPanel(new BorderLayout());
        titleBarPanel.setBackground(backgroundColor);
        titleBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        
        JLabel titleLabel = new JLabel("Calculator");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBarPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);
        
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        
        controlPanel.add(closeButton);
        titleBarPanel.add(controlPanel, BorderLayout.EAST);
        
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int dragStartX, dragStartY;
            
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int newX = getLocation().x + e.getX() - dragStartX;
                int newY = getLocation().y + e.getY() - dragStartY;
                setLocation(newX, newY);
            }
        };
        
        titleBarPanel.addMouseListener(dragAdapter);
        titleBarPanel.addMouseMotionListener(dragAdapter);
        
        add(titleBarPanel, BorderLayout.NORTH);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(backgroundColor);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        
        display = new JTextArea();
        display.setEditable(false);
        display.setLineWrap(true);
        display.setWrapStyleWord(false);
        display.setFont(new Font("Segoe UI", Font.BOLD, displayFontSize));
        display.setBackground(displayBackgroundColor);
        display.setForeground(displayTextColor);
        display.setBorder(null);
        display.setMargin(new Insets(25, 15, 25, 15));
        
        display.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JPanel displayContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(displayBackgroundColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        displayContainer.setOpaque(false);
        displayContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        displayContainer.add(display, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(displayContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(displayBackgroundColor);
        scrollPane.getViewport().setBackground(displayBackgroundColor);
        
        scrollPane.getVerticalScrollBar().setBorder(null);
        scrollPane.getVerticalScrollBar().setBackground(displayBackgroundColor);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(displayBackgroundColor, 
                                                                       new Color(52, 54, 57)));
        
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel displayBorder = new JPanel(new BorderLayout());
        displayBorder.setBackground(displayBackgroundColor);
        displayBorder.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(42, 44, 47), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        displayBorder.add(scrollPane);
        
        displayPanel.add(displayBorder, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1;
        gbc.weighty = 0.8;

        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        themePanel.setBackground(backgroundColor);
        
        addThemeButton(themePanel, new Color(88, 101, 242), "Discord Blue");
        addThemeButton(themePanel, new Color(0, 186, 124), "Spotify Green");
        addThemeButton(themePanel, new Color(255, 69, 58), "Apple Red");
        addThemeButton(themePanel, new Color(255, 159, 10), "Orange");
        addThemeButton(themePanel, new Color(191, 90, 242), "Purple");
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        buttonPanel.add(themePanel, gbc);
        
        addButton("C", specialButtonColor, buttonTextColor, buttonPanel, gbc, 0, 1, 1, 1);
        addButton("<", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 1, 1, 1, 1);
        addButton("/", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 2, 1, 1, 1);
        addButton("*", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 1, 1, 1);

        addButton("7", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 2, 1, 1);
        addButton("8", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 2, 1, 1);
        addButton("9", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 2, 1, 1);
        addButton("-", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 2, 1, 1);

        addButton("4", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 3, 1, 1);
        addButton("5", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 3, 1, 1);
        addButton("6", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 3, 1, 1);
        addButton("+", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 3, 1, 1);

        addButton("1", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 4, 1, 1);
        addButton("2", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 4, 1, 1);
        addButton("3", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 4, 1, 1);

        addButton("=", specialButtonColor, buttonTextColor, buttonPanel, gbc, 3, 4, 1, 2);

        addButton("0", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 5, 2, 1);
        addButton(".", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 5, 1, 1);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void addThemeButton(JPanel panel, Color themeColor, String tooltip) {
        JButton themeButton = new JButton();
        themeButton.setPreferredSize(new Dimension(24, 24));
        themeButton.setBackground(themeColor);
        themeButton.setBorderPainted(false);
        themeButton.setFocusPainted(false);
        themeButton.setToolTipText(tooltip);
        themeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        themeButton.putClientProperty("JButton.buttonType", "roundRect");
        
        themeButton.addActionListener(e -> {
            currentThemeColor = themeColor;
            updateTheme();
        });
        
        panel.add(themeButton);
    }
    
    private void updateTheme() {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                updateButtonsInPanel((JPanel) c);
            }
        }
    }
    
    private void updateButtonsInPanel(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                if (button.getText().equals("C") || button.getText().equals("=")) {
                    button.setBackground(currentThemeColor);
                }
            } else if (c instanceof JPanel) {
                updateButtonsInPanel((JPanel) c);
            }
        }
    }

    private void addButton(String text, Color bgColor, Color fgColor, JPanel panel, GridBagConstraints gbc, int x, int y, int width, int height) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);
        
        button.setUI(new CustomButtonUI(10));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brighten(bgColor, 0.2f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(darken(bgColor, 0.1f));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        panel.add(button, gbc);
    }
    
    private Color brighten(Color color, float fraction) {
        int r = Math.min(255, (int)(color.getRed() * (1 + fraction)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + fraction)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + fraction)));
        return new Color(r, g, b);
    }
    
    private Color darken(Color color, float fraction) {
        int r = Math.max(0, (int)(color.getRed() * (1 - fraction)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - fraction)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - fraction)));
        return new Color(r, g, b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "C":
                display.setText("");
                result = BigDecimal.ZERO;
                operator = "";
                operatorPressed = false;
                break;
            case "<":
                String currentText = display.getText();
                if (!currentText.isEmpty()) {
                    display.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "=":
                handleCalculation();
                break;
            default:
                if ("+-/*".contains(command)) {
                    operator = command;
                    operatorPressed = true;
                    display.setText(display.getText() + " " + command + " ");
                } else {
                    if (operatorPressed) {
                        display.setText(display.getText() + command);
                        operatorPressed = false;
                    } else {
                        display.setText(display.getText() + command);
                    }
                }
                break;
        }
    }

    private void handleCalculation() {
        try {
            String[] parts = display.getText().split(" ");
            if (parts.length < 3) return;

            BigDecimal num1 = new BigDecimal(parts[0]);
            BigDecimal num2 = new BigDecimal(parts[2]);
            
            MathContext mc = new MathContext(34, RoundingMode.HALF_UP);

            switch (operator) {
                case "+":
                    result = num1.add(num2, mc);
                    break;
                case "-":
                    result = num1.subtract(num2, mc);
                    break;
                case "*":
                    result = num1.multiply(num2, mc);
                    break;
                case "/":
                    if (num2.compareTo(BigDecimal.ZERO) != 0) {
                        result = num1.divide(num2, mc);
                    } else {
                        JOptionPane.showMessageDialog(this, "Cannot divide by zero.");
                        return;
                    }
                    break;
            }

            String formattedResult = result.stripTrailingZeros().toPlainString();
            display.setText(formattedResult);

            operatorPressed = false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new Main();
    }
}

class CustomButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
    private final int radius;
    
    public CustomButtonUI(int radius) {
        this.radius = radius;
    }
    
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (b.getModel().isPressed()) {
            g2.setColor(b.getBackground().darker());
        } else if (b.getModel().isRollover()) {
            g2.setColor(b.getBackground().brighter());
        } else {
            g2.setColor(b.getBackground());
        }
        
        g2.fill(new RoundRectangle2D.Double(0, 0, b.getWidth(), b.getHeight(), radius, radius));
        
        g2.setColor(new Color(0, 0, 0, 40));
        g2.draw(new RoundRectangle2D.Double(0, 0, b.getWidth() - 1, b.getHeight() - 1, radius, radius));
        
        g2.setColor(new Color(0, 0, 0, 10));
        g2.fill(new RoundRectangle2D.Double(2, 2, b.getWidth() - 4, b.getHeight() - 4, radius, radius));
        
        g2.dispose();
        
        super.paint(g, c);
    }
}

class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
    private Color trackColor;
    private Color thumbColor;
    
    public CustomScrollBarUI(Color trackColor, Color thumbColor) {
        this.trackColor = trackColor;
        this.thumbColor = thumbColor;
    }
    
    @Override
    protected void configureScrollBarColors() {
        thumbHighlightColor = thumbColor;
        thumbDarkShadowColor = thumbColor;
        thumbLightShadowColor = thumbColor;
        thumbColor = thumbColor;
        trackColor = trackColor;
        trackHighlightColor = trackColor;
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
    
    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        g2.dispose();
    }
}