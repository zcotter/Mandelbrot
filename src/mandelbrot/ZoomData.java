package mandelbrot;

/**
 * Stores parameters required to recreate a view of the mandelbrot set.
 * @author Zach Cotter
 */
public class ZoomData {

    private double lowerXBound;
    private double lowerYBound;
    private double width;
    private double height;
    
    public ZoomData(double lowerXBound,
                    double lowerYBound,
                    double width,
                    double height) {
        this.lowerXBound = lowerXBound;
        this.lowerYBound = lowerYBound;
        this.width = width;
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public double getLowerXBound() {
        return lowerXBound;
    }

    public double getLowerYBound() {
        return lowerYBound;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setLowerXBound(double lowerXBound) {
        this.lowerXBound = lowerXBound;
    }

    public void setLowerYBound(double lowerYBound) {
        this.lowerYBound = lowerYBound;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}