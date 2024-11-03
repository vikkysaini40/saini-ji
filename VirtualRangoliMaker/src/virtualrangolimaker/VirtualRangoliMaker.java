/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package virtualrangolimaker;

/**
 *
 * @author saini
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Stack;

public class VirtualRangoliMaker extends JFrame {
    private int lastX, lastY;
    private Color currentColor = Color.MAGENTA; // Default color
    private String currentShape = "Freehand";
    private BufferedImage canvasImage;
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();
    private JPanel drawPanel;

    public VirtualRangoliMaker() {
        setTitle("Virtual Rangoli Maker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (canvasImage != null) {
                    g.drawImage(canvasImage, 0, 0, null);
                }
            }
        };

        drawPanel.setBackground(Color.WHITE);
        canvasImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        clearCanvas(); // Initialize canvas to white

        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                undoStack.push(copyCanvas()); // Save state for undo
                redoStack.clear(); // Clear redo stack
            }
        });

        drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics g = canvasImage.getGraphics();
                if ("Eraser".equals(currentShape)) {
                    g.setColor(Color.WHITE); // Eraser color
                    g.fillRect(e.getX(), e.getY(), 10, 10); // Erase in a small square
                } else {
                    g.setColor(currentColor);
                    if ("Freehand".equals(currentShape)) {
                        g.drawLine(lastX, lastY, e.getX(), e.getY());
                    } else if ("Circle".equals(currentShape)) {
                        int radius = 25;
                        g.fillOval(e.getX() - radius, e.getY() - radius, radius * 2, radius * 2);
                    } else if ("Square".equals(currentShape)) {
                        int size = 50;
                        g.fillRect(e.getX() - size / 2, e.getY() - size / 2, size, size);
                    }
                }
                lastX = e.getX();
                lastY = e.getY();
                drawPanel.repaint();
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel();

        // Color selection button
        JButton colorButton = new JButton("Select Color");
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
            }
        });

        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearCanvas());

        // Eraser tool
        JButton eraserButton = new JButton("Eraser");
        eraserButton.addActionListener(e -> currentShape = "Eraser");

        // Shape selection
        String[] shapes = {"Freehand", "Circle", "Square"};
        JComboBox<String> shapeSelector = new JComboBox<>(shapes);
        shapeSelector.addActionListener(e -> {
            currentShape = (String) shapeSelector.getSelectedItem();
        });
        buttonPanel.add(shapeSelector);

        // Undo/Redo buttons
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                redoStack.push(copyCanvas());
                canvasImage = undoStack.pop();
                drawPanel.repaint();
            }
        });

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (!redoStack.isEmpty()) {
                undoStack.push(copyCanvas());
                canvasImage = redoStack.pop();
                drawPanel.repaint();
            }
        });

        // Save and Load functionality
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveCanvas());

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> loadCanvas());

        buttonPanel.add(colorButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(eraserButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(redoButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        add(drawPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private BufferedImage copyCanvas() {
        BufferedImage copy = new BufferedImage(canvasImage.getWidth(), canvasImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = copy.getGraphics();
        g.drawImage(canvasImage, 0, 0, null);
        g.dispose();
        return copy;
    }

    private void clearCanvas() {
        Graphics2D g2d = canvasImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 800, 600);
        g2d.dispose();
        drawPanel.repaint();
    }

    private void saveCanvas() {
        try {
            ImageIO.write(canvasImage, "PNG", new File("drawing.png"));
            JOptionPane.showMessageDialog(this, "Canvas saved as drawing.png");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save image: " + e.getMessage());
        }
    }

    private void loadCanvas() {
        try {
            BufferedImage loadedImage = ImageIO.read(new File("drawing.png"));
            Graphics g = canvasImage.getGraphics();
            g.drawImage(loadedImage, 0, 0, null);
            g.dispose();
            drawPanel.repaint();
            JOptionPane.showMessageDialog(this, "Canvas loaded from drawing.png");
        } catch (HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VirtualRangoliMaker rangoliMaker = new VirtualRangoliMaker();
            rangoliMaker.setVisible(true);
        });
    }
}
