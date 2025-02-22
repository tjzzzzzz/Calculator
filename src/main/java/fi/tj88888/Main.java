package fi.tj88888;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    private JTextField display; // Calculator display
    private String operator = ""; // Tracks the operator pressed
    private double result = 0; // Tracks the ongoing result
    private boolean operatorPressed = false; // Tracks if an operator button was pressed

    public Main() {
        // Set up the main calculator window
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 650); // Slightly taller window for extra top space
        setLayout(new BorderLayout());

        // Set window-specific icon
        Image windowIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/window-icon.png"));
        setIconImage(windowIcon); // For the application window

        // Set taskbar-specific icon (Java 9+)
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                Image taskbarIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/taskbar-icon.png"));
                taskbar.setIconImage(taskbarIcon); // For the taskbar
            } catch (UnsupportedOperationException | NullPointerException e) {
                System.out.println("The taskbar icon could not be set.");
            }
        }

        // Define colors for styling
        Color backgroundColor = new Color(43, 43, 43); // Dark gray background
        Color numberButtonColor = new Color(63, 63, 63); // Slightly lighter gray
        Color operatorButtonColor = new Color(84, 84, 84); // Lighter gray for operators
        Color specialButtonColor = new Color(255, 96, 80); // Accent color for "C" and "="
        Color displayTextColor = Color.WHITE; // White text for display
        Color buttonTextColor = Color.WHITE; // White text color for all buttons

        // Set the background of the main JFrame
        getContentPane().setBackground(backgroundColor);

        // Display section (with extra padding for more space)
        display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 34)); // Larger font for readability
        display.setBackground(backgroundColor);
        display.setForeground(displayTextColor);
        display.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10)); // Larger padding for extra space
        add(display, BorderLayout.NORTH);

        // Buttons panel section
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(backgroundColor);

        // GridBagLayout constraints for buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Buttons should fill their cells
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between buttons
        gbc.weightx = 1; // Equal horizontal size for buttons
        gbc.weighty = 0.8; // Reduced vertical size for smaller buttons

        // Row 1: Special buttons
        addButton("C", specialButtonColor, buttonTextColor, buttonPanel, gbc, 0, 0, 1, 1);
        addButton("<", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 1, 0, 1, 1);
        addButton("/", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 2, 0, 1, 1);
        addButton("*", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 0, 1, 1);

        // Row 2: Numbers 7-9
        addButton("7", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 1, 1, 1);
        addButton("8", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 1, 1, 1);
        addButton("9", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 1, 1, 1);
        addButton("-", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 1, 1, 1);

        // Row 3: Numbers 4-6
        addButton("4", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 2, 1, 1);
        addButton("5", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 2, 1, 1);
        addButton("6", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 2, 1, 1);
        addButton("+", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 2, 1, 1);

        // Row 4: Numbers 1-3
        addButton("1", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 3, 1, 1);
        addButton("2", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 3, 1, 1);
        addButton("3", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 3, 1, 1);

        // Add "=" button spanning two rows
        addButton("=", specialButtonColor, buttonTextColor, buttonPanel, gbc, 3, 3, 1, 2);

        // Row 5: Number 0 and "."
        addButton("0", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 4, 2, 1);
        addButton(".", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 4, 1, 1);

        // Add the button panel at the center of the JFrame
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true); // Make the JFrame visible
    }

    // Helper method to add buttons to the layout
    private void addButton(String text, Color bgColor, Color fgColor, JPanel panel, GridBagConstraints gbc, int x, int y, int width, int height) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18)); // Smaller font for smaller buttons
        button.setFocusPainted(false);
        button.setBorderPainted(false); // Borderless modern style
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.addActionListener(this);

        panel.add(button, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "C": // Clear
                display.setText("");
                result = 0;
                operator = "";
                operatorPressed = false;
                break;
            case "<": // Backspace
                String currentText = display.getText();
                if (!currentText.isEmpty()) {
                    display.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "=": // Perform calculation
                handleCalculation();
                break;
            default: // Numbers and operators
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

            double num1 = Double.parseDouble(parts[0]);
            double num2 = Double.parseDouble(parts[2]);

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        JOptionPane.showMessageDialog(this, "Cannot divide by zero.");
                        return;
                    }
                    break;
            }

            // Check if the result is a whole number
            if (result == (long) result) {
                display.setText(String.valueOf((long) result)); // Display as integer
            } else {
                display.setText(String.valueOf(result)); // Display as double
            }

            operatorPressed = false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    public static void main(String[] args) {
        new Main(); // Create and show the calculator
    }
}