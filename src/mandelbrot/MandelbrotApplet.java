package mandelbrot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Applet that draws the mandelbrot set and allows the user to zoom in and out
 * in order to examine different parts.
 * @author Zach Cotter
 */
public class MandelbrotApplet extends JApplet {

    private final int SIZE = 550;
    private Stack<ZoomData> oldZooms;
    private Coordinate points[][];
    private Color pixels[][];
    private MandelbrotPanel panel;
    private Rectangle zoom;
    private ZoomData currentZoom;
    private JProgressBar bar;

    /**
     * Constructs a new MandelbrotApplet
     */
    public MandelbrotApplet() {
        oldZooms = new Stack<ZoomData>();
        panel = new MandelbrotPanel();

        panel.addMouseListener(new MouseDragListener());
        this.setLayout(new BorderLayout());
        this.add(panel,
                 BorderLayout.CENTER);
        this.add(new ButtonPanel(),
                 BorderLayout.SOUTH);

    }

    //mouse listener that allows the user to make a square around the area
    //they are zooming in on.
    private class MouseDragListener extends MouseAdapter {

        private int xPress;
        private int yPress;

        //stores the pressed location
        @Override
        public void mousePressed(MouseEvent e) {
            xPress = e.getX();
            yPress = e.getY();
        }

        //forms a square with the mouse pressed location as the upper left
        //corner, and the minimum of the x and y offsets between the press and
        //release locations as the side length.
        @Override
        public void mouseReleased(MouseEvent e) {
            int xRel = e.getX();
            int yRel = e.getY();
            int xDis = Math.abs(xRel - xPress);
            int yDis = Math.abs(yRel - yPress);
            int dis = Math.min(xDis,
                               yDis);
            zoom = new Rectangle(xPress,
                                 yPress,
                                 dis,
                                 dis);
            panel.paintIt(panel.getGraphics());
        }
    }

    //panel in which the fractal is draw.
    private class MandelbrotPanel extends JPanel {

        //stores the point in the fractal represented by the pixel in
        //the upper left corner of the panel.
        private double xPos;
        private double yPos;
        //stores the width and height of the current fractal view (in units on the 
        //imaginary plane, not in pixels)
        private double width;
        private double height;

        /**
         * Constructs a new mandelbrot panel.
         */
        public MandelbrotPanel() {
            this.setSize(SIZE,
                         SIZE);
            pixels = new Color[SIZE][SIZE];
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    pixels[x][y] = Color.GREEN;
                }
            }
            xPos = -2.0;
            yPos = -2.0;
            width = 4.0;
            height = 4.0;
            currentZoom = new ZoomData(xPos,
                                       yPos,
                                       width,
                                       height);
            calculate();
        }

        /**
         * Zooms to the specified position in the fractal
         * @param xPos x value of the point in the upper left corner of
         * the specified fractal view.
         * @param yPos y value of the point in the upper left corner of the
         * specified fractal view.
         * @param width the width of the fractal view
         * @param height the height of the fractal view.
         */
        public void makeView(double xPos,
                             double yPos,
                             double width,
                             double height) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.width = width;
            this.height = height;
            calculate();
            paintIt(this.getGraphics());
        }

        /**
         * Assigns a point on the fractal to each pixel within the panel,
         * then determines if that point is in the mandelbrot set within
         * a specified number of iterations. If not, it stores the number of
         * iterations required to exclude it. Then determines the appropriate
         * coloring of the pixel.
         */
        private void calculate() {
            points = new Coordinate[SIZE][SIZE];
            pixels = new Color[SIZE][SIZE];
            double xInc = width / SIZE;
            double yInc = height / SIZE;
            double progress = 0.0;
            double progressIncrement = 100.0 / SIZE;
            int lastUpdate = 0;
            for (int x = 0; x < SIZE; x++) {
                progress += progressIncrement;
                if (bar != null && progress >= lastUpdate + 5) {
                    lastUpdate = (int) progress;
                    bar.setValue((int) progress);
                    bar.paint(bar.getGraphics());
                }
                double xCoord = xPos + (xInc * x);
                for (int y = 0; y < SIZE; y++) {
                    double yCoord = yPos + (yInc * y);
                    points[x][y] = new Coordinate(xCoord,
                                                  yCoord);

                    pixels[x][y] =
                    MandelbrotCalculator.getPixelColor(
                            MandelbrotCalculator.isMandelbrot(xCoord,
                                                              yCoord,
                                                              255));
                    /*
                    pixels[x][y] =
                    MandelbrotCalculator.getMaximumPrecisionColor(
                    MandelbrotCalculator.isMandelbrot(xCoord,
                    yCoord,
                    1530));
                     */
                }
            }
        }

        /**
         * Paints the current fractal view, and the square drawn by the user,
         * if any.
         * @param g the Graphics to paint on. 
         */
        //the "It" allows me to avoid overriding the paint method in JPanel
        public void paintIt(Graphics g) {
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    g.setColor(pixels[x][y]);
                    g.fillRect(x,
                               y,
                               1,
                               1);
                }
            }
            if (zoom != null) {
                g.setColor(Color.WHITE);
                g.drawRect(zoom.x,
                           zoom.y,
                           zoom.width,
                           zoom.height);

            }
        }

        /**
         * Calls the paintIt method
         * @param g 
         */
        @Override
        protected void paintComponent(Graphics g) {
            paintIt(g);
        }
    }

    /**
     * Responds to the zoom in button being pressed.
     */
    private void inButtonPressed() {
        //determines if the user has drawn a rectangle to zoom in to. if not,
        //prompts them to.
        if (zoom == null) {
            JOptionPane.showMessageDialog(this,
                                          "Please click and drag a rect on an area to zoom to first.");
            return;
        }
        //determines the fractal view indicated by the rectangle drawn by the  user
        double px1 = points[zoom.x][zoom.y].getX();
        double py1 = points[zoom.x][zoom.y].getY();
        int x2 = zoom.x + zoom.width;
        int y2 = zoom.y + zoom.height;
        double px2 = points[x2][y2].getX();
        double py2 = points[x2][y2].getY();
        double width = px2 - px1;
        double height = py2 - py1;
        zoom = null;

        /*
         * zooms to the area selected by the user.
         * adds the view area shown before the zoom to the stack of old views
         * then records the current view area
         */
        panel.makeView(px1,
                       py1,
                       width,
                       height);
        //adds the view area shown before the zoom to the stack of old views
        oldZooms.push(currentZoom);
        currentZoom = new ZoomData(px1,
                                   py1,
                                   width,
                                   height);
    }

    /**
     * Responds to the user pressing the zoom out button.
     */
    private void outButtonPressed() {
        //if the user has not zoomed in yet, displays an error message.
        if (oldZooms.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                                          "Can't zoom out further");
            return;
        }
        //makes the current view the view stored at the top of the stack of old views.
        ZoomData back = oldZooms.pop();
        currentZoom = back;
        panel.makeView(back.getLowerXBound(),
                       back.getLowerYBound(),
                       back.getWidth(),
                       back.getHeight());

    }

    //holds the components at the bottom of the frame
    private class ButtonPanel extends JPanel {

        private JButton inButton;
        private JButton outButton;

        /**
         * Cosntructs a new button panel.
         */
        public ButtonPanel() {
            this.setLayout(new GridLayout(3,
                                          1));
            inButton = new JButton("Zoom In");
            inButton.addActionListener(new InButtonActionListener());
            outButton = new JButton("Zoom Out");
            outButton.addActionListener(new OutButtonActionListener());
            bar = new JProgressBar(0,
                                   100);
            bar.setValue(0);
            bar.setStringPainted(true);
            this.add(inButton);
            this.add(outButton);
            this.add(bar);
        }

        //action listener for the zoom in button
        private class InButtonActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                inButtonPressed();
            }
        }

        //action listener for the zoom out button.
        private class OutButtonActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                outButtonPressed();
            }
        }
    }
}
