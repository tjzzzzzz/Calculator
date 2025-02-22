package fi.tj88888;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    private JTextField display; // The text field where the numbers and calculations appear
    private String operator = ""; // Stores the currently selected operator (+, -, *, /)
    private double result = 0; // Keeps track of the ongoing calculated result
    private boolean operatorPressed = false; // Tracks if an operator button is pressed

    public Main() {
        // Set the basic properties of the calculator window
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 650); // Larger height to leave room for all UI elements
        setLayout(new BorderLayout());

        // Set the calculator's window icon
        Image windowIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/window-icon.png"));
        setIconImage(windowIcon); // Appears in the window title bar

        // Set the taskbar icon (this only works on Java 9+)
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                Image taskbarIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/taskbar-icon.png"));
                taskbar.setIconImage(taskbarIcon);
            } catch (UnsupportedOperationException | NullPointerException e) {
                System.out.println("The taskbar icon could not be set."); // Logs a message if it fails
            }
        }

        // Define color themes for the calculator
        Color backgroundColor = new Color(43, 43, 43); // Dark gray for the background
        Color numberButtonColor = new Color(63, 63, 63); // Slightly lighter gray for number buttons
        Color operatorButtonColor = new Color(84, 84, 84); // Medium gray for operator buttons
        Color specialButtonColor = new Color(255, 96, 80); // Bright red-orange for "C" and "="
        Color displayTextColor = Color.WHITE; // White text for the calculator display
        Color buttonTextColor = Color.WHITE; // White text for all buttons

        // Set the main window's background color
        getContentPane().setBackground(backgroundColor);

        // Create the calculator display at the top
        display = new JTextField();
        display.setEditable(false); // The user shouldn't manually edit text here
        display.setHorizontalAlignment(SwingConstants.RIGHT); // Align text to the right, like a real calculator
        display.setFont(new Font("Arial", Font.BOLD, 34)); // Bigger font for better readability
        display.setBackground(backgroundColor);
        display.setForeground(displayTextColor);
        display.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10)); // Add spacing around the display
        add(display, BorderLayout.NORTH); // Place the display area at the top of the layout

        // Create the panel that holds all the calculator buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(backgroundColor);

        // Set up layout constraints for buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Make buttons stretch to fill their cells
        gbc.insets = new Insets(5, 5, 5, 5); // Add some spacing between buttons
        gbc.weightx = 1; // Make buttons adjust their size horizontally
        gbc.weighty = 0.8; // Adjust vertical size slightly smaller

        // Add special and operator buttons to the first row
        addButton("C", specialButtonColor, buttonTextColor, buttonPanel, gbc, 0, 0, 1, 1);
        addButton("<", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 1, 0, 1, 1);
        addButton("/", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 2, 0, 1, 1);
        addButton("*", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 0, 1, 1);

        // Add numbers 7, 8, 9, and '-' to the second row
        addButton("7", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 1, 1, 1);
        addButton("8", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 1, 1, 1);
        addButton("9", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 1, 1, 1);
        addButton("-", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 1, 1, 1);

        // Add numbers 4, 5, 6, and '+' to the third row
        addButton("4", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 2, 1, 1);
        addButton("5", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 2, 1, 1);
        addButton("6", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 2, 1, 1);
        addButton("+", operatorButtonColor, buttonTextColor, buttonPanel, gbc, 3, 2, 1, 1);

        // Add numbers 1, 2, and 3 to the fourth row
        addButton("1", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 3, 1, 1);
        addButton("2", numberButtonColor, buttonTextColor, buttonPanel, gbc, 1, 3, 1, 1);
        addButton("3", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 3, 1, 1);

        // Add the "=" button, which takes up two vertical cells
        addButton("=", specialButtonColor, buttonTextColor, buttonPanel, gbc, 3, 3, 1, 2);

        // Add 0 and "." to the final row
        addButton("0", numberButtonColor, buttonTextColor, buttonPanel, gbc, 0, 4, 2, 1); // "0" spans two columns
        addButton(".", numberButtonColor, buttonTextColor, buttonPanel, gbc, 2, 4, 1, 1);

        // Add the button panel below the display on the main window
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true); // Make everything visible
    }

    // A helper method to create buttons with consistent styling
    private void addButton(String text, Color bgColor, Color fgColor, JPanel panel, GridBagConstraints gbc, int x, int y, int width, int height) {
        gbc.gridx = x; // Column position
        gbc.gridy = y; // Row position
        gbc.gridwidth = width; // How many columns it should occupy
        gbc.gridheight = height; // How many rows it should occupy

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18)); // Smaller font size for buttons
        button.setFocusPainted(false); // Remove focus highlight for a cleaner look
        button.setBorderPainted(false); // Remove borders for a modern design
        button.setBackground(bgColor); // Set the button's background color
        button.setForeground(fgColor); // Set the button's text color
        button.addActionListener(this); // Tell the button to listen for clicks

        panel.add(button, gbc); // Add the button to the panel using the layout constraints
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand(); // Get the text of the button clicked

        switch (command) {
            case "C": // Reset everything
                display.setText("");
                result = 0;
                operator = "";
                operatorPressed = false;
                break;
            case "<": // Backspace: remove the last character
                String currentText = display.getText();
                if (!currentText.isEmpty()) {
                    display.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "=": // Perform the calculation
                handleCalculation();
                break;
            default: // For numbers and operators
                if ("+-/*".contains(command)) {
                    operator = command; // Store the operator
                    operatorPressed = true; // Flag that an operator was pressed
                    display.setText(display.getText() + " " + command + " "); // Add space for readability
                } else { // Handle numeric input
                    if (operatorPressed) {
                        display.setText(display.getText() + command);
                        operatorPressed = false; // Reset the operatorPressed flag
                    } else {
                        display.setText(display.getText() + command);
                    }
                }
                break;
        }
    }

    private void handleCalculation() {
        try {
            // Split the input into numbers and the operator
            String[] parts = display.getText().split(" ");
            if (parts.length < 3) return; // If there's not enough input, stop here

            double num1 = Double.parseDouble(parts[0]); // First number
            double num2 = Double.parseDouble(parts[2]); // Second number

            // Perform the calculation
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

            // Display the result
            if (result == (long) result) { // Check if the result is an integer
                display.setText(String.valueOf((long) result)); // Display without decimals
            } else {
                display.setText(String.valueOf(result)); // Display with decimals
            }

            operatorPressed = false; // Reset the operator flag
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input."); // Show an error message
        }
    }

    public static void main(String[] args) {
        new Main(); // Create and display the calculator
    }
}