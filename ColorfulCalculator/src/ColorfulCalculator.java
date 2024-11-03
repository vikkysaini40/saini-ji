import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Stack;

public class ColorfulCalculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final StringBuilder input;
    private final Random random;

    public ColorfulCalculator() {
        input = new StringBuilder();
        random = new Random();

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Digital-7", Font.BOLD, 48));
        display.setBackground(Color.BLACK);
        display.setForeground(Color.GREEN);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create buttons
        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", "C", "=", "+",
            "√", "(", ")", "<" // Backspace button labeled as "<"
        };
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        
        for (String label : buttonLabels) {
            JButton button = createButton(label);
            panel.add(button);
        }

        // Set up the frame
        setLayout(new BorderLayout());
        
        ImageIcon logo = new ImageIcon("path/to/your/logo.png"); // Change the path to your logo file
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        add(logoLabel, BorderLayout.NORTH);
        add(display, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        
        setTitle("Colorful Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.CYAN);
        button.setForeground(Color.BLACK);
        button.addActionListener(this);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(randomColor());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(Color.CYAN);
            }
        });

        return button;
    }

    private Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.charAt(0) == 'C') {
            input.setLength(0);
            display.setText("");
        } else if (command.charAt(0) == '=') {
            try {
                String result = evaluate(input.toString());
                display.setText(result);
                input.setLength(0); // Clear input after evaluation
            } catch (Exception ex) {
                display.setText("Error");
            }
        } else if (command.equals("<")) {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                display.setText(input.toString());
            }
        } else {
            input.append(command);
            display.setText(input.toString());
        }

        // Handle natural language processing
        if (!input.toString().isEmpty()) {
            processNaturalLanguageInput(input.toString());
        }
    }

    private void processNaturalLanguageInput(String input) {
        // Simple NLP parsing to handle some common phrases
        if (input.toLowerCase().contains("add")) {
            String[] parts = input.split("add");
            if (parts.length > 1) {
                String[] numbers = parts[1].trim().split("\\s+");
                double result = 0;
                for (String number : numbers) {
                    result += Double.parseDouble(number);
                }
                display.setText(String.valueOf(result));
                this.input.setLength(0);
            }
        } else if (input.toLowerCase().contains("subtract")) {
            String[] parts = input.split("subtract");
            if (parts.length > 1) {
                String[] numbers = parts[1].trim().split("\\s+");
                double result = Double.parseDouble(numbers[0]);
                for (int i = 1; i < numbers.length; i++) {
                    result -= Double.parseDouble(numbers[i]);
                }
                display.setText(String.valueOf(result));
                this.input.setLength(0);
            }
        } else if (input.toLowerCase().contains("square root of")) {
            String[] parts = input.split("square root of");
            if (parts.length > 1) {
                double value = Double.parseDouble(parts[1].trim());
                display.setText(String.valueOf(Math.sqrt(value)));
                this.input.setLength(0);
            }
        }
        // Add more phrases as needed
    }

    private String evaluate(String expression) {
        if (expression.startsWith("√")) {
            double value = evaluateExpression(expression.substring(1));
            return String.valueOf(Math.sqrt(value));
        }
        return String.valueOf(evaluateExpression(expression));
    }

    private double evaluateExpression(String expression) {
        String[] tokens = expression.split("(?<=[-+*/()])|(?=[-+*/()])");
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (isNumeric(token)) {
                values.push(Double.valueOf(token));
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Remove the '('
            } else {
                while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private double applyOperation(String operator, double b, double a) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            case "√":
                return Math.sqrt(a);
            default:
                return 0;
        }
    }

    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "√" -> 3;
            default -> 0;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColorfulCalculator::new);
    }
}
