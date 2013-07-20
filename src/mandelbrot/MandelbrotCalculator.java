package mandelbrot;

import java.awt.Color;

/**
 * Methods used with the mandelbrot fractal. According to the recursive formula
 * Z = Z^2 + C (where c and z are complex #s), C is in the mandelbrot set
 * if the magnitude of Z continues to be less than 2.
 * @author Zach Cotter
 */
public class MandelbrotCalculator {

    /**
     * Determines if the given coordinates can be excluded from the mandelbrot
     * set within the specified number of iterations of the algorithm.
     * @param a The a value of the complex number
     * @param b The b value of the complex number
     * @param iterations the specified number of iterations
     * @return The number of iterations it took to exclude the given number from
     * the set, or -1 if it is part of the set.
     */
    public static int isMandelbrot(double a,
                                   double b,
                                   int iterations) {
        double x = 0.0;
        double y = 0.0;
        double temp = 0.0;
        for (int i = 0; i < iterations; i++) {
            temp = x;
            x = (Math.pow(x,
                          2.0) - Math.pow(y,
                                          2.0)) + a;
            y = 2.0 * temp * y + b;
            if (!magnitudeLessThanTwo(x,
                                      y)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * determines if the given pair is within two units of the origin
     * @param x x coordinate
     * @param y y coordinate
     * @return whether the magnitude is less than 2.
     */
    public static boolean magnitudeLessThanTwo(double x,
                                               double y) {
        double magnitude = Math.sqrt(Math.pow(x,
                                              2.0) + Math.pow(y,
                                                              2.0));
        return magnitude <= 2.0;
    }

    /**
     * Generates a color for each pixel depending on the number of iterations
     * it to exclude it from the mandelbrot set. Uses 256 colors.
     * @param iterations the number of iterations it took to exclude a point 
     * from the mandelbrot set
     * @return A Color
     */
    public static Color getPixelColor(int iterations) {
        if (iterations == -1) {
            return Color.black;
        }
        if (iterations <= 42) {
            return new Color(255,
                             (int) ((double) iterations * (85.0 / 14.0)),
                             0);
        }
        if (iterations <= 84) {
            return new Color(
                    255 - (int) ((double) (iterations - 42) * (85.0 / 14.0)),
                    255,
                    0);
        }
        if (iterations <= 126) {
            return new Color(0,
                             255,
                             (int) ((double) (iterations - 84) * (85.0 / 14.0)));
        }
        if (iterations <= 168) {
            return new Color(0,
                             255 - (int) ((double) (iterations - 126) * (85.0 / 14.0)),
                             255);
        }
        if (iterations <= 210) {
            return new Color(
                    (int) ((double) (iterations - 168) * (85.0 / 14.0)),
                    0,
                    255);
        }
        if (iterations <= 255) {
            return new Color(255,
                             0,
                             255 - (int) ((double) (iterations - 210) * (17.0 / 3.0)));
        }
        else {
            return Color.black;
        }
    }

    /**
     * A higher definition, but lower efficiency version of getPixelColor().
     * Generates a color for each pixel depending on the number of iterations
     * it to exclude it from the mandelbrot set. Uses 1530 colors.
     * @param iterations the number of iterations it took to exclude a point 
     * from the mandelbrot set
     * @return A Color
     */
    public static Color getMaximumPrecisionColor(int color) {
        if (color == -1) {
            return Color.black;
        }
        if (color <= 255) {
            return new Color(255,
                             color,
                             0);
        }
        if (color <= 510) {
            return new Color(color - 255,
                             255,
                             0);
        }
        if (color <= 765) {
            return new Color(0,
                             255,
                             color - 510);
        }
        if (color <= 1020) {
            return new Color(0,
                             color - 765,
                             255);
        }
        if (color <= 1275) {
            return new Color(color - 1020,
                             0,
                             255);
        }
        if (color <= 1530) {
            return new Color(255,
                             0,
                             color - 1275);
        }
        else {
            return Color.black;
        }
    }
}