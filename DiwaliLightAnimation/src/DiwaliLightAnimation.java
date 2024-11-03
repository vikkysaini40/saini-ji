import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.util.Random;

public class DiwaliLightAnimation extends JFrame {
    private int lightCount = 10; // Number of diyas
    private Color[] colors = {Color.YELLOW, Color.ORANGE, Color.RED, Color.PINK, Color.MAGENTA, Color.CYAN};
    private int currentColorIndex = 0;
    private Timer timer;
    private final Image lakshmiImage;

    public DiwaliLightAnimation() {
        setTitle("Diwali Light Animation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load the Lakshmi image
        lakshmiImage = new ImageIcon("path/to/lakshmi_image.png").getImage(); // Change to your image path

        // Set up a timer for animation
        timer = new Timer(500, (ActionEvent e) -> {
            currentColorIndex = (currentColorIndex + 1) % colors.length;
            repaint();
        });

        // Button panel
        JPanel buttonPanel = new JPanel();
        
        JButton startButton = new JButton("Start Animation");
        startButton.addActionListener(e -> timer.start());
        buttonPanel.add(startButton);

        JButton stopButton = new JButton("Stop Animation");
        stopButton.addActionListener(e -> timer.stop());
        buttonPanel.add(stopButton);

        JButton increaseButton = new JButton("Increase Diyas");
        increaseButton.addActionListener(e -> {
            if (lightCount < 20) { // Limit max diyas
                lightCount++;
                repaint();
            }
        });
        buttonPanel.add(increaseButton);

        JButton decreaseButton = new JButton("Decrease Diyas");
        decreaseButton.addActionListener(e -> {
            if (lightCount > 1) { // Limit min diyas
                lightCount--;
                repaint();
            }
        });
        buttonPanel.add(decreaseButton);

        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> printContent());
        buttonPanel.add(printButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void printContent() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                paint(g2d); // Call the paint method to print the content
                return PAGE_EXISTS;
            }
        });
        
        boolean doPrint = printerJob.printDialog();
        if (doPrint) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawName(g); // Draw "Vikky"
        drawSaini(g); // Draw "Saini"
        drawLakshmi(g); // Draw the Lakshmi image
        drawDiyas(g);
    }

    private void drawName(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 28)); // Set font style and size
        String name = "Vikky Saini";
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = (getWidth() - metrics.stringWidth(name)) / 2; // Center the text
        int y = 50; // Set y position
        g.drawString(name, x, y); // Draw the name
    }

    private void drawSaini(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font style and size for the message
        String message = """
                      May there be light in every home, 
                      and showers of happiness all around,
                      With lamps adorning, may love abound.
                      This Diwali, may you receive the shadow of joy,
                      And may every heart be filled with the anthem of prosperity.
                      
                      Wishing you and your family a very Happy Diwali!""";
        String[] lines = message.split("\n");
        int y = 90; // Starting y position below Vikky
        for (String line : lines) {
            int x = (getWidth() - g.getFontMetrics().stringWidth(line)) / 2; // Center the text
            g.drawString(line, x, y); // Draw each line of the message
            y += 20; // Move down for the next line
        }
    }

    private void drawLakshmi(Graphics g) {
        if (lakshmiImage != null) {
            int imageX = (getWidth() - lakshmiImage.getWidth(null)) / 2; // Center the image
            int imageY = 200; // Set y position for the image
            g.drawImage(lakshmiImage, imageX, imageY, null); // Draw the image
        }
    }

    private void drawDiyas(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int diyaWidth = 50;
        int diyaHeight = 100;

        Random rand = new Random(); // Random number generator for colors

        for (int i = 0; i < lightCount; i++) {
            int x = (width / lightCount) * i + (width / lightCount - diyaWidth) / 2;
            int y = height / 2 - diyaHeight / 2 + 250; // Adjust y position to avoid overlap with other elements
            
            // Use a random color from the array for each diya
            g.setColor(colors[rand.nextInt(colors.length)]);
            g.fillOval(x, y, diyaWidth, diyaHeight); // Draw diya
            
            g.setColor(Color.BLACK);
            g.fillOval(x + 10, y + 30, 30, 30); // Wick
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DiwaliLightAnimation animation = new DiwaliLightAnimation();
            animation.setVisible(true);
        });
    }
}
